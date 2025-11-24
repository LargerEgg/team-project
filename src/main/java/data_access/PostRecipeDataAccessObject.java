package data_access;

import use_case.post_recipe.PostRecipeDataAccessInterface;
import entity.Recipe;

public class PostRecipeDataAccessObject implements PostRecipeDataAccessInterface {
    @Override
    public Recipe saveRecipe(Recipe recipe) {
        System.out.println("Saving recipe: " + recipe.getTitle() + " (Status: " + recipe.getStatus() + ")");
        return recipe;
    }
}
