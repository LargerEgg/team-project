package use_case.unsave_recipe;

public interface UnsaveRecipeInputBoundary {
    void execute(String username, String recipeId);
}
