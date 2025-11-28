package use_case.save_recipe;

import entity.Recipe;

public class SaveRecipeInputData {
    private final String username;
    private final Recipe recipe;

    public SaveRecipeInputData(String username,Recipe recipe) {
        this.username = username;
        this.recipe = recipe;
    }

    public String getUsername() {
        return username;
    }

    public Recipe getRecipe() {
        return recipe;
    }

}
