package entity;
import java.util.Date;
import java.util.UUID;

/**
 * A simple entity representing a review. reviews have a username and password.
 */
public class Review {
    private UUID reviewId;
    private UUID recipeId;
    private UUID authorId;
    private Date dateCreated;
    private String title;
    private String description;
    private int rating;

    /**
     * Creates a new user with the given non-empty name and non-empty password.
     * @param title the title of the review
     * @param description the description of the review
     * @param rating the rating of the recipe
     * @param recipeId the UUID of the recipe this review is for
     * @param authorId the UUID of the author of this recipe
     * @throws IllegalArgumentException if the password or name are empty
     */
    public Review(String title, String description, int rating, UUID recipeId, UUID authorId) {
        /*
            REPLACE ALL OF THIS SHIT LATER
         */
        if ("".equals(title)) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if ("".equals(description)) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (rating < 1 | rating > 5) {
            throw new IllegalArgumentException("Rating should be between 1 and 5");
        }
        this.title = title;
        this.description = description;
        this.rating = rating;

        this.dateCreated = new Date();
        this.reviewId =  UUID.randomUUID();
        this.recipeId = recipeId;
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public UUID getReviewId() {
        return reviewId;
    }

    public UUID getRecipeId() {
        return recipeId;
    }

    public UUID getAuthorId() {
        return authorId;
    }
}