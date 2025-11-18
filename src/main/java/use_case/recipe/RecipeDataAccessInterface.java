package use_case.recipe;

import entity.Recipe;
import java.util.List;

public interface RecipeDataAccessInterface {
    Recipe findById(String recipeId);

    void save(Recipe recipe);

    void addRecipe(Recipe recipe);

    List<Recipe> getAllRecipes();
}
