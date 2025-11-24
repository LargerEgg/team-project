package use_case.post_recipe;

public interface PostRecipeOutputBoundary {
    void prepareSuccessView(PostRecipeOutputData outputData);
    void prepareDraftSavedView(PostRecipeOutputData outputData);

    // In case a failure occurs
    void prepareFailedView(String errorMessage, PostRecipeInputData inputData);
}
