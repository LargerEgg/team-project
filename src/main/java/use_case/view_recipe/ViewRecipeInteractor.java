package use_case.view_recipe;

import entity.Recipe;

public class ViewRecipeInteractor implements ViewRecipeInputBoundary {

    private final ViewRecipeDataAccessInterface repo;
    private final ViewRecipeOutputBoundary presenter;

    public ViewRecipeInteractor(ViewRecipeDataAccessInterface repo,
                                ViewRecipeOutputBoundary presenter) {
        this.repo = repo;
        this.presenter = presenter;
    }

    @Override
    public void execute(ViewRecipeInputData input) {

        Recipe recipe = input.getRecipe();

        if (recipe == null) {
            presenter.prepareFailView("Recipe not found.");
            return;
        }

        // Run the database write operation on a separate thread to avoid blocking the UI
        new Thread(() -> {
            try {
                repo.recordView(recipe.getRecipeId());
            } catch (Exception e) {
                // In a real application, you'd want to log this error
                e.printStackTrace();
            }
        }).start();

        // Prepare the success view immediately, without waiting for the database
        ViewRecipeOutputData outputData = new ViewRecipeOutputData(recipe);
        presenter.prepareSuccessView(outputData);
    }
}
