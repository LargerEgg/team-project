package use_case.edit_review;

import entity.Review;
import use_case.edit_review.EditReviewDataAccessInterface;
import data_access.FirebaseReviewDataAccessObject;

import java.util.Date;
import java.util.UUID;

/**
 * The Edit Review Interactor.
 */
public class EditReviewInteractor implements EditReviewInputBoundary {
    private final EditReviewDataAccessInterface reviewDataAccessObject;
    private final EditReviewOutputBoundary reviewPresenter;

    public EditReviewInteractor(EditReviewDataAccessInterface editReviewDataAccessInterface,
                                EditReviewOutputBoundary editReviewOutputBoundary) {
        this.reviewDataAccessObject = editReviewDataAccessInterface;
        this.reviewPresenter = editReviewOutputBoundary;
    }

    @Override
    public void publish(EditReviewInputData inputData) {

        if (inputData.getAuthorId() == null || inputData.getAuthorId().isBlank()) {
            reviewPresenter.prepareFailView("You must be logged in to publish a review", inputData);
            return;
        }
        if (inputData.getReview().isBlank()) {
            reviewPresenter.prepareFailView("Title cannot be empty.", inputData);
            return;
        }
        if (inputData.getDescription().isBlank()) {
            reviewPresenter.prepareFailView("Description cannot be empty.", inputData);
            return;
        }

        String reviewId = UUID.randomUUID().toString();
        final Review review = new Review(reviewId, inputData.getRecipeId(), inputData.getAuthorId(),
                new Date(), inputData.getReview(), inputData.getDescription(), inputData.getRating());

        try {
            reviewDataAccessObject.saveReview(review);
            reviewDataAccessObject.recordReviewRecipe(inputData.getRecipeId(), review);
            EditReviewOutputData outputData = new EditReviewOutputData(reviewId,"Review published.");
            reviewPresenter.prepareSuccessView(outputData);
        } catch (RuntimeException e) {
            reviewPresenter.prepareFailView("Failed to publish review: " + e.getMessage(), inputData);
        }
    }
}
