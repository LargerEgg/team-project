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

        Recipe recipe = repo.findById(input.getRecipeId());

        if (recipe == null) {
            presenter.prepareFailView("Recipe not found with ID: " + input.getRecipeId());
            return;
        }

        recipe.incrementViews();
        repo.save(recipe);

        // Changed to pass the full Recipe object
        ViewRecipeOutputData outputData = new ViewRecipeOutputData(recipe);
        presenter.prepareSuccessView(outputData);
    }
}
