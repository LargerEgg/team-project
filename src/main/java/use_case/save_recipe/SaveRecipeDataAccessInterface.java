package use_case.save_recipe;

import entity.Recipe;

public interface SaveRecipeDataAccessInterface {
    boolean isRecipeSaved(String username,  String recipeID);
    void saveRecipe(String username, String recipeID);
    void unsaveRecipe(String username, String recipeID);
}
