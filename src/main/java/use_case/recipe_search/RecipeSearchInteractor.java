package use_case.recipe_search;

import entity.Recipe;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecipeSearchInteractor implements RecipeSearchInputBoundary {
    private final RecipeSearchRecipeDataAccessInterface dataAccess;
    private final RecipeSearchOutputBoundary presenter;
    private SwingWorker<List<Recipe>, Void> activeWorker;

    public RecipeSearchInteractor(RecipeSearchRecipeDataAccessInterface dataAccess,
                                    RecipeSearchOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(RecipeSearchInputData inputData) {
        // If there's an active worker, cancel it
        if (activeWorker != null && !activeWorker.isDone()) {
            activeWorker.cancel(true);
        }

        // Create a new worker for the new search
        activeWorker = new SwingWorker<List<Recipe>, Void>() {
            @Override
            protected List<Recipe> doInBackground() throws Exception {
                // This is where the long-running search happens
                return dataAccess.search(inputData.getName(), inputData.getCategory());
            }

            @Override
            protected void done() {
                // This runs on the EDT after the background task is finished
                if (!isCancelled()) {
                    try {
                        List<Recipe> recipes = get(); // Get the result from doInBackground
                        RecipeSearchOutputData outputData = new RecipeSearchOutputData(recipes);
                        presenter.prepareSuccessView(outputData);
                    } catch (InterruptedException | ExecutionException e) {
                        // If an error occurred in the background, present the fail view
                        presenter.prepareFailView(e.getCause().getMessage());
                    }
                }
                // If the worker was cancelled, do nothing.
            }
        };

        // Start the new worker
        activeWorker.execute();
    }
}
