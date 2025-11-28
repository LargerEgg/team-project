package interface_adapter.save_recipe;

import entity.Recipe;
import use_case.save_recipe.SaveRecipeInputBoundary;
import use_case.save_recipe.SaveRecipeInputData;

public class SaveRecipeController {

    private final SaveRecipeInputBoundary interactor;

    public SaveRecipeController(SaveRecipeInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String username, Recipe recipe) {
        SaveRecipeInputData inputData = new SaveRecipeInputData(username, recipe);
        interactor.execute(inputData);
    }

}
