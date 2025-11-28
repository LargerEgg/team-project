package use_case.recipe_search;

import entity.Recipe;

import java.util.List;

public class RecipeSearchOutputData {
    private final List<Recipe> recipes;

    public RecipeSearchOutputData(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public List<Recipe> getRecipeList() {
        return recipes;
    }
}
