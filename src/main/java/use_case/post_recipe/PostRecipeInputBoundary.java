package use_case.post_recipe;

public interface PostRecipeInputBoundary {

    // Publish the recipe
    void publish(PostRecipeInputData inputData);

    // Save the recipe as draft
    void saveDraft(PostRecipeInputData inputData);
}
