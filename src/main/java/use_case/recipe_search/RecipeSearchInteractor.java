package use_case.recipe_search;

import entity.Recipe;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecipeSearchInteractor implements RecipeSearchInputBoundary {
    private final RecipeSearchRecipeDataAccessInterface dataAccess;
    private final RecipeSearchOutputBoundary presenter;

    public RecipeSearchInteractor(RecipeSearchRecipeDataAccessInterface dataAccess,
                                    RecipeSearchOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(RecipeSearchInputData inputData) {
        new SwingWorker<List<Recipe>, Void>() {
            @Override
            protected List<Recipe> doInBackground() {
                return dataAccess.search(inputData.getName(), inputData.getCategory());
            }

            @Override
            protected void done() {
                try {
                    List<Recipe> recipes = get();
                    RecipeSearchOutputData outputData = new RecipeSearchOutputData(recipes);
                    presenter.prepareSuccessView(outputData);
                } catch (InterruptedException | ExecutionException e) {
                    presenter.prepareFailView(e.getCause().getMessage());
                }
            }
        }.execute();
    }
}
