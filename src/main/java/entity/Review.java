package entity;
import java.util.Date;

/**
 * A simple entity representing a review. reviews have a username and password.
 */
public class Review {
    private int reviewId;
    private int recipeId;
    private int authorId;
    private Date dateCreated;
    private String title;
    private String description;
    private int rating;

    /**
     * Creates a new user with the given non-empty name and non-empty password.
     * @param title the title of the review
     * @param description the description of the review
     * @param rating the rating of the recipe
     * @throws IllegalArgumentException if the password or name are empty
     */
    public Review(String title, String description, int rating) {
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
}