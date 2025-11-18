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

        recipe.incrementViews();      // ⭐views +1
        repo.save(recipe);            // 保存更新

        return presenter.prepareSuccess(new ViewRecipeOutputData(
                recipe.getViews(),
                recipe.getRecipeId()
        ));
    }
}
