package interface_adapter.recommend_recipe;

import interface_adapter.ViewManagerModel;
import use_case.recommend_recipe.RecommendRecipeOutputBoundary;
import use_case.recommend_recipe.RecommendRecipeOutputData;

/*
N.
 */
public class RecommendRecipePresenter implements RecommendRecipeOutputBoundary {

    private final RecommendRecipeViewModel recommendRecipeViewModel;
    private final ViewManagerModel viewManagerModel;

    public RecommendRecipePresenter(RecommendRecipeViewModel recommendRecipeViewModel,
                                    ViewManagerModel viewManagerModel) {
        this.recommendRecipeViewModel = recommendRecipeViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(RecommendRecipeOutputData response) {
        RecommendRecipeState state = recommendRecipeViewModel.getState();
        state.setRecommendations(response.getRecipes());
        state.setRecommendationError(null);

        this.recommendRecipeViewModel.setState(state);
        this.recommendRecipeViewModel.firePropertyChanged();

        this.viewManagerModel.setState(recommendRecipeViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        RecommendRecipeState state = recommendRecipeViewModel.getState();
        state.setRecommendationError(error);

        this.recommendRecipeViewModel.setState(state);
        this.recommendRecipeViewModel.firePropertyChanged();
    }
}