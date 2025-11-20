package use_case.post_recipe;

import entity.Recipe;

public interface PostRecipeDataAccessInterface {

    Recipe saveRecipe(Recipe recipe);
}
