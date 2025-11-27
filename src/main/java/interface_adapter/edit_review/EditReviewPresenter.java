package interface_adapter.edit_review;

import interface_adapter.ViewManagerModel;
import interface_adapter.reviews.ReviewsState;
import interface_adapter.reviews.ReviewsViewModel;
import use_case.edit_review.EditReviewOutputBoundary;
import use_case.edit_review.EditReviewOutputData;

/**
 * The Presenter for the edit reviews Use Case.
 */
public class EditReviewPresenter implements EditReviewOutputBoundary {

    private final EditReviewViewModel editReviewViewModel;
    private final ReviewsViewModel reviewsViewModel;
    private final ViewManagerModel viewManagerModel;

    public EditReviewPresenter(ViewManagerModel viewManagerModel,
                           EditReviewViewModel editReviewViewModel,
                           ReviewsViewModel reviewsViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.editReviewViewModel = editReviewViewModel;
        this.reviewsViewModel = reviewsViewModel;
    }

    @Override
    public void prepareSuccessView(EditReviewOutputData response) {
        // On success, switch to the login view.
        final ReviewsState reviewState = reviewsViewModel.getState();
        reviewState.setReview(response.getMessage());
        reviewsViewModel.firePropertyChange();

        viewManagerModel.setState(reviewsViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        final EditReviewState editReviewState = editReviewViewModel.getState();
        editReviewState.setReviewError(error);
        editReviewViewModel.firePropertyChange();
    }

    @Override
    public void switchToReviewsView() {
        viewManagerModel.setState(reviewsViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}