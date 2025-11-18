package use_case.recipe.view_recipe;

public class ViewRecipeOutputData {
    private final int views;
    private final String recipeId;

    public ViewRecipeOutputData(int views, String recipeId) {
        this.views = views;
        this.recipeId = recipeId;
    }

    public int getViews() { return views; }
    public String getRecipeId() { return recipeId; }
}
