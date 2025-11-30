package interface_adapter.unsave_recipe;

import interface_adapter.save_recipe.SaveRecipeState;
import interface_adapter.save_recipe.SaveRecipeViewModel;
import use_case.unsave_recipe.UnsaveRecipeOutputBoundary;

public class UnsaveRecipePresenter implements UnsaveRecipeOutputBoundary {

    private final SaveRecipeViewModel viewModel;

    public UnsaveRecipePresenter(SaveRecipeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(String message) {
        SaveRecipeState state = new SaveRecipeState();
        state.setSaved(false);
        state.setMessage(message);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        SaveRecipeState state = new SaveRecipeState();
        state.setSaved(true);
        state.setMessage(error);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}
