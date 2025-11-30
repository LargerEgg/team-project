package use_case.view_recipe;

import entity.Recipe;

public class ViewRecipeOutputData {
    private final Recipe recipe;
    private final String username;
    private final boolean Saved;

    public ViewRecipeOutputData(Recipe recipe, String username, boolean saved) {
        this.recipe = recipe;
        this.username = username;
        this.Saved = saved;
    }

    public boolean isSaved() {return Saved;}
    public String getUsername() {return username;}
    public Recipe getRecipe() {
        return recipe;
    }
}
