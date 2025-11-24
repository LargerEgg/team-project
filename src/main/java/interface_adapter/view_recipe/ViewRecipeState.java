package interface_adapter.view_recipe;

public class ViewRecipeState {
    private String title = "";
    private String recipeId = "";
    private int views = 0;
    private int saves = 0;
    private double averageRating = 0.0;
    private String viewError = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getSaves() {
        return saves;
    }

    public void setSaves(int saves) {
        this.saves = saves;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public String getViewError() {
        return viewError;
    }

    public void setViewError(String viewError) {
        this.viewError = viewError;
    }
}

