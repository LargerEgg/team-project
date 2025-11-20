package use_case.view_recipe;

import entity.Recipe;
import use_case.view_recipe.ViewRecipeDataAccessInterface;

public class ViewRecipeInteractor implements ViewRecipeInputBoundary {

    private final ViewRecipeDataAccessInterface repo;
    private final ViewRecipeOutputBoundary presenter;

    public ViewRecipeInteractor(ViewRecipeDataAccessInterface repo,
                                ViewRecipeOutputBoundary presenter) {
        this.repo = repo;
        this.presenter = presenter;
    }

    @Override
    public ViewRecipeOutputData execute(ViewRecipeInputData input) {

        Recipe recipe = repo.findById(input.getRecipeId());

        recipe.incrementViews();
        repo.save(recipe);

        return presenter.prepareSuccess(new ViewRecipeOutputData(
                recipe.getTitle(),
                recipe.getRecipeId(),
                recipe.getViews(),
                recipe.getSaves(),
                recipe.getAverageRating()
        ));
    }
}
