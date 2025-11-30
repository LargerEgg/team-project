package use_case.save_recipe;

public interface SaveRecipeOutputBoundary {
    void prepareSuccess(SaveRecipeOutputData saveRecipeOutputData);
    void prepareUnsave(SaveRecipeOutputData saveRecipeOutputData);
    void prepareFailure(String errorMessage);
}
