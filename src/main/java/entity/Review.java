package entity;
import java.util.Date;
import java.util.UUID;

/**
 * A simple entity representing a review. reviews have a username and password.
 */
public class Review {
    private String reviewId;
    private String recipeId;
    private String authorId; // Changed to String to match usage in RecipeView
    private Date dateCreated;
    private String title;
    private String description;
    private int rating;

    /**
     * Creates a new review with all necessary details.
     * @param reviewId the unique ID of the review
     * @param recipeId the ID of the recipe being reviewed
     * @param authorId the ID of the user who wrote the review
     * @param dateCreated the date the review was created
     * @param title the title of the review
     * @param description the description of the review
     * @param rating the rating of the recipe (1-5)
     * @throws IllegalArgumentException if the title or description are empty, or rating is out of range
     */
    public Review(String reviewId, String recipeId, String authorId, Date dateCreated, String title, String description, int rating) {
        if ("".equals(title)) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if ("".equals(description)) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating should be between 1 and 5");
        }
        this.reviewId = reviewId;
        this.recipeId = recipeId;
        this.authorId = authorId;
        this.dateCreated = dateCreated;
        this.title = title;
        this.description = description;
        this.rating = rating;
    }

    // Overloaded constructor for backward compatibility or simpler creation if some fields are not immediately available
    public Review(String title, String description, int rating) {
        this(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "anonymous", new Date(), title, description, rating); // Default values for new fields
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getRating() {
        return rating;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }
}
