package use_case.edit_review;

/**
 * The output boundary for the Signup Use Case.
 */
public interface EditReviewOutputBoundary {

    /**
     * Prepares the success view for the Edit Reviews Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(EditReviewOutputData outputData);

    /**
     * Prepares the failure view for the Edit Reviews Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage, EditReviewInputData inputData);
}
