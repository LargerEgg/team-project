package use_case;

import entity.Recipe;
import entity.Ingredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import use_case.view_recipe.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ViewRecipeInteractorTest {

    private TestViewRecipeDataAccess dataAccess;
    private TestViewRecipePresenter presenter;
    private ViewRecipeInteractor interactor;

    @BeforeEach
    void setUp() {
        dataAccess = new TestViewRecipeDataAccess();
        presenter = new TestViewRecipePresenter();
        interactor = new ViewRecipeInteractor(dataAccess, presenter);
    }

    @Test
    @DisplayName("View Recipe: Success - Valid recipe ID")
    void testViewRecipeSuccess() {
        // Arrange
        String recipeId = "testRecipeId";
        
        Recipe expectedRecipe = new Recipe(
                recipeId,
                "testUser",
                "Test Recipe Title",
                "Test Description",
                new ArrayList<>(Collections.singletonList(new Ingredient("Chicken", "500g"))),
                "Test Cuisine",
                new ArrayList<>(),
                Recipe.Status.PUBLISHED,
                new Date(),
                new Date(),
                "https://example.com/image.jpg"
        );
        dataAccess.setRecipeToReturn(expectedRecipe);

        ViewRecipeInputData inputData = new ViewRecipeInputData(recipeId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.successViewCalled);
        assertFalse(presenter.failViewCalled);
        assertNotNull(presenter.outputData);
        Recipe actualRecipe = presenter.outputData.getRecipe();
        assertNotNull(actualRecipe);
        assertEquals(expectedRecipe.getRecipeId(), actualRecipe.getRecipeId());
        assertEquals(expectedRecipe.getTitle(), actualRecipe.getTitle());
        assertEquals(expectedRecipe.getDescription(), actualRecipe.getDescription());
    }

    @Test
    @DisplayName("View Recipe: Failure - Recipe not found")
    void testViewRecipeFailureNotFound() {
        // Arrange
        String nonExistentRecipeId = "nonExistentId";
        dataAccess.setRecipeToReturn(null); // Simulate recipe not found

        ViewRecipeInputData inputData = new ViewRecipeInputData(nonExistentRecipeId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertFalse(presenter.successViewCalled);
        assertEquals("Recipe not found.", presenter.errorMessage);
    }

    // Test double for ViewRecipeDataAccessInterface
    private static class TestViewRecipeDataAccess implements ViewRecipeDataAccessInterface {
        private Recipe recipeToReturn;

        public void setRecipeToReturn(Recipe recipe) {
            this.recipeToReturn = recipe;
        }

        @Override
        public Recipe findById(String recipeId) {
            return recipeToReturn;
        }

        @Override
        public void recordView(String recipeId) {
            // Do nothing for testing purposes
        }
    }

    // Test double for ViewRecipeOutputBoundary
    private static class TestViewRecipePresenter implements ViewRecipeOutputBoundary {
        boolean successViewCalled = false;
        boolean failViewCalled = false;
        ViewRecipeOutputData outputData = null;
        String errorMessage = null;

        @Override
        public void prepareSuccessView(ViewRecipeOutputData outputData) {
            successViewCalled = true;
            this.outputData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            failViewCalled = true;
            this.errorMessage = errorMessage;
        }
    }
}
