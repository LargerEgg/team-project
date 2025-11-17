package use_case.edit_review;

/**
 * The Input Data for the Signup Use Case.
 */
public class EditReviewInputData {

    private final String review;
    private final String description;
    private final int rating;

    public EditReviewInputData(String review, String description, int rating) {
        this.review = review;
        this.description = description;
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public String getDescription() {
        return description;
    }

    public int getRating() {
        return rating;
    }
}
