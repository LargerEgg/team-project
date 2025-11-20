package use_case.save_recipe;

public class SaveRecipeInputData {
    private final String username;
    private final String recipeId;

    public SaveRecipeInputData(String username, String recipeId) {
        this.username = username;
        this.recipeId = recipeId;
    }

    public String getUsername() {
        return username;
    }

    public String getRecipeId() {
        return recipeId;
    }
}
