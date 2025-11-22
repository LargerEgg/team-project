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
        // Recipe was successfully published
        final PostRecipeState state = postRecipeViewModel.getState();
        state.setSuccessMessage(outputData.getMessage() + " (ID: " + outputData.getRecipeId() + ")");
        state.setErrorMessage("");

        // Clear the form after successful publish
        clearForm(state);

        // Fire property change with specific property name so view knows it's a success
        postRecipeViewModel.firePropertyChange("success");
    }

    @Override
    public void prepareDraftSavedView(PostRecipeOutputData outputData) {
        // Recipe was successfully saved as draft
        final PostRecipeState state = postRecipeViewModel.getState();
        state.setSuccessMessage(outputData.getMessage() + " (ID: " + outputData.getRecipeId() + ")");
        state.setErrorMessage("");

        // Don't clear form for drafts - user might want to continue editing

        // Fire property change with specific property name so view knows it's a draft save
        postRecipeViewModel.firePropertyChange("draft_saved");
    }

    @Override
    public void prepareFailedView(String errorMessage, PostRecipeInputData inputData) {
        // Recipe failed to publish or save
        final PostRecipeState state = postRecipeViewModel.getState();
        state.setErrorMessage(errorMessage);
        state.setSuccessMessage("");

        // Restore the input data so user doesn't lose their work
        state.setAuthorId(inputData.getAuthorId());
        state.setTitle(inputData.getTitle());
        state.setDescription(inputData.getDescription());
        state.setIngredients(inputData.getIngredients());
        state.setCategory(inputData.getCategory());
        state.setTags(inputData.getTags());
        state.setImagePath(inputData.getImagePath());

        // Fire property change with specific property name so view knows it's an error
        postRecipeViewModel.firePropertyChange("error");
    }

    /**
     * Clears all form fields in the state.
     */
    private void clearForm(PostRecipeState state) {
        state.setTitle("");
        state.setDescription("");
        state.setIngredients(new java.util.ArrayList<>());
        state.setCategory("");
        state.setTags(new java.util.ArrayList<>());
        state.setImagePath("");
    }
}