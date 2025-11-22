package interface_adapter.post_recipe;

import interface_adapter.ViewManagerModel;
import use_case.post_recipe.PostRecipeOutputData;
import use_case.post_recipe.PostRecipeInputData;
import use_case.post_recipe.PostRecipeOutputBoundary;

/**
 * The Presenter for the Post Recipe Use Case.
 */
public class PostRecipePresenter implements PostRecipeOutputBoundary {
    private final PostRecipeViewModel postRecipeViewModel;
    private final ViewManagerModel viewManagerModel;

    public PostRecipePresenter(ViewManagerModel viewManagerModel,
                               PostRecipeViewModel postRecipeViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.postRecipeViewModel = postRecipeViewModel;
    }

    @Override
    public void prepareSuccessView(PostRecipeOutputData outputData) {
        final PostRecipeState state = postRecipeViewModel.getState();
        state.setSuccessMessage(outputData.getMessage() + " (ID: " + outputData.getRecipeId() + ")");
        state.setErrorMessage("");

        clearForm(state);

        postRecipeViewModel.firePropertyChange("success");
    }

    @Override
    public void prepareDraftSavedView(PostRecipeOutputData outputData) {
        final PostRecipeState state = postRecipeViewModel.getState();
        state.setSuccessMessage(outputData.getMessage() + " (ID: " + outputData.getRecipeId() + ")");
        state.setErrorMessage("");

        postRecipeViewModel.firePropertyChange("draft_saved");
    }

    @Override
    public void prepareFailedView(String errorMessage, PostRecipeInputData inputData) {
        final PostRecipeState state = postRecipeViewModel.getState();
        state.setErrorMessage(errorMessage);
        state.setSuccessMessage("");

        state.setAuthorId(inputData.getAuthorId());
        state.setTitle(inputData.getTitle());
        state.setDescription(inputData.getDescription());
        state.setIngredients(inputData.getIngredients());
        state.setCategory(inputData.getCategory());
        state.setTags(inputData.getTags());
        state.setImagePath(inputData.getImagePath());

        postRecipeViewModel.firePropertyChange("error");
    }

    private void clearForm(PostRecipeState state) {
        state.setTitle("");
        state.setDescription("");
        state.setIngredients(new java.util.ArrayList<>());
        state.setCategory("");
        state.setTags(new java.util.ArrayList<>());
        state.setImagePath("");
    }
}