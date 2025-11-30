package interface_adapter.save_recipe;

import use_case.save_recipe.SaveRecipeOutputBoundary;
import use_case.save_recipe.SaveRecipeOutputData;

import javax.swing.*;

public class SaveRecipePresenter implements SaveRecipeOutputBoundary {

    private final SaveRecipeViewModel viewModel;

    public SaveRecipePresenter(SaveRecipeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccess(SaveRecipeOutputData outputData) {
        SaveRecipeState state = new SaveRecipeState();
        state.setSaved(outputData.isSaved());

        if (outputData.isSaved()) {
            state.setMessage("Saved" + outputData.getRecipeName());
        } else {
            state.setMessage("Unsaved" + outputData.getRecipeName());
        }

        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    @Override
    public void prepareUnsave(SaveRecipeOutputData outputData) {
        SaveRecipeState state = new SaveRecipeState();
        state.setSaved(false);
        state.setMessage("Unsaved: " + outputData.getRecipeName());

        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    @Override
    public void prepareFailure(String message) {
        SaveRecipeState state = new SaveRecipeState();
        state.setSaved(false);
        state.setMessage(message);

        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

}
