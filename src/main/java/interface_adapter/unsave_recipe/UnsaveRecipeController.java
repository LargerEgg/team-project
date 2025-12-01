package interface_adapter.unsave_recipe;

import entity.Recipe;
import use_case.unsave_recipe.UnsaveRecipeInputBoundary;

public class UnsaveRecipeController {

    private final UnsaveRecipeInputBoundary interactor;

    public UnsaveRecipeController(UnsaveRecipeInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String username, Recipe recipe) {
        interactor.execute(username, recipe.getRecipeId());
    }
}
