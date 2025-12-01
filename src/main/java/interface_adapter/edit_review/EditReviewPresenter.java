package interface_adapter.edit_review;

import interface_adapter.ViewManagerModel;
import interface_adapter.view_recipe.ViewRecipeController;
import interface_adapter.view_recipe.ViewRecipeViewModel;
import use_case.edit_review.EditReviewInputData;
import use_case.edit_review.EditReviewOutputBoundary;
import use_case.edit_review.EditReviewOutputData;

/**
 * The Presenter for the edit reviews Use Case.
 */
public class EditReviewPresenter implements EditReviewOutputBoundary {

    private final EditReviewViewModel editReviewViewModel;
    private final ViewManagerModel viewManagerModel;
    private final ViewRecipeViewModel viewRecipeViewModel;
    private final ViewRecipeController viewRecipeController;

    public EditReviewPresenter(ViewManagerModel viewManagerModel,
                           EditReviewViewModel editReviewViewModel,
                           ViewRecipeViewModel viewRecipeViewModel,
                           ViewRecipeController viewRecipeController) {
        this.viewManagerModel = viewManagerModel;
        this.editReviewViewModel = editReviewViewModel;
        this.viewRecipeViewModel = viewRecipeViewModel;
        this.viewRecipeController = viewRecipeController;
    }

    @Override
    public void prepareSuccessView(EditReviewOutputData outputData) {
        final EditReviewState state = editReviewViewModel.getState();
        state.setSuccessMessage(outputData.getMessage() + " (ID: " + outputData.getReviewId() + ")");
        state.setErrorMessage("");

        clearForm(state);

        editReviewViewModel.firePropertyChange("success");

        // Refresh the ViewRecipeView
        String recipeId = state.getCurrentRecipe();
        String currentUser = state.getCurrentUser();

        // Call the ViewRecipeController to re-fetch and display the updated recipe
        viewRecipeController.execute(recipeId, currentUser); // This will be the new signature
        viewManagerModel.setState(viewRecipeViewModel.getViewName()); // Corrected method call
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage, EditReviewInputData inputData) {
        final EditReviewState state = editReviewViewModel.getState();
        state.setErrorMessage(errorMessage);
        state.setSuccessMessage("");

        state.setReview(inputData.getReview());
        state.setDescription(inputData.getDescription());
        state.setRating(inputData.getRating());
        state.setAuthorId(inputData.getAuthorId());
        state.setRecipeId(inputData.getRecipeId());

        editReviewViewModel.firePropertyChange("error");
    }

    private void clearForm(EditReviewState state) {
        state.setReview("");
        state.setDescription("");
        state.setRating(5);
    }
}
