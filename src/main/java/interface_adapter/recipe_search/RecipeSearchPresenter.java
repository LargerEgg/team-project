package interface_adapter.recipe_search;

import interface_adapter.ViewManagerModel;
import use_case.recipe_search.RecipeSearchOutputBoundary;
import use_case.recipe_search.RecipeSearchOutputData;
import view.RecipeSearchView;

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
        final RecipeSearchState recipeSearchState = recipeSearchViewModel.getState();
        recipeSearchState.setRecipeList(response.getRecipeList());
        recipeSearchViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        final RecipeSearchState recipeSearchState = recipeSearchViewModel.getState();
        recipeSearchState.setSearchError(error);
        recipeSearchViewModel.firePropertyChange();
    }
}
