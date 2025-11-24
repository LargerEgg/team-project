package interface_adapter.view_recipe;

import use_case.view_recipe.ViewRecipeInputBoundary;
import use_case.view_recipe.ViewRecipeInputData;

public class ViewRecipeController {

    private final ViewRecipeInputBoundary viewRecipeUseCaseInteractor;

    public ViewRecipeController(ViewRecipeInputBoundary viewRecipeUseCaseInteractor) {
        this.viewRecipeUseCaseInteractor = viewRecipeUseCaseInteractor;
    }

    public void execute(String recipeId) {
        final ViewRecipeInputData viewRecipeInputData = new ViewRecipeInputData(recipeId);
        viewRecipeUseCaseInteractor.execute(viewRecipeInputData);
    }
}

