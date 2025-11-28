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

    public EditReviewInteractor(ReviewDataAccessObject editReviewDataAccessInterface,
                                EditReviewOutputBoundary editReviewOutputBoundary,
                                Review review) {
        this.reviewDataAccessObject = editReviewDataAccessInterface;
        this.reviewPresenter = editReviewOutputBoundary;
    }

    @Override
    public void publish(EditReviewInputData inputData) {
        String authorId = inputData.getAuthorId();
        String recipeId = inputData.getRecipeId();
        Date now = new Date();
        String reviewId = UUID.randomUUID().toString();
        String title = inputData.getReview();
        String description = inputData.getDescription();
        int rating = inputData.getRating();

        if (title.isBlank()) {
            reviewPresenter.prepareFailView("Title cannot be empty.");
            return;
        }
        if (description.isBlank()) {
            reviewPresenter.prepareFailView("Description cannot be empty.");
            return;
        }

        try {
            final Review review = new Review(reviewId, recipeId, authorId, now, title, description, rating);
            reviewDataAccessObject.save(review);
            EditReviewOutputData outputData = new EditReviewOutputData(reviewId, "Review published.");
            reviewPresenter.prepareSuccessView(outputData);
        } catch (RuntimeException e) {
            reviewPresenter.prepareFailView("Failed to publish review");
        }
    }
}
