package interface_adapter.recommend_recipe;

import use_case.recommend_recipe.RecommendRecipeInputBoundary;
import use_case.recommend_recipe.RecommendRecipeInputData;

public class RecommendRecipeController {

    final RecommendRecipeInputBoundary recommendRecipeUseCaseInteractor;

    public RecommendRecipeController(RecommendRecipeInputBoundary recommendRecipeUseCaseInteractor) {
        this.recommendRecipeUseCaseInteractor = recommendRecipeUseCaseInteractor;
    }

    /**
     * Executes the "Recommend Recipe" Use Case.
     * This method is triggered when the user clicks the "Recommend" button on the Main View.
     *
     * @param username The username of the currently logged-in user.
     */
    public void execute(String username) {
        // 1. Wrap the raw data (username) into an InputData object
        RecommendRecipeInputData inputData = new RecommendRecipeInputData(username);

        // 2. Delegate the execution to the Interactor
        recommendRecipeUseCaseInteractor.execute(inputData);
    }
}