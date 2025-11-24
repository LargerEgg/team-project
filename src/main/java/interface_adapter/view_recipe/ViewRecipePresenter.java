package interface_adapter.view_recipe;

import interface_adapter.ViewManagerModel;
import use_case.view_recipe.ViewRecipeOutputBoundary;
import use_case.view_recipe.ViewRecipeOutputData;

public class ViewRecipePresenter implements ViewRecipeOutputBoundary {
    private final ViewRecipeViewModel viewRecipeViewModel;
    private final ViewManagerModel viewManagerModel;

    public ViewRecipePresenter(ViewRecipeViewModel viewRecipeViewModel, ViewManagerModel viewManagerModel) {
        this.viewRecipeViewModel = viewRecipeViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(ViewRecipeOutputData outputData) {
        ViewRecipeState viewRecipeState = viewRecipeViewModel.getState();
        viewRecipeState.setRecipe(outputData.getRecipe());
        this.viewRecipeViewModel.setState(viewRecipeState);
        this.viewRecipeViewModel.firePropertyChange();

        this.viewManagerModel.setState(viewRecipeViewModel.getViewName()); // Corrected method call
        this.viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        ViewRecipeState viewRecipeState = viewRecipeViewModel.getState();
        viewRecipeState.setErrorMessage(error);
        viewRecipeViewModel.firePropertyChange();
    }
}
