package use_case.unsave_recipe;

public class UnsaveRecipeInteractor implements UnsaveRecipeInputBoundary {

    private final UnsaveRecipeDataAccessInterface recipeDao;
    private final UnsaveRecipeOutputBoundary presenter;

    public UnsaveRecipeInteractor(UnsaveRecipeDataAccessInterface recipeDao, UnsaveRecipeOutputBoundary presenter) {
        this.recipeDao = recipeDao;
        this.presenter = presenter;
    }

    @Override
    public void execute(String username, String recipeId) {
        try {
            recipeDao.unsave(username, recipeId);
            presenter.prepareSuccessView("Recipe unsaved successfully.");
        } catch (Exception e) {
            presenter.prepareFailView("Failed to unsave recipe: " + e.getMessage());
        }
    }
}
