package interface_adapter.edit_review;

import use_case.edit_review.EditReviewInputBoundary;
import use_case.edit_review.EditReviewInputData;

/**
 * Controller for the Edit Review Use Case.
 */
public class EditReviewController {

    private final EditReviewInputBoundary EditReviewUseCaseInteractor;

    public EditReviewController(EditReviewInputBoundary EditReviewUseCaseInteractor) {
        this.EditReviewUseCaseInteractor = EditReviewUseCaseInteractor;
    }

    /**
     * Executes the Publish Use Case.
     * @param inputData the input data in a review
     */
    public void publish(EditReviewInputData inputData) {
        EditReviewUseCaseInteractor.publish(inputData);
    }
}