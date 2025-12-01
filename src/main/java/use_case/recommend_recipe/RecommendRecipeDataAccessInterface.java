package use_case.recommend_recipe;

import entity.User;
import entity.Recipe;
import java.util.List;

public interface RecommendRecipeDataAccessInterface {

    /**
     * Retrieve the user object by username.
     * This is necessary for the Interactor to access the user's favorite recipes.
     * * @param username the username of the current user
     * @return the User object containing their favorites
     */
    User getUser(String username);

    /**
     * Search for recipes based on a specific category (e.g., "Italian", "Vegan").
     * This is used to fetch new recommendations from the external API.
     * * @param category the category name derived from user preferences
     * @return a list of recipes matching the category
     */
    List<Recipe> getRecipesByCategory(String category);

}