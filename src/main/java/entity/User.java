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

    public String getName() {
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

    public List<String> getFavouriteCategoriesRanked() {
        if (this.savedRecipes == null || this.savedRecipes.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Integer> categoryCounts = new HashMap<>();
        for (Recipe recipe : this.savedRecipes) {
            if (recipe == null) {
                continue;
            }

            String category;
            try {
                category = recipe.getCategory();
            } catch (Exception e) {
                continue;
            }

            if (category == null || category.trim().isEmpty()) {
                continue;
            }

            category = category.trim();
            categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
        }

        if (categoryCounts.isEmpty()) {
            return new ArrayList<>();
        }

        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(categoryCounts.entrySet());

        entryList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        List<String> rankedCategories = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : entryList) {
            rankedCategories.add(entry.getKey());
        }

        return rankedCategories;
    }
}
