package use_case.saved_recipes;

import entity.Recipe;
import java.util.List;

public class ShowSavedRecipesOutputData {
    private final List<Recipe> saved_recipes;

    public ShowSavedRecipesOutputData(List<Recipe> recipes) {
        this.saved_recipes = recipes;
    }

    public List<Recipe> getSaved_recipes() {
        return saved_recipes;
    }
}
