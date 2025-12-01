package use_case.view_recipe;

import entity.Recipe;

public class ViewRecipeInputData {
    private final Recipe recipe;
    private final String username;

    public ViewRecipeInputData(Recipe recipe,  String username) {
        this.recipe = recipe;
        this.username = username;
    }

    public String getUsername() {return username;}
    public Recipe getRecipe() {
        return recipe;
    }
}
