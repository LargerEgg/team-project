package use_case.save_recipe;

public class SaveRecipeOutputData {
    private final String recipeName;

    public SaveRecipeOutputData(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeName() {
        return recipeName;
    }

}
