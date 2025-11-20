package use_case.save_recipe;

public interface SaveRecipeOutputBoundary {
    void prepareSuccessView(SaveRecipeOutputData outputData);
    void prepareFailView(String error);
}
