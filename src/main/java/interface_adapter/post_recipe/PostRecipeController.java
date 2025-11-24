package interface_adapter.post_recipe;

import use_case.post_recipe.PostRecipeInputBoundary;
import use_case.post_recipe.PostRecipeInputData;

public class PostRecipeController {
    private final PostRecipeInputBoundary postRecipeInteractor;

    public PostRecipeController(PostRecipeInputBoundary postRecipeInteractor) {
        this.postRecipeInteractor = postRecipeInteractor;
    }

    public void publish(PostRecipeInputData inputData) {
        postRecipeInteractor.publish(inputData);
    }

    public void saveDraft(PostRecipeInputData inputData) {
        postRecipeInteractor.saveDraft(inputData);
    }
}
