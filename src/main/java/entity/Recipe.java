package entity;

import java.util.Date;
import java.util.List;

public class Recipe {

    private String recipeName;
    private int recipeId;
    private String description;
    private List<Ingredient> ingredients;
    private String category;
    private boolean shareable;
    private Date creationDate;
    private Date updateDate;
    private int views;
    private int saves;
    private List<Review> reviews;

    // --- Getters and Setters ---

    public String getRecipeName() {
        return recipeName;
    }
    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public int getRecipeId() {
        return recipeId;
    }
    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isShareable() {
        return shareable;
    }
    public void setShareable(boolean shareable) {
        this.shareable = shareable;
    }

    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public int getViews() {
        return views;
    }
    public void setViews(int views) {
        this.views = views;
    }

    public int getSaves() {
        return saves;
    }
    public void setSaves(int saves) {
        this.saves = saves;
    }

    public List<Review> getReviews() {
        return reviews;
    }
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}