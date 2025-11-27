package interface_adapter.view_recipe;

import entity.Recipe;

public class ViewRecipeState {
    private Recipe recipe;
    private String errorMessage = null;

    public ViewRecipeState(ViewRecipeState copy) {
        this.recipe = copy.recipe;
        this.errorMessage = copy.errorMessage;
    }

    public ViewRecipeState() {}

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
