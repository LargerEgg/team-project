package use_case.save_recipe;

public class SaveRecipeOutputData {
    private final String recipeName;
    private final boolean saved;

    public SaveRecipeOutputData(String recipeName, boolean saved) {
        this.recipeName = recipeName;
        this.saved = saved;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public boolean isSaved() {
        return saved;
    }

}
