package interface_adapter.view_recipe;

import entity.Recipe;
import use_case.view_recipe.ViewRecipeInputBoundary;
import use_case.view_recipe.ViewRecipeInputData;

public class ViewRecipeController {
    final ViewRecipeInputBoundary viewRecipeInteractor;

    public ViewRecipeController(ViewRecipeInputBoundary viewRecipeInteractor) {
        this.viewRecipeInteractor = viewRecipeInteractor;
    }

    public void execute(Recipe recipe) {
        ViewRecipeInputData viewRecipeInputData = new ViewRecipeInputData(recipe);
        viewRecipeInteractor.execute(viewRecipeInputData);
    }
}
