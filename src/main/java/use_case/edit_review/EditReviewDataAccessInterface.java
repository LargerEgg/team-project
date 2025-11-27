package use_case.edit_review;

import entity.Recipe;
import entity.User;

public interface EditReviewDataAccessInterface {
    User findUserByUsername(String username);
    Recipe findRecipeById(String recipeId);

    boolean isRecipeSavedByUser(String username, String recipeId);

    void saveUser(User user);
    void saveRecipe(Recipe recipe);
}
