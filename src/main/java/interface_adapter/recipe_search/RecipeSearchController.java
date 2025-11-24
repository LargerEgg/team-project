package interface_adapter.recipe_search;

import use_case.recipe_search.RecipeSearchInputBoundary;
import use_case.recipe_search.RecipeSearchInputData;

public class RecipeSearchController {
    private final RecipeSearchInputBoundary recipeSearchInteractor;

    public RecipeSearchController(RecipeSearchInputBoundary recipeSearchInteractor) {
        this.recipeSearchInteractor = recipeSearchInteractor;
    }

    public void execute(String name, String category) {
        RecipeSearchInputData recipeSearchInputData = new RecipeSearchInputData(name, category);
        recipeSearchInteractor.execute(recipeSearchInputData);
    }
}
