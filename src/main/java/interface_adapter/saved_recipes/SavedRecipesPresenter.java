package interface_adapter.saved_recipes;

import use_case.saved_recipes.ShowSavedRecipesOutputBoundary;
import use_case.saved_recipes.ShowSavedRecipesOutputData;

public class SavedRecipesPresenter implements ShowSavedRecipesOutputBoundary {

    private final SavedRecipesViewModel viewModel;

    public SavedRecipesPresenter(SavedRecipesViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccess(ShowSavedRecipesOutputData data) {
        SavedRecipesState state = new SavedRecipesState();
        state.setRecipes(data.getSaved_recipes());
        state.setError(null);

        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailure(String message) {
        SavedRecipesState state = new SavedRecipesState();
        state.setError(message);

        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }
}
