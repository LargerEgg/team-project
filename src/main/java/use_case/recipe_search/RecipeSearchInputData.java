package use_case.recipe_search;

public class RecipeSearchInputData {
    private final String name;
    private final String category;

    public RecipeSearchInputData(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }
}
