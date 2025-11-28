package use_case.view_recipe;

import entity.Recipe;

public class ViewRecipeInputData {
    private final Recipe recipe;

    public ViewRecipeInputData(Recipe recipe) {
        this.recipe = recipe;
    }

    public Recipe getRecipe() {
        return recipe;
    }
}
