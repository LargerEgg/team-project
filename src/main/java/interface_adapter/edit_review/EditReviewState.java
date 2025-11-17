package interface_adapter.edit_review;

/**
 * The state for the Edit Review View Model.
 */
public class EditReviewState {
    private String review = "";
    private String reviewError;
    private String description = "";
    private String descriptionError;
    private int rating;
    private String ratingError;

    public String getReview() {
        return review;
    }

    public String getReviewError() {
        return reviewError;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionError() {
        return descriptionError;
    }

    public int getRating() {
        return rating;
    }

    public String getRatingError() {
        return ratingError;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setReviewError(String reviewError) {
        this.reviewError = reviewError;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDescriptionError(String descriptionError) {
        this.descriptionError = descriptionError;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setRatingError(String ratingError) {
        this.ratingError = ratingError;
    }

    @Override
    public String toString() {
        return "EditReviewState{"
                + "title='" + review + '\''
                + ", description='" + description + '\''
                + ", rating='" + rating + '\''
                + '}';
    }
}