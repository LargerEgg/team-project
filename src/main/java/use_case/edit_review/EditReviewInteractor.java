package use_case.edit_review;

import entity.Recipe;
import entity.Review;
import entity.User;
import interface_adapter.edit_review.EditReviewDataAccessInterface;
import data_access.ReviewDataAccessObject;

import java.util.Date;
import java.util.UUID;

/**
 * The Edit Review Interactor.
 */
public class EditReviewInteractor implements EditReviewInputBoundary {
    private final EditReviewDataAccessInterface reviewDataAccessObject;
    private final EditReviewOutputBoundary reviewPresenter;
    private final Review review;

    public EditReviewInteractor(ReviewDataAccessObject editReviewDataAccessInterface,
                            EditReviewOutputBoundary editReviewOutputBoundary,
                            Review review) {
        this.reviewDataAccessObject = editReviewDataAccessInterface;
        this.reviewPresenter = editReviewOutputBoundary;
        this.review = review;
    }

    @Override
    public void execute(EditReviewInputData editReviewInputData) {
        String authorId = editReviewInputData.getAuthorId();
        String recipeId = editReviewInputData.getRecipeId();
        Date now = new Date();
        String reviewId = UUID.randomUUID().toString();
        String title = editReviewInputData.getReview();
        String description = editReviewInputData.getDescription();
        int rating = editReviewInputData.getRating();

        if ("".equals(editReviewInputData.getReview())) {
            reviewPresenter.prepareFailView("Title cannot be empty.");
        }
        else if ("".equals(editReviewInputData.getDescription())) {
            reviewPresenter.prepareFailView("Description cannot be empty.");
        }
        else {
            try {
                final Review review = new Review(reviewId, recipeId, authorId, now, title, description, rating);
                reviewDataAccessObject.save(review);
                EditReviewOutputData outputData = new EditReviewOutputData("Review published.");
                reviewPresenter.prepareSuccessView(outputData);
            } catch (RuntimeException e) {
                reviewPresenter.prepareFailView("Failed to publish review");
            }

        }
    }

    @Override
    public void switchToReviewsView() {
        reviewPresenter.switchToReviewsView();
    }
}
