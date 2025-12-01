package interface_adapter.view_recipe;

import entity.Recipe;
import use_case.view_recipe.ViewRecipeInputBoundary;
import use_case.view_recipe.ViewRecipeInputData;

public class ViewRecipeController {
    final ViewRecipeInputBoundary viewRecipeInteractor;

    public ViewRecipeController(ViewRecipeInputBoundary viewRecipeInteractor) {
        this.viewRecipeInteractor = viewRecipeInteractor;
    }

    public void execute(Recipe recipe, String username) {
        ViewRecipeInputData viewRecipeInputData = new ViewRecipeInputData(recipe, username);
        viewRecipeInteractor.execute(viewRecipeInputData);
    }

    // Overloaded execute method to accept recipeId and username
    public void execute(String recipeId, String username) {
        ViewRecipeInputData viewRecipeInputData = new ViewRecipeInputData(recipeId, username);
        viewRecipeInteractor.execute(viewRecipeInputData);
    }
}
