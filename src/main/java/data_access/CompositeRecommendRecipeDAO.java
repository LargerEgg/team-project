package data_access;

import entity.Recipe;
import use_case.recommend_recipe.RecommendRecipeDataAccessInterface;
import use_case.saved_recipes.ShowSavedRecipesDataAccessInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * A composite Data Access Object for the Recommend Recipe use case.
 * It combines:
 * - Firebase (via ShowSavedRecipesDataAccessInterface) for getting user's saved recipes
 * - MealDB API (via RecommendRecipeDataAccessObject) for getting recommended recipes by category
 */
public class CompositeRecommendRecipeDAO implements RecommendRecipeDataAccessInterface {

    private final ShowSavedRecipesDataAccessInterface savedRecipesDAO;
    private final RecommendRecipeDataAccessObject apiDAO;

    public CompositeRecommendRecipeDAO(ShowSavedRecipesDataAccessInterface savedRecipesDAO,
                                       RecommendRecipeDataAccessObject apiDAO) {
        this.savedRecipesDAO = savedRecipesDAO;
        this.apiDAO = apiDAO;
    }

    /**
     * Get the user's saved recipes from Firebase.
     *
     * @param username the username of the current user
     * @return the list of saved recipes for the user
     */
    @Override
    public List<Recipe> getSavedRecipes(String username) {
        if (username == null || username.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<Recipe> recipes = savedRecipesDAO.getRecipes(username);
            return recipes != null ? recipes : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error fetching saved recipes for user " + username + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get recommended recipes by category from MealDB API.
     *
     * @param category the category name derived from user preferences
     * @return a list of recipes matching the category
     */
    @Override
    public List<Recipe> getRecipesByCategory(String category) {
        return apiDAO.getRecipesByCategory(category);
    }
}

