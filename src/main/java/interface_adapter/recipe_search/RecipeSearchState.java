package interface_adapter.recipe_search;

import entity.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeSearchState {
    private String searchError = "";
    private List<Recipe> recipeList = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    private String sortBy = "Views"; // Default sort by Views
    private boolean ascending = true; // Default ascending
    private int currentImageCount = 0;
    private int totalImageCount = 0;
    private String currentUser;

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getSearchError() {
        return searchError;
    }

    public void setSearchError(String searchError) {
        this.searchError = searchError;
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public int getCurrentImageCount() {
        return currentImageCount;
    }

    public void setCurrentImageCount(int currentImageCount) {
        this.currentImageCount = currentImageCount;
    }

    public int getTotalImageCount() {
        return totalImageCount;
    }

    public void setTotalImageCount(int totalImageCount) {
        this.totalImageCount = totalImageCount;
    }
}
