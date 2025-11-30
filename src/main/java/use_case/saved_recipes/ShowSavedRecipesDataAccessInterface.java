package use_case.saved_recipes;

import entity.Recipe;

import java.util.List;

public interface ShowSavedRecipesDataAccessInterface {
    List<Recipe> getRecipes(String username);
}
