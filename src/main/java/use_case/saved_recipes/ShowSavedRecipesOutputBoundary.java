package use_case.saved_recipes;

public interface ShowSavedRecipesOutputBoundary {
    void  prepareSuccess(ShowSavedRecipesOutputData outputData);
    void prepareFailure(String message);
}
