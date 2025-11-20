package use_case.view_recipe;

import entity.Recipe;

public interface ViewRecipeDataAccessInterface {

    Recipe findById(String recipeId);

    void save(Recipe recipe);
}