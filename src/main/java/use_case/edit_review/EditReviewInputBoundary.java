package use_case.edit_review;

/**
 * Input Boundary for actions which are related to signing up.
 */
public interface EditReviewInputBoundary {

    /**
     * Executes the signup use case.
     * @param editReviewInputData the input data
     */
    void execute(EditReviewInputData editReviewInputData);

    /**
     * Executes the switch to login view use case.
     */
    void switchToReviewsView();
}
