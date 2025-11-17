package use_case.edit_review;

/**
 * Output Data for the Edit Review Use Case.
 */
public class EditReviewOutputData {

    private final String username;

    public EditReviewOutputData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
