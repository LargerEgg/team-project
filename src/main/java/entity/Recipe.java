package entity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Recipe {

    public enum Status {
        DRAFT,
        PUBLISHED,
    }

    private String recipeId;
    private String authorId;

    private String title;
    private String description;
    private List<Ingredient> ingredients;
    private String imagePath;
    private BufferedImage image;

    private String category;
    private List<String> tags;
    private List<Review> reviews;

    private int views;
    private int saves;
    private double averageRating;

    private boolean shareable;
    private Date creationDate;
    private Date updateDate;
    private Status status;

    /**
     * Main constructor, includes the pre-fetched image.
     */
    public Recipe(String recipeId,
                  String authorId,
                  String title,
                  String description,
                  List<Ingredient> ingredients,
                  String category,
                  List<String> tags,
                  Status status,
                  Date creationDate,
                  Date updateDate,
                  String imagePath,
                  BufferedImage image
    ) {
        this.recipeId = recipeId;
        this.authorId = authorId;
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.imagePath = imagePath;
        this.image = image;
        this.reviews = new ArrayList<>();
        this.category = category;
        this.tags = tags;
        this.status = status;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.shareable = false;
        this.views = 0;
        this.saves = 0;
        this.averageRating = 0.0;
    }

    /**
     * Overloaded constructor for lazy-loading the image.
     */
    public Recipe(String recipeId,
                  String authorId,
                  String title,
                  String description,
                  List<Ingredient> ingredients,
                  String category,
                  List<String> tags,
                  Status status,
                  Date creationDate,
                  Date updateDate,
                  String imagePath
    ) {
        this(recipeId, authorId, title, description, ingredients, category, tags, status, creationDate, updateDate, imagePath, null);
    }

    private void touchUpdateDate() {
        this.updateDate = new Date();
    }

    // --- Getters and Setters ---

    public String getRecipeId() { return recipeId; }
    public void setRecipeId(String recipeId) { this.recipeId = recipeId; touchUpdateDate(); }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; touchUpdateDate(); }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; touchUpdateDate(); }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; touchUpdateDate(); }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; touchUpdateDate(); }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; touchUpdateDate(); }

    /**
     * Lazily loads the image. If the image is not already in memory, it downloads it
     * from the imagePath, caches it, and then returns it.
     */
    public BufferedImage getImage() {
        if (this.image == null && this.imagePath != null && !this.imagePath.isEmpty()) {
            try {
                this.image = ImageIO.read(new URL(this.imagePath));
            } catch (IOException e) {
                e.printStackTrace();
                // Return null or a placeholder image if the download fails
                return null;
            }
        }
        return this.image;
    }

    public void setImage(BufferedImage image) { this.image = image; touchUpdateDate(); }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; touchUpdateDate(); }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; touchUpdateDate(); }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; touchUpdateDate(); }



    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; touchUpdateDate(); }

    public int getSaves() { return saves; }
    public void setSaves(int saves) { this.saves = saves; touchUpdateDate(); }

    public void incrementViews() {
        this.views++;
        touchUpdateDate();
    }

    public void incrementSaves() {
        this.saves++;
        touchUpdateDate();
    }

    public boolean isShareable() { return shareable; }
    public void setShareable(boolean shareable) { this.shareable = shareable; touchUpdateDate(); }

    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; touchUpdateDate(); }

    public Date getUpdateDate() { return updateDate; }
    public void setUpdateDate(Date updateDate) { this.updateDate = updateDate; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; touchUpdateDate(); }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; touchUpdateDate(); }

    public void recalculateAverageRating() {
        if (reviews.isEmpty()) {
            this.averageRating = 0.0;
        } else {
            this.averageRating = reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
        }
        touchUpdateDate();
    }
}
