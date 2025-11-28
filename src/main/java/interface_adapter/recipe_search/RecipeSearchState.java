package interface_adapter.recipe_search;

import entity.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * The state for the Recipe Search View Model.
 */
public class RecipeSearchState {
    private String searchError = "";
    private List<Recipe> recipeList = new ArrayList<>();

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
}
