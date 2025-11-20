package use_case.post_recipe;

public class PostDataOutputData {
    private final String recipeId;
    private final String message;

    public PostDataOutputData(String recipeId, String message) {
        this.recipeId = recipeId;
        this.message = message;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public String getMessage() {
        return message;
    }
}
