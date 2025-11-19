package use_case.recipe;

import entity.Recipe;
import java.util.List;
// Not sure about the whole recipe folder yet, may be modified later
public interface RecipeDataAccessInterface {
    Recipe findById(String recipeId);

    void save(Recipe recipe);

    void addRecipe(Recipe recipe);

    List<Recipe> getAllRecipes();
}
