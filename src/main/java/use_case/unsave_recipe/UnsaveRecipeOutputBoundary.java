package use_case.unsave_recipe;

public interface UnsaveRecipeOutputBoundary {
    void prepareSuccessView(String message);
    void prepareFailView(String error);
}
