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
    public SaveRecipeOutputData execute(SaveRecipeInputData input) {
        String username = input.getUsername();
        String recipeId = input.getRecipeId();

        User user = dataAccessObject.findUserByUsername(username);
        Recipe recipe = dataAccessObject.findRecipeById(recipeId);

        if (user == null) {
            return presenter.prepareFailView("User not found: " + username);
        }
        if (recipe == null) {
            return presenter.prepareFailView("Recipe not found: " + recipeId);
        }

        if (dataAccessObject.isRecipeSavedByUser(username, recipeId)) {
            return presenter.prepareFailView("You have already saved this recipe.");
        }

        user.saveRecipe(recipe);
        recipe.incrementSaves();
        dataAccessObject.saveUser(user);
        dataAccessObject.saveRecipe(recipe);

        SaveRecipeOutputData outputData = new SaveRecipeOutputData(true, "Recipe saved successfully.");
        return presenter.prepareSuccessView(outputData);
    }
}