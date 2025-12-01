package use_case.edit_review;

/**
 * Input Boundary for actions which are related to signing up.
 */
public interface EditReviewInputBoundary {

    /**
     * Executes the 'publish review' use case.
     * @param editReviewInputData the input data
     */
    void publish(EditReviewInputData editReviewInputData);
}
