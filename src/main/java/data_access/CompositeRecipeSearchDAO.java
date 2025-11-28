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

        // 2. Asynchronously fetch all view counts from Firebase
        if (firebaseDAO != null && recipes != null && !recipes.isEmpty()) {
            List<CompletableFuture<Void>> futures = recipes.stream()
                    .map(recipe -> firebaseDAO.getViewCount(recipe.getRecipeId())
                            .thenAccept(recipe::setViews))
                    .collect(Collectors.toList());

            // 3. Wait for all the view count fetches to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        return recipes;
    }
}
