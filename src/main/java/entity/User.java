package entity;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class User
{
    private String username;
    private String password;
    private List<Recipe> savedRecipes;
    private List<Recipe> publishedRecipes;
    private List<Review> reviews;
    private String userid;

    public User(String name, String password) {
        if ("".equals(name)) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if ("".equals(password)) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.username = name;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    public void saveRecipe(Recipe recipe) {
        if (recipe != null && !this.savedRecipes.contains(recipe)) {
            this.savedRecipes.add(recipe);
        }
    }

    public void removeSavedRecipe(Recipe recipe) {
        if (recipe != null) {
            this.savedRecipes.remove(recipe);
        }
    }

    public void saveReview(Review review) {
        if (review != null && !this.reviews.contains(review)) {
            this.reviews.add(review);
        }
    }

    public void removeReview(Review review) {
        if (review != null) {
            this.reviews.remove(review);
        }
    }

    public List<Review> getReviews() {
        return this.reviews;
    }

    public List<Recipe> getSavedRecipes() {
        return savedRecipes;
    }

    public String getUserid() {
        return userid;
    }

    public List<Recipe> getPublishedRecipes() {
        return publishedRecipes;
    }
}
