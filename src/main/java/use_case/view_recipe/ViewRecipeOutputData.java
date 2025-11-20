package use_case.view_recipe;

public class ViewRecipeOutputData {
    private final String title;
    private final int views;
    private final String recipeId;
    private final int saves;
    private final double averageRating;

    public ViewRecipeOutputData(String title, String recipeId, int views, int saves, double averageRating) {
        this.title = title;
        this.views = views;
        this.recipeId = recipeId;
        this.saves = saves;
        this.averageRating = averageRating;
    }
    public String getTitle() { return title; }
    public int getViews() { return views; }
    public String getRecipeId() { return recipeId; }
    public int getSaves() { return saves; }
    public double getAverageRating() { return averageRating; }
}
