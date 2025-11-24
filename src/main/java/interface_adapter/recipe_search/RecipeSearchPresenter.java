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
        RecipeSearchState recipeSearchState = recipeSearchViewModel.getState();
        recipeSearchState.setRecipeList(response.getRecipes());
        recipeSearchState.setCurrentImageCount(0); // Reset progress
        recipeSearchState.setTotalImageCount(0); // Reset progress
        recipeSearchViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        RecipeSearchState recipeSearchState = recipeSearchViewModel.getState();
        recipeSearchState.setSearchError(error);
        recipeSearchState.setCurrentImageCount(0); // Reset progress
        recipeSearchState.setTotalImageCount(0); // Reset progress
        recipeSearchViewModel.firePropertyChange();
    }

    @Override
    public void prepareProgressView(RecipeSearchOutputData progressData) {
        RecipeSearchState recipeSearchState = recipeSearchViewModel.getState();
        recipeSearchState.setCurrentImageCount(progressData.getCurrentImageCount());
        recipeSearchState.setTotalImageCount(progressData.getTotalImageCount());
        recipeSearchViewModel.firePropertyChange("progress"); // Fire a specific property change for progress
    }
}
