package interface_adapter.saved_recipes;

import interface_adapter.recipe_search.RecipeSearchState;
import interface_adapter.recipe_search.RecipeSearchViewModel;
import use_case.saved_recipes.ShowSavedRecipesOutputBoundary;
import use_case.saved_recipes.ShowSavedRecipesOutputData;

public class SavedRecipesPresenter implements ShowSavedRecipesOutputBoundary {

    private final RecipeSearchViewModel recipeSearchViewModel;

    public SavedRecipesPresenter(RecipeSearchViewModel recipeSearchViewModel) {
        this.recipeSearchViewModel = recipeSearchViewModel;
    }

    @Override
    public void prepareSuccess(ShowSavedRecipesOutputData data) {
        RecipeSearchState state = recipeSearchViewModel.getState();
        state.setRecipeList(data.getSaved_recipes());
        state.setSearchError(null);

        recipeSearchViewModel.setState(state);
        recipeSearchViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailure(String message) {
        RecipeSearchState state = recipeSearchViewModel.getState();
        state.setSearchError(message);

        recipeSearchViewModel.setState(state);
        recipeSearchViewModel.firePropertyChange();
    }
}
