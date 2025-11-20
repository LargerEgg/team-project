package use_case.post_recipe;

public interface PostRecipeOutputBoundary {
    void prepareSuccessView(PostDataOutputData outputData);
    void prepareDraftSavedView(PostDataOutputData outputData);

    // In case a failure occurs
    void prepareFailedView(String errorMessage, PostRecipeInputData inputData);
}
