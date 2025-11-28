package use_case.saved_recipes;

import entity.Recipe;
import use_case.save_recipe.SaveRecipeDataAccessInterface;

import java.util.List;

public class ShowSavedRecipesInteractor implements ShowSavedRecipesInputBoundary {

    private final ShowSavedRecipesDataAccessInterface savedRecipeDAO;
    private final ShowSavedRecipesOutputBoundary presenter;

    public ShowSavedRecipesInteractor(ShowSavedRecipesDataAccessInterface savedRecipeDAO, ShowSavedRecipesOutputBoundary presenter) {
        this.savedRecipeDAO = savedRecipeDAO;
        this.presenter = presenter;
    }

    @Override
    public void execute(ShowSavedRecipesInputData inputData) {
        String username = inputData.getUsername();

        try {
            List<Recipe> recipes = savedRecipeDAO.getRecipes(username); // Create Data Access Interface for this and implement it for same DAO as save recipes use case

            ShowSavedRecipesOutputData outputData = new ShowSavedRecipesOutputData(recipes);

            presenter.prepareSuccess(outputData);
        } catch (Exception e) {
            presenter.prepareFailure("Failed to load saved recipes" + e.getMessage());
        }
    }

}
