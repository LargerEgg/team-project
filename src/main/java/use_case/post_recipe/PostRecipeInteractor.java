package use_case.post_recipe;

import entity.Ingredient;
import entity.Recipe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PostRecipeInteractor implements PostRecipeInputBoundary {

    private final PostRecipeDataAccessInterface recipeDataAccess;
    private final PostRecipeOutputBoundary presenter;

    public PostRecipeInteractor(PostRecipeDataAccessInterface recipeDataAccess, PostRecipeOutputBoundary presenter) {
        this.recipeDataAccess = recipeDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void publish(PostRecipeInputData inputData) {
        if (inputData.getTitle() == null || inputData.getTitle().isBlank()){
            presenter.prepareFailedView("Title is required", inputData);
            return;
        }
        if (inputData.getDescription() == null || inputData.getDescription().isBlank()){
            presenter.prepareFailedView("Description is required", inputData);
            return;
        }
        if (inputData.getIngredients() == null || inputData.getIngredients().isEmpty()){
            presenter.prepareFailedView("Ingredients is required", inputData);
            return;
        }

        Recipe recipe = buildRecipeFromInput(
                inputData,
                Recipe.Status.PUBLISHED
        );

        try {
            Recipe saved = recipeDataAccess.saveRecipe(recipe);
            PostDataOutputData outputData = new PostDataOutputData(saved.getRecipeId(), "Recipe published");
        } catch (RuntimeException e) {
            presenter.prepareFailedView("Failed to publish recipe", inputData);
        }
    }

    @Override
    public void saveDraft(PostRecipeInputData inputData) {
        Recipe draft = buildRecipeFromInput(inputData, Recipe.Status.DRAFT);

        Recipe saved = recipeDataAccess.saveRecipe(draft);
        PostDataOutputData outputData = new PostDataOutputData(saved.getRecipeId(), "Recipe saved");
        presenter.prepareDraftSavedView(outputData);
    }

    private Recipe buildRecipeFromInput(PostRecipeInputData inputData, Recipe.Status status) {
        // We will change this to firebase
        String recipeId = UUID.randomUUID().toString();
        Date now = new Date();

        List<Ingredient> ingredients = new ArrayList<>();
        if (inputData.getIngredients() != null){
            for (PostRecipeInputData.IngredientDTO ingredientDTO : inputData.getIngredients()){
                Ingredient ingredient = new Ingredient(ingredientDTO.getName(), ingredientDTO.getQuantity());
                ingredients.add(ingredient);
            }
        }

        return new Recipe(
                recipeId,
                inputData.getAuthorId(),
                inputData.getTitle(),
                inputData.getDescription(),
                ingredients,
                inputData.getCategory(),
                inputData.getTags(),
                status,
                now,
                now,
                inputData.getImagePath()
        );
    }


}
