package interface_adapter.view_recipe;

import interface_adapter.ViewManagerModel;
import kotlin.jvm.internal.StringCompanionObject;
import use_case.view_recipe.ViewRecipeOutputBoundary;
import use_case.view_recipe.ViewRecipeOutputData;

public class ViewRecipePresenter implements ViewRecipeOutputBoundary {
    private final ViewRecipeViewModel viewRecipeViewModel;
    private final ViewManagerModel viewManagerModel;
    private final String currentUser;

    public ViewRecipePresenter(ViewRecipeViewModel viewRecipeViewModel, ViewManagerModel viewManagerModel, String  currentUser) {
        this.viewRecipeViewModel = viewRecipeViewModel;
        this.viewManagerModel = viewManagerModel;
        this.currentUser = currentUser;
    }

    @Override
    public void prepareSuccessView(ViewRecipeOutputData outputData) {
        ViewRecipeState viewRecipeState = viewRecipeViewModel.getState();
        viewRecipeState.setRecipe(outputData.getRecipe());
        viewRecipeState.setIsSaved(outputData.isSaved());
        viewRecipeState.setCurrentUser(outputData.getUsername());
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
