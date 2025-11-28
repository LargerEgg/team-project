package data_access;

import entity.Recipe;
import use_case.recipe_search.RecipeSearchOutputBoundary;
import use_case.recipe_search.RecipeSearchRecipeDataAccessInterface;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompositeRecipeSearchDAO implements RecipeSearchRecipeDataAccessInterface {

    private final RecipeDataAccessObject apiDAO;
    private final FirebaseRecipeDataAccessObject firebaseDAO;

    public CompositeRecipeSearchDAO(RecipeDataAccessObject apiDAO, FirebaseRecipeDataAccessObject firebaseDAO) {
        this.apiDAO = apiDAO;
        this.firebaseDAO = firebaseDAO;
    }

    @Override
    public List<String> getAllCategories() {
        List<String> apiCategories = apiDAO.getAllCategories();
        List<String> firebaseCategories = firebaseDAO.getAllCategories();

        return Stream.concat(apiCategories.stream(), firebaseCategories.stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Recipe> search(String name, String category, RecipeSearchOutputBoundary presenter) {
        // 1. Get recipes from the API
        List<Recipe> apiRecipes = apiDAO.search(name, category, presenter);

        // 2. Get recipes from Firebase
        List<Recipe> firebaseRecipes;
        if (category != null && !category.isEmpty()) {
            firebaseRecipes = firebaseDAO.findByCategory(category, name);
        } else {
            firebaseRecipes = firebaseDAO.search(name, null);
        }

        // 3. Combine and remove duplicates
        List<Recipe> combinedRecipes = Stream.concat(apiRecipes.stream(), firebaseRecipes.stream())
                .distinct()
                .collect(Collectors.toList());

        // 4. Asynchronously fetch all view counts from Firebase
        if (firebaseDAO != null && combinedRecipes != null && !combinedRecipes.isEmpty()) {
            List<CompletableFuture<Void>> futures = combinedRecipes.stream()
                    .map(recipe -> firebaseDAO.getViewCount(recipe.getRecipeId())
                            .thenAccept(recipe::setViews))
                    .collect(Collectors.toList());

            // 5. Wait for all the view count fetches to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        return combinedRecipes;
    }
}
