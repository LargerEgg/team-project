package interface_adapter.save_recipe;

import use_case.save_recipe.SaveRecipeOutputBoundary;
import use_case.save_recipe.SaveRecipeOutputData;

public class SaveRecipePresenter implements SaveRecipeOutputBoundary {
    private final SaveRecipeViewModel saveRecipeViewModel;

    public SaveRecipePresenter(SaveRecipeViewModel saveRecipeViewModel) {
        this.saveRecipeViewModel = saveRecipeViewModel;
    }

    @Override
    public void prepareSuccessView(SaveRecipeOutputData outputData) {
        final SaveRecipeState saveRecipeState = saveRecipeViewModel.getState();
        saveRecipeState.setSuccessMessage(outputData.getMessage());
        saveRecipeState.setErrorMessage("");
        saveRecipeViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        final SaveRecipeState saveRecipeState = saveRecipeViewModel.getState();
        saveRecipeState.setErrorMessage(error);
        saveRecipeState.setSuccessMessage("");
        saveRecipeViewModel.firePropertyChange();
    }
}

