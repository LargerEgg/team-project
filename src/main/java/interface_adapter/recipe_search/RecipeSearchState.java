package interface_adapter.recipe_search;

import entity.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeSearchState {
    private String searchError = "";
    private List<Recipe> recipeList = new ArrayList<>();
    private List<String> categories = new ArrayList<>();

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
}
