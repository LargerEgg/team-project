package use_case.edit_review;

/**
 * The Input Data for the Signup Use Case.
 */
public class EditReviewInputData {

    private final String review; //title
    private final String description;
    private final int rating;
    private String authorId;
    private final String recipeId;

    public EditReviewInputData(String review, String description, int rating, String authorId, String recipeId) {
        this.review = review;
        this.description = description;
        this.rating = rating;
        this.authorId = authorId;
        this.recipeId = recipeId;
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

    public String getAuthorId() {
        return authorId;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
}
