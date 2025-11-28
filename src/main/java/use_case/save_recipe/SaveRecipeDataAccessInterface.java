package use_case.save_recipe;

import entity.Recipe;

public interface SaveRecipeDataAccessInterface {
    boolean isRecipeSaved(String username,  Recipe recipe);
    void saveRecipe(String username, Recipe recipe);
    void unsaveRecipe(String username, Recipe recipe);
}
