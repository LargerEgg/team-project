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
     * @param review the title of the review
     * @param description the description of the review
     * @param rating the rating of the review (1-5)
     */
    public void execute(String review, String description, int rating) {
        final EditReviewInputData editReviewInputData = new EditReviewInputData(
                review, description, rating);

        EditReviewUseCaseInteractor.execute(editReviewInputData);
    }

    /**
     * Executes the "switch to ReviewsView" Use Case.
     */
    public void switchToReviewsView() {
        EditReviewUseCaseInteractor.switchToReviewsView();
    }
}