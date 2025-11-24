package interface_adapter.view_recipe;

import use_case.view_recipe.ViewRecipeInputBoundary;
import use_case.view_recipe.ViewRecipeInputData;

public class ViewRecipeController {
    final ViewRecipeInputBoundary viewRecipeInteractor;

    public ViewRecipeController(ViewRecipeInputBoundary viewRecipeInteractor) {
        this.viewRecipeInteractor = viewRecipeInteractor;
    }

    public void execute(String recipeId) {
        ViewRecipeInputData viewRecipeInputData = new ViewRecipeInputData(recipeId);
        viewRecipeInteractor.execute(viewRecipeInputData);
    }
}
