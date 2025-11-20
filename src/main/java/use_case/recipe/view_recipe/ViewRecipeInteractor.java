package use_case.recipe.view_recipe;

import entity.Recipe;
import use_case.recipe.RecipeDataAccessInterface;

public class ViewRecipeInteractor implements ViewRecipeInputBoundary {

    private final RecipeDataAccessInterface repo;
    private final ViewRecipeOutputBoundary presenter;

    public ViewRecipeInteractor(RecipeDataAccessInterface repo,
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
