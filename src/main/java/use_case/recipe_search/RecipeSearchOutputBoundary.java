package use_case.recipe_search;

public interface RecipeSearchOutputBoundary {
    void prepareSuccessView(RecipeSearchOutputData recipeSearchOutputData);

    void prepareFailView(String error);
}
