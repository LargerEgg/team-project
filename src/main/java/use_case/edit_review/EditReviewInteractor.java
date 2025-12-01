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
            inputData.setAuthorId("Anonymous");
        }
        if (inputData.getReview().isBlank()) {
            reviewPresenter.prepareFailView("Title cannot be empty.", inputData);
            return;
        }
        if (inputData.getDescription().isBlank()) {
            reviewPresenter.prepareFailView("Description cannot be empty.", inputData);
            return;
        }
        if (0 >= inputData.getRating() || inputData.getRating() >= 6) {
            reviewPresenter.prepareFailView("Rating must be between 1 and 5.", inputData);
            return;
        }

        String reviewId = UUID.randomUUID().toString();
        final Review review = new Review(reviewId, inputData.getRecipeId(), inputData.getAuthorId(),
                new Date(), inputData.getReview(), inputData.getDescription(), inputData.getRating());

        Review previousReview = reviewDataAccessObject.findByAuthor(inputData.getAuthorId(), inputData.getRecipeId());
        if (previousReview != null && !(previousReview.getAuthorId().equals("Anonymous"))) {
            review.setReviewId(previousReview.getReviewId());
            try {
                reviewDataAccessObject.changeReview(review);
                EditReviewOutputData outputData = new EditReviewOutputData(reviewId,"Review edited successfully:");
                reviewPresenter.prepareSuccessView(outputData);
            } catch (RuntimeException e) {
                reviewPresenter.prepareFailView("Failed to publish review: " + e.getMessage(), inputData);
            }
        }
        else {
            try {
                reviewDataAccessObject.saveReview(review);
                reviewDataAccessObject.recordReviewRecipe(inputData.getRecipeId(), review);
                EditReviewOutputData outputData = new EditReviewOutputData(reviewId,"Review published successfully:");
                reviewPresenter.prepareSuccessView(outputData);
            } catch (RuntimeException e) {
                reviewPresenter.prepareFailView("Failed to publish review: " + e.getMessage(), inputData);
            }
        }
    }
}
