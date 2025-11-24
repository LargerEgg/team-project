package interface_adapter.view_recipe;

import interface_adapter.ViewManagerModel;
import use_case.view_recipe.ViewRecipeOutputBoundary;
import use_case.view_recipe.ViewRecipeOutputData;

public class ViewRecipePresenter implements ViewRecipeOutputBoundary {

    private final ViewRecipeViewModel viewRecipeViewModel;
    private final ViewManagerModel viewManagerModel;

    public ViewRecipePresenter(ViewManagerModel viewManagerModel,
                              ViewRecipeViewModel viewRecipeViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.viewRecipeViewModel = viewRecipeViewModel;
    }

    @Override
    public void prepareSuccessView(ViewRecipeOutputData outputData) {
        final ViewRecipeState viewRecipeState = viewRecipeViewModel.getState();
        viewRecipeState.setTitle(outputData.getTitle());
        viewRecipeState.setRecipeId(outputData.getRecipeId());
        viewRecipeState.setViews(outputData.getViews());
        viewRecipeState.setSaves(outputData.getSaves());
        viewRecipeState.setAverageRating(outputData.getAverageRating());
        viewRecipeState.setViewError("");
        viewRecipeViewModel.firePropertyChange();

        // Switch to the view recipe view
        viewManagerModel.setState(viewRecipeViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        final ViewRecipeState viewRecipeState = viewRecipeViewModel.getState();
        viewRecipeState.setViewError(error);
        viewRecipeViewModel.firePropertyChange();
    }
}

