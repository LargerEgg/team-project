package use_case.recipe_search;

import entity.Recipe;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecipeSearchInteractor implements RecipeSearchInputBoundary {
    private final RecipeSearchRecipeDataAccessInterface dataAccess;
    private final RecipeSearchOutputBoundary presenter;
    private SwingWorker<List<Recipe>, RecipeSearchOutputData> activeWorker;
    private boolean isTestMode = false; // New field for test mode

    public RecipeSearchInteractor(RecipeSearchRecipeDataAccessInterface dataAccess,
                                    RecipeSearchOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    // Setter for test mode
    public void setTestMode(boolean testMode) {
        this.isTestMode = testMode;
        System.out.println("RecipeSearchInteractor: Test mode set to " + testMode);
    }

    @Override
    public void execute(RecipeSearchInputData inputData) {
        System.out.println("RecipeSearchInteractor: execute called. isTestMode = " + isTestMode);
        if (isTestMode) {
            System.out.println("RecipeSearchInteractor: Executing in test mode (synchronously).");
            // Synchronous execution for testing
            try {
                List<Recipe> recipes = dataAccess.search(inputData.getName(), inputData.getCategory(), presenter);
                RecipeSearchOutputData outputData = new RecipeSearchOutputData(recipes);
                presenter.prepareSuccessView(outputData);
                System.out.println("RecipeSearchInteractor: prepareSuccessView called in test mode.");
            } catch (Exception e) {
                // Prepend the error message for consistency with the test expectation
                presenter.prepareFailView("Error searching for recipes: " + e.getMessage());
                System.out.println("RecipeSearchInteractor: prepareFailView called in test mode with error: " + e.getMessage());
            }
        } else {
            System.out.println("RecipeSearchInteractor: Executing in production mode (asynchronously with SwingWorker).");
            // Asynchronous execution with SwingWorker for production
            if (activeWorker != null && !activeWorker.isDone()) {
                activeWorker.cancel(true);
            }

            activeWorker = new SwingWorker<List<Recipe>, RecipeSearchOutputData>() {
                @Override
                protected List<Recipe> doInBackground() throws Exception {
                    System.out.println("RecipeSearchInteractor: SwingWorker doInBackground called.");
                    return dataAccess.search(inputData.getName(), inputData.getCategory(), presenter);
                }

                @Override
                protected void process(List<RecipeSearchOutputData> chunks) {
                    System.out.println("RecipeSearchInteractor: SwingWorker process called.");
                    for (RecipeSearchOutputData progressData : chunks) {
                        presenter.prepareProgressView(progressData);
                    }
                }

                @Override
                protected void done() {
                    System.out.println("RecipeSearchInteractor: SwingWorker done called.");
                    if (!isCancelled()) {
                        try {
                            List<Recipe> recipes = get();
                            RecipeSearchOutputData outputData = new RecipeSearchOutputData(recipes);
                            presenter.prepareSuccessView(outputData);
                            System.out.println("RecipeSearchInteractor: prepareSuccessView called by SwingWorker.");
                        } catch (InterruptedException | ExecutionException e) {
                            // Prepend the error message for consistency with the test expectation
                            presenter.prepareFailView("Error searching for recipes: " + e.getCause().getMessage());
                            System.out.println("RecipeSearchInteractor: prepareFailView called by SwingWorker with error: " + e.getCause().getMessage());
                        }
                    }
                }
            };
            activeWorker.execute();
        }
    }
}
