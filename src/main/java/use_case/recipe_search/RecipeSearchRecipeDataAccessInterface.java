package use_case.recipe_search;

import entity.Recipe;

import java.util.List;

public interface RecipeSearchRecipeDataAccessInterface {
    /**
     * Searches for recipes based on a name and/or a category.
     *
     * @param name     The name of the recipe to search for. Can be null or empty.
     * @param category The category to filter by. Can be null or empty.
     * @return A list of matching recipes.
     */
    List<Recipe> search(String name, String category);
}
