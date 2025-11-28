package entity;

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

    private String category;
    private List<String> tags;
    private List<Review> reviews;

    private int views;
    private int saves;

    private boolean shareable;
    private Date creationDate;
    private Date updateDate;
    private Status status;

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
        this.recipeId = recipeId;
        this.authorId = authorId;

        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.imagePath = imagePath;

        this.reviews = new ArrayList<>(); // 初始化为空列表
        this.category = category;
        this.tags = tags;
        this.status = status;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.shareable = false; // 默认为 false

        this.views = 0; // 默认为 0
        this.saves = 0; // 默认为 0

    }

    private void touchUpdateDate() {
        this.updateDate = new Date();
    }

    // --- Getters and Setters ---

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
        touchUpdateDate();
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
        touchUpdateDate();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        touchUpdateDate();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        touchUpdateDate();
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        touchUpdateDate();
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        touchUpdateDate();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        touchUpdateDate();
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
        touchUpdateDate();
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        touchUpdateDate();
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
        touchUpdateDate();
    }

    public int getSaves() {
        return saves;
    }

    public void setSaves(int saves) {
        this.saves = saves;
        touchUpdateDate();
    }

    public boolean isShareable() {
        return shareable;
    }

    public void setShareable(boolean shareable) {
        this.shareable = shareable;
        touchUpdateDate();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        touchUpdateDate();
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        touchUpdateDate();
    }
}