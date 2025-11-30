package interface_adapter.edit_review;

import interface_adapter.ViewManagerModel;
import use_case.edit_review.EditReviewInputData;
import use_case.edit_review.EditReviewOutputBoundary;
import use_case.edit_review.EditReviewOutputData;

/**
 * The Presenter for the edit reviews Use Case.
 */
public class EditReviewPresenter implements EditReviewOutputBoundary {

    private final EditReviewViewModel editReviewViewModel;
    private final ViewManagerModel viewManagerModel;

    public EditReviewPresenter(ViewManagerModel viewManagerModel,
                           EditReviewViewModel editReviewViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.editReviewViewModel = editReviewViewModel;
    }

    @Override
    public void prepareSuccessView(EditReviewOutputData outputData) {
        final EditReviewState state = editReviewViewModel.getState();
        state.setSuccessMessage(outputData.getMessage() + " (ID: " + outputData.getReviewId() + ")");
        state.setErrorMessage("");

        clearForm(state);

        editReviewViewModel.firePropertyChange("success");
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