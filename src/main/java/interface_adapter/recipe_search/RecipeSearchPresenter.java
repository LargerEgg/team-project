package interface_adapter.recipe_search;

import interface_adapter.ViewManagerModel;
import use_case.recipe_search.RecipeSearchOutputBoundary;
import use_case.recipe_search.RecipeSearchOutputData;

/**
 * The Presenter for the Recipe Search Use Case.
 */
public class RecipeSearchPresenter implements RecipeSearchOutputBoundary {
    private final RecipeSearchViewModel recipeSearchViewModel;
    private final ViewManagerModel viewManagerModel;

    public RecipeSearchPresenter(ViewManagerModel viewManagerModel,
                                 RecipeSearchViewModel recipeSearchViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.recipeSearchViewModel = recipeSearchViewModel;
    }

    @Override
    public void prepareSuccessView(RecipeSearchOutputData response) {
        // On success, update the state with the new recipe list
        // and then fire a property change to update the view.
        RecipeSearchState recipeSearchState = recipeSearchViewModel.getState();
        recipeSearchState.setRecipeList(response.getRecipeList());
        // The sort criteria are already in the state, so no need to set them here.
        // The view will re-sort based on the state's current sort criteria.
        recipeSearchViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        // On failure, update the state with the error message
        // and then fire a property change to update the view.
        RecipeSearchState recipeSearchState = recipeSearchViewModel.getState();
        recipeSearchState.setSearchError(error);
        recipeSearchViewModel.firePropertyChange();
    }
}
