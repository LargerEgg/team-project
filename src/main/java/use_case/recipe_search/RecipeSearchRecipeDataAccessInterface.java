package use_case.recipe_search;

import entity.Recipe;
import java.util.List;

public interface RecipeSearchRecipeDataAccessInterface {
    List<Recipe> search(String name, String category);
    List<String> getAllCategories();
}
