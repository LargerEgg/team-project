package use_case.recommend_recipe;

public interface RecommendRecipeOutputBoundary {

    /**
     * Called when the recommendation process is successful.
     * The Interactor passes the output data (list of recipes) to the Presenter.
     * * @param outputData the data package containing recommendations
     */
    void prepareSuccessView(RecommendRecipeOutputData outputData);

    /**
     * Called when the recommendation process fails.
     * (e.g., user has no favorites to analyze, or API connection failed).
     * * @param errorMessage a string describing why it failed
     */
    void prepareFailView(String errorMessage);

}
