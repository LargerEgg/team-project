package interface_adapter.recommend_recipe;

import entity.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecommendRecipeState {
    private List<Recipe> recommendations = new ArrayList<>();
    private String recommendationError = null;
    private String username = "";

    // Copy Constructor
    // Creates a copy of the state to ensure immutability where needed
    public RecommendRecipeState(RecommendRecipeState copy) {
        this.recommendations = copy.recommendations;
        this.recommendationError = copy.recommendationError;
        this.username = copy.username;
    }

    // Default Constructor
    public RecommendRecipeState() {}

    // --- Getters ---
    public List<Recipe> getRecommendations() {
        return recommendations;
    }

    public String getRecommendationError() {
        return recommendationError;
    }

    public String getUsername() {
        return username;
    }

    // --- Setters ---
    public void setRecommendations(List<Recipe> recommendations) {
        this.recommendations = recommendations;
    }

    public void setRecommendationError(String recommendationError) {
        this.recommendationError = recommendationError;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}