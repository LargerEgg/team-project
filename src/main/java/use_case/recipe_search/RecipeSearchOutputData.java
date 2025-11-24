package use_case.recipe_search;

import entity.Recipe;

import java.util.List;

public class RecipeSearchOutputData {
    private final List<Recipe> recipes;
    private final int currentImageCount;
    private final int totalImageCount;
    private final boolean isFinalResult; // True if this is the final list of recipes, false if it's a progress update

    public RecipeSearchOutputData(List<Recipe> recipes, int currentImageCount, int totalImageCount, boolean isFinalResult) {
        this.recipes = recipes;
        this.currentImageCount = currentImageCount;
        this.totalImageCount = totalImageCount;
        this.isFinalResult = isFinalResult;
    }

    // Constructor for final results
    public RecipeSearchOutputData(List<Recipe> recipes) {
        this(recipes, 0, 0, true);
    }

    // Constructor for progress updates
    public RecipeSearchOutputData(int currentImageCount, int totalImageCount) {
        this(null, currentImageCount, totalImageCount, false);
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public int getCurrentImageCount() {
        return currentImageCount;
    }

    public int getTotalImageCount() {
        return totalImageCount;
    }

    public boolean isFinalResult() {
        return isFinalResult;
    }
}
