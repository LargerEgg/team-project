package use_case.edit_review;

/**
 * Output Data for the Edit Review Use Case.
 */
public class EditReviewOutputData {
    private final String reviewId;
    private final String message;

    public EditReviewOutputData(String reviewId, String message) {
        this.reviewId = reviewId;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getReviewId() {
        return reviewId;
    }

}
