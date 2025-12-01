package use_case.view_recipe;

import data_access.FirebaseSaveRecipeDataAccessObject;
import entity.Recipe;
import use_case.edit_review.EditReviewDataAccessInterface;
import use_case.save_recipe.SaveRecipeDataAccessInterface;

public class ViewRecipeInteractor implements ViewRecipeInputBoundary {

    private final ViewRecipeDataAccessInterface repo;
    private final ViewRecipeOutputBoundary presenter;
    private final SaveRecipeDataAccessInterface saveRecipeDAO;
    private final EditReviewDataAccessInterface editReviewDAO;

    public ViewRecipeInteractor(ViewRecipeDataAccessInterface repo,
                                ViewRecipeOutputBoundary presenter,
                                SaveRecipeDataAccessInterface saveRecipeDAO, EditReviewDataAccessInterface editReviewDAO) {
        this.repo = repo;
        this.presenter = presenter;
        this.saveRecipeDAO = saveRecipeDAO;
        this.editReviewDAO = editReviewDAO;
    }

    @Override
    public void execute(ViewRecipeInputData input) {
        String username = input.getUsername();
        Recipe recipe;

        if (input.getRecipe() != null) {
            recipe = input.getRecipe();
        } else if (input.getRecipeId() != null && !input.getRecipeId().isEmpty()) {
            recipe = repo.findById(input.getRecipeId());
        } else {
            presenter.prepareFailView("Recipe not found: No recipe object or ID provided.");
            return;
        }

        if (recipe == null) {
            presenter.prepareFailView("Recipe not found with ID: " + input.getRecipeId());
            return;
        }

        recipe.setReviews(editReviewDAO.findByRecipe(recipe.getRecipeId()));
        recipe.recalculateAverageRating();

        // Increment the local view count immediately for UI display
        recipe.incrementViews();

        // Run the database write operation on a separate thread to avoid blocking the UI
        new Thread(() -> {
            try {
                repo.recordView(recipe.getRecipeId());
            } catch (Exception e) {
                // In a real application, you'd want to log this error.
                // For now, we catch it to prevent the background thread from crashing.
            }
        }).start();

        boolean saved = false;
        if (username != null && !username.isEmpty()) {
            saved = saveRecipeDAO.isRecipeSaved(username, recipe.getRecipeId());
        }

        // Prepare the success view immediately, without waiting for the database
        ViewRecipeOutputData outputData = new ViewRecipeOutputData(recipe, username, saved);
        presenter.prepareSuccessView(outputData);
    }
}
