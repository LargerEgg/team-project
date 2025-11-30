package use_case.recipe_search;

import entity.Recipe;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecipeSearchInteractor implements RecipeSearchInputBoundary {
    private final RecipeSearchRecipeDataAccessInterface dataAccess;
    private final RecipeSearchOutputBoundary presenter;
    private SwingWorker<List<Recipe>, Recipe> activeWorker;
    private boolean isTestMode = false;

    public RecipeSearchInteractor(RecipeSearchRecipeDataAccessInterface dataAccess,
                                    RecipeSearchOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    public void setTestMode(boolean testMode) {
        this.isTestMode = testMode;
    }

    @Override
    public void execute(RecipeSearchInputData inputData) {
        if (isTestMode) {
            // Synchronous execution for testing
            try {
                List<Recipe> recipes = dataAccess.search(inputData.getName(), inputData.getCategory());
                RecipeSearchOutputData outputData = new RecipeSearchOutputData(recipes);
                presenter.prepareSuccessView(outputData);
            } catch (Exception e) {
                presenter.prepareFailView("Error searching for recipes: " + e.getMessage());
            }
        } else {
            // Asynchronous execution with SwingWorker
            if (activeWorker != null && !activeWorker.isDone()) {
                activeWorker.cancel(true);
            }

            activeWorker = new SwingWorker<List<Recipe>, Recipe>() {
                @Override
                protected List<Recipe> doInBackground() throws Exception {
                    List<Recipe> recipes = dataAccess.search(inputData.getName(), inputData.getCategory());
                    for (Recipe recipe : recipes) {
                        publish(recipe); // Publish each recipe for progress updates
                    }
                    return recipes;
                }

                @Override
                protected void process(List<Recipe> chunks) {
                    // This method is now reachable
                    RecipeSearchOutputData progressData = new RecipeSearchOutputData(chunks);
                    presenter.prepareProgressView(progressData);
                }

                @Override
                protected void done() {
                    if (isCancelled()) {
                        return;
                    }
                    try {
                        List<Recipe> recipes = get();
                        RecipeSearchOutputData outputData = new RecipeSearchOutputData(recipes);
                        presenter.prepareSuccessView(outputData);
                    } catch (Exception e) {
                        Throwable cause = (e instanceof ExecutionException) ? e.getCause() : e;
                        
                        // Check for interruption anywhere in the cause chain
                        boolean interrupted = false;
                        Throwable current = cause;
                        while (current != null) {
                            if (current instanceof InterruptedException) {
                                interrupted = true;
                                break;
                            }
                            current = current.getCause();
                        }

                        if (interrupted) {
                            presenter.prepareFailView("Error searching for recipes: The search was interrupted.");
                        } else {
                            presenter.prepareFailView("Error searching for recipes: " + (cause != null ? cause.getMessage() : "null"));
                        }
                    }
                }
            };
            activeWorker.execute();
        }
    }
}
