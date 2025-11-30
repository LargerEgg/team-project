package interface_adapter.edit_review;

/**
 * The state for the Edit Review View Model.
 */
public class EditReviewState {
    private String review = "";
    private String description = "";
    private int rating;
    private String authorId;
    private String recipeId;

    private String errorMessage = "";
    private String successMessage;

    private String currentUser = "Anonymous";
    private String currentRecipe;

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

    public String getSuccessMessage() {
        return successMessage;
    }
    public String getErrorMessage() {
        return errorMessage;
    }

    public String getCurrentUser(){
        return currentUser;
    }
    public String getCurrentRecipe(){
        return currentRecipe;
    }

    public void setReview(String review) {
        this.review = review;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }
    public void setCurrentRecipe(String currentRecipe) {
        this.currentRecipe = currentRecipe;
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