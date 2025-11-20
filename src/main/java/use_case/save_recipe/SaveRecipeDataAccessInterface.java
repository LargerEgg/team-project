package use_case.save_recipe;

import entity.Recipe;
import entity.User;

public interface SaveRecipeDataAccessInterface {

    User findUserByUsername(String username);
    Recipe findRecipeById(String recipeId);

    boolean isRecipeSavedByUser(String username, String recipeId);

    void saveUser(User user);
    void saveRecipe(Recipe recipe);
}
