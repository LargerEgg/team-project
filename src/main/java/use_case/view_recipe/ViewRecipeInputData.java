package use_case.view_recipe;

import entity.Recipe;

public class ViewRecipeInputData {
    private final Recipe recipe;
    private final String recipeId; // Add this field
    private final String username;

    public ViewRecipeInputData(Recipe recipe,  String username) {
        this.recipe = recipe;
        this.recipeId = recipe.getRecipeId(); // Set recipeId from recipe
        this.username = username;
    }

    // New constructor for when only recipeId is available
    public ViewRecipeInputData(String recipeId, String username) {
        this.recipe = null; // Recipe object is not available yet
        this.recipeId = recipeId;
        this.username = username;
    }

    public String getUsername() {return username;}
    public Recipe getRecipe() {
        return recipe;
    }
    public String getRecipeId() { // Add getter for recipeId
        return recipeId;
    }
}
