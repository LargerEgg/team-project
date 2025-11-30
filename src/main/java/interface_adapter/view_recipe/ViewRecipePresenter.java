package interface_adapter.view_recipe;

import interface_adapter.ViewManagerModel;
import interface_adapter.edit_review.EditReviewState;
import interface_adapter.edit_review.EditReviewViewModel;
import use_case.view_recipe.ViewRecipeOutputBoundary;
import use_case.view_recipe.ViewRecipeOutputData;

public class ViewRecipePresenter implements ViewRecipeOutputBoundary {
    private final ViewRecipeViewModel viewRecipeViewModel;
    private final ViewManagerModel viewManagerModel;
    private final EditReviewViewModel editReviewViewModel;

    public ViewRecipePresenter(ViewRecipeViewModel viewRecipeViewModel, ViewManagerModel viewManagerModel, EditReviewViewModel editReviewViewModel) {
        this.viewRecipeViewModel = viewRecipeViewModel;
        this.viewManagerModel = viewManagerModel;
        this.editReviewViewModel = editReviewViewModel;
    }

    @Override
    public void prepareSuccessView(ViewRecipeOutputData outputData) {

        final EditReviewState loggedInReviewState = editReviewViewModel.getState();
        loggedInReviewState.setCurrentRecipe(outputData.getRecipe().getRecipeId());
        this.editReviewViewModel.firePropertyChange();

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
