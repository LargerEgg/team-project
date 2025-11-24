package use_case.recommend_recipe;

import java.util.List;
import entity.Recipe;

public class RecommendRecipeOutputData {

    private final List<Recipe> recipes;

    private final String categoryName;

    public RecommendRecipeOutputData(List<Recipe> recipes, String categoryName) {
        this.recipes = recipes;
        this.categoryName = categoryName;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
