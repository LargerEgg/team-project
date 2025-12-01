package interface_adapter.view_recipe;

import entity.Recipe;

public class ViewRecipeState {
    private Recipe recipe;
    private String errorMessage = null;
    private boolean isSaved;
    private String currentUser;

    public ViewRecipeState(ViewRecipeState copy) {
        this.recipe = copy.recipe;
        this.errorMessage = copy.errorMessage;
        this.isSaved = copy.isSaved;
        this.currentUser = copy.currentUser;
    }

    public Boolean getIsSaved() {return isSaved;}
    public void setIsSaved(Boolean isSaved) {this.isSaved = isSaved;}

    public String getCurrentUser() {return currentUser;}
    public void setCurrentUser(String currentUser) {this.currentUser = currentUser;}

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
