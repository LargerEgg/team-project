package use_case.recipe_search;

import entity.Recipe;

import java.util.List;

/**
 * DAO interface for the Login Use Case.
 */
public interface RecipeSearchRecipeDataAccessInterface {
    /**
     * Returns a list of recipes that match the search filters.
     * @param name the name of the recipe
     * @return a list of recipes
     */
    List<Recipe> search(String name);
}
