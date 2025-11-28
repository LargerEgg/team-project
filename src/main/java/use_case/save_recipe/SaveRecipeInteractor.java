package use_case.save_recipe;

import entity.Recipe;

public class SaveRecipeInteractor implements SaveRecipeInputBoundary {

    private final SaveRecipeDataAccessInterface recipeDao;
    private final SaveRecipeOutputBoundary presenter;

    public SaveRecipeInteractor(SaveRecipeDataAccessInterface recipeDao, SaveRecipeOutputBoundary presenter) {
        this.recipeDao = recipeDao;
        this.presenter = presenter;
    }

    @Override
    public void execute(SaveRecipeInputData inputData) {
        String username = inputData.getUsername();
        Recipe recipe = inputData.getRecipe();

        try {

            boolean alreadySaved = recipeDao.isRecipeSaved(username, recipe);

            if (alreadySaved) {
                recipeDao.unsaveRecipe(username, recipe);

                SaveRecipeOutputData output = new SaveRecipeOutputData(recipe.getTitle());
                presenter.prepareSuccess(output);
            } else {
                recipeDao.saveRecipe(username, recipe);

                SaveRecipeOutputData output = new SaveRecipeOutputData(recipe.getTitle());
                presenter.prepareSuccess(output);
            }

        } catch (Exception e) {
            presenter.prepareFailure("Failed to save recipe: " + e.getMessage());
        }

    }

}
