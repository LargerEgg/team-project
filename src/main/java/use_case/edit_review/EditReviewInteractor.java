package use_case.edit_review;

import entity.Review;

/**
 * The Edit Review Interactor.
 */
public class EditReviewInteractor implements EditReviewInputBoundary {
    private final EditReviewDataAccessInterface reviewDataAccessObject;
    private final EditReviewOutputBoundary reviewPresenter;
    private final Review review;

    public EditReviewInteractor(EditReviewDataAccessInterface editReviewDataAccessInterface,
                            EditReviewOutputBoundary editReviewOutputBoundary,
                            Review review) {
        this.reviewDataAccessObject = editReviewDataAccessInterface;
        this.reviewPresenter = editReviewOutputBoundary;
        this.review = review;
    }

    @Override
    public void execute(EditReviewInputData editReviewInputData) {
        if (reviewDataAccessObject.existsByName(editReviewInputData.getReview())) {
            reviewPresenter.prepareFailView("User already exists.");
        }
        else if (!editReviewInputData.getPassword().equals(editReviewInputData.getRepeatPassword())) {
            reviewPresenter.prepareFailView("Passwords don't match.");
        }
        else if ("".equals(editReviewInputData.getReview())) {
            reviewPresenter.prepareFailView("Title cannot be empty.");
        }
        else if ("".equals(editReviewInputData.getDescription())) {
            reviewPresenter.prepareFailView("Description cannot be empty.");
        }
        else {
            final Review review = new Review(editReviewInputData.getReview(), editReviewInputData.getDescription(),
                    editReviewInputData.getRating(), );
            reviewDataAccessObject.save(review);

            final EditReviewOutputData editReviewOutputData = new EditReviewOutputData(review.getTitle());
            reviewPresenter.prepareSuccessView(editReviewOutputData);
        }
    }

    @Override
    public void switchToReviewsView() {
        reviewPresenter.switchToReviewsView();
    }
}
