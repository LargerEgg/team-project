package use_case.edit_review;

/**
 * Output Data for the Edit Review Use Case.
 */
public class EditReviewOutputData {

    private final String message;

    public EditReviewOutputData(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
