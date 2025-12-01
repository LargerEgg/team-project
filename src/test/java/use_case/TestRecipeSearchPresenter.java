package use_case;

import use_case.recipe_search.RecipeSearchOutputBoundary;
import use_case.recipe_search.RecipeSearchOutputData;

public class TestRecipeSearchPresenter implements RecipeSearchOutputBoundary {
    public boolean successViewCalled;
    public boolean failViewCalled;
    public RecipeSearchOutputData outputData;
    public String errorMessage;

    public TestRecipeSearchPresenter() {
        System.out.println("TestRecipeSearchPresenter: Constructor called.");
        this.successViewCalled = false;
        this.failViewCalled = false;
        this.outputData = null;
        this.errorMessage = null;
    }

    @Override
    public void prepareSuccessView(RecipeSearchOutputData outputData) {
        System.out.println("TestRecipeSearchPresenter: prepareSuccessView called.");
        successViewCalled = true;
        this.outputData = outputData;
    }

    @Override
    public void prepareFailView(String errorMessage) {
        System.out.println("TestRecipeSearchPresenter: prepareFailView called with error: " + errorMessage);
        failViewCalled = true;
        this.errorMessage = errorMessage;
    }

    @Override
    public void prepareProgressView(RecipeSearchOutputData progressData) {
        // Not used in these tests, but must be implemented
    }
}
