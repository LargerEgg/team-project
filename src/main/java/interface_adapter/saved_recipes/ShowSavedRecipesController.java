package interface_adapter.saved_recipes;

import use_case.saved_recipes.ShowSavedRecipesInputBoundary;
import use_case.saved_recipes.ShowSavedRecipesInputData;

public class ShowSavedRecipesController {

    private final ShowSavedRecipesInputBoundary interactor;

    public ShowSavedRecipesController(ShowSavedRecipesInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String username) {
        ShowSavedRecipesInputData input = new ShowSavedRecipesInputData(username);
        interactor.execute(input);
    }
}
