package use_case.recipe_search;

public class RecipeSearchInputData {
    private final String query;

    public RecipeSearchInputData(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
