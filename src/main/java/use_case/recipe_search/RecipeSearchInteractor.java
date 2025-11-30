package use_case.recipe_search;

import entity.Recipe;

import java.util.List;

public class RecipeSearchInteractor implements RecipeSearchInputBoundary {
    private final RecipeSearchRecipeDataAccessInterface recipeSearchRecipeDataAccessInterface;
    private final RecipeSearchOutputBoundary recipeSearchPresenter;

    public RecipeSearchInteractor(RecipeSearchRecipeDataAccessInterface recipeSearchRecipeDataAccessInterface,
                                    RecipeSearchOutputBoundary recipeSearchPresenter) {
        this.recipeSearchRecipeDataAccessInterface = recipeSearchRecipeDataAccessInterface;
        this.recipeSearchPresenter = recipeSearchPresenter;
    }

    @Override
    public void execute(RecipeSearchInputData recipeSearchInputData) {
        try {
            List<Recipe> recipes = recipeSearchRecipeDataAccessInterface.search(recipeSearchInputData.getQuery());
            RecipeSearchOutputData recipeSearchOutputData = new RecipeSearchOutputData(recipes);
            recipeSearchPresenter.prepareSuccessView(recipeSearchOutputData);
        } catch (RuntimeException e) {
            recipeSearchPresenter.prepareFailView(e.getMessage());
        }
    }
}
