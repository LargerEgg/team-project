package data_access;

import entity.Recipe;
import use_case.recipe_search.RecipeSearchOutputBoundary;
import use_case.recipe_search.RecipeSearchRecipeDataAccessInterface;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CompositeRecipeSearchDAO implements RecipeSearchRecipeDataAccessInterface {

    private final RecipeDataAccessObject apiDAO;
    private final FirebaseRecipeDataAccessObject firebaseDAO;

    public CompositeRecipeSearchDAO(RecipeDataAccessObject apiDAO, FirebaseRecipeDataAccessObject firebaseDAO) {
        this.apiDAO = apiDAO;
        this.firebaseDAO = firebaseDAO;
    }

    @Override
    public List<String> getAllCategories() {
        return apiDAO.getAllCategories();
    }

    @Override
    public List<Recipe> search(String name, String category, RecipeSearchOutputBoundary presenter) {
        // 1. Get recipes from the API
        List<Recipe> recipes = apiDAO.search(name, category, presenter);

        // 2. Asynchronously fetch popularity data (views, saves, averageRating) from Firebase
        if (firebaseDAO != null && recipes != null && !recipes.isEmpty()) {
            List<CompletableFuture<Void>> futures = recipes.stream()
                    .flatMap(recipe -> {
                        String recipeId = recipe.getRecipeId();
                        // Create futures for views, saves, and averageRating
                        CompletableFuture<Void> viewsFuture = firebaseDAO.getViewCount(recipeId)
                                .thenAccept(recipe::setViews);
                        CompletableFuture<Void> savesFuture = firebaseDAO.getSaveCount(recipeId)
                                .thenAccept(recipe::setSaves);
                        CompletableFuture<Void> ratingFuture = firebaseDAO.getAverageRating(recipeId)
                                .thenAccept(recipe::setAverageRating);
                        return java.util.stream.Stream.of(viewsFuture, savesFuture, ratingFuture);
                    })
                    .collect(Collectors.toList());

            // 3. Wait for all the fetches to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        return recipes;
    }
}
