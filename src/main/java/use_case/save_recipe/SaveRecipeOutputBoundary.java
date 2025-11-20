package use_case.save_recipe;

public interface SaveRecipeOutputBoundary {
    SaveRecipeOutputData prepareSuccessView(SaveRecipeOutputData outputData);
    SaveRecipeOutputData prepareFailView(String error);
}
