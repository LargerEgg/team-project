package use_case.recommend_recipe;

public interface RecommendRecipeInputBoundary {

    /**
     * Executes the recommendation business logic.
     * This method is called by the Controller to start the use case.
     * * @param inputData the data passed from the controller (contains username/id)
     */
    void execute(RecommendRecipeInputData inputData);

}
