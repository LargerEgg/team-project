package interface_adapter.recommend_recipe;

import use_case.recommend_recipe.RecommendRecipeOutputBoundary;
import use_case.recommend_recipe.RecommendRecipeOutputData;

public class RecommendRecipePresenter implements RecommendRecipeOutputBoundary {

    private final RecommendRecipeViewModel recommendRecipeViewModel;

    public RecommendRecipePresenter(RecommendRecipeViewModel recommendRecipeViewModel) {
        this.recommendRecipeViewModel = recommendRecipeViewModel;
    }

    @Override
    public void prepareSuccessView(RecommendRecipeOutputData response) {
        // 1. Get the current State
        RecommendRecipeState state = recommendRecipeViewModel.getState();

        // 2. Update the data in State (fill in recommended recipes)
        state.setRecommendations(response.getRecipes());
        // Clear any previous error messages
        state.setRecommendationError(null);

        // 3. Save the updated State back to the ViewModel
        this.recommendRecipeViewModel.setState(state);

        // 4. Notify the View to refresh (View listens for "state" changes)
        this.recommendRecipeViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        // 1. Get the current State
        RecommendRecipeState state = recommendRecipeViewModel.getState();

        // 2. Update the error message in State
        state.setRecommendationError(error);

        // 3. Save the updated State back to the ViewModel
        this.recommendRecipeViewModel.setState(state);

        // 4. Notify the View to show an error alert
        this.recommendRecipeViewModel.firePropertyChanged();
    }
}