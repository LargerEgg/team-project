package use_case.save_recipe;

import entity.Recipe;
import entity.User;

public class SaveRecipeInteractor implements SaveRecipeInputBoundary {

    private final SaveRecipeDataAccessInterface dataAccessObject;
    private final SaveRecipeOutputBoundary presenter;

    public SaveRecipeInteractor(SaveRecipeDataAccessInterface dataAccessObject,
                                SaveRecipeOutputBoundary presenter) {
        this.dataAccessObject = dataAccessObject;
        this.presenter = presenter;
    }

    @Override
    public void execute(SaveRecipeInputData input) {
        String username = input.getUsername();
        String recipeId = input.getRecipeId();

        User user = dataAccessObject.findUserByUsername(username);
        Recipe recipe = dataAccessObject.findRecipeById(recipeId);

        if (user == null) {
            presenter.prepareFailView("User not found: " + username);
            return;
        }
        if (recipe == null) {
            presenter.prepareFailView("Recipe not found: " + recipeId);
            return;
        }

        if (dataAccessObject.isRecipeSavedByUser(username, recipeId)) {
            presenter.prepareFailView("You have already saved this recipe.");
            return;
        }

        user.saveRecipe(recipe);
        recipe.incrementSaves();
        dataAccessObject.saveUser(user);
        dataAccessObject.saveRecipe(recipe);

        SaveRecipeOutputData outputData = new SaveRecipeOutputData(true, "Recipe saved successfully.");
        presenter.prepareSuccessView(outputData);
    }
}