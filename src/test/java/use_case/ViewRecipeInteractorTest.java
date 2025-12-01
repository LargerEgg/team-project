package use_case;

import entity.Ingredient;
import entity.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import use_case.save_recipe.SaveRecipeDataAccessInterface;
import use_case.view_recipe.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ViewRecipeInteractorTest {

    private TestViewRecipeDataAccess dataAccess;
    private TestViewRecipePresenter presenter;
    private TestSaveRecipeDataAccess saveRecipeDataAccess;
    private ViewRecipeInteractor interactor;

    @BeforeEach
    void setUp() {
        dataAccess = new TestViewRecipeDataAccess();
        presenter = new TestViewRecipePresenter();
        saveRecipeDataAccess = new TestSaveRecipeDataAccess();
        interactor = new ViewRecipeInteractor(dataAccess, presenter, saveRecipeDataAccess);
    }

    @Test
    @DisplayName("View Recipe: Success - Valid recipe ID")
    void testViewRecipeSuccess() {
        // Arrange
        String recipeId = "testRecipeId";
        String username = "testUser";

        Recipe expectedRecipe = createTestRecipe(recipeId, username);
        dataAccess.setRecipeToReturn(expectedRecipe);

        ViewRecipeInputData inputData = new ViewRecipeInputData(expectedRecipe, username);

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
        String username = "testUser";
        ViewRecipeInputData inputData = new ViewRecipeInputData(null, username);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertFalse(presenter.successViewCalled);
        assertEquals("Recipe not found.", presenter.errorMessage);
    }

    @Test
    @DisplayName("View Recipe: Success - Null username")
    void testViewRecipeSuccessNullUsername() {
        // Arrange
        String recipeId = "testRecipeId";
        Recipe expectedRecipe = createTestRecipe(recipeId, null);
        dataAccess.setRecipeToReturn(expectedRecipe);
        ViewRecipeInputData inputData = new ViewRecipeInputData(expectedRecipe, null);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.successViewCalled);
        assertFalse(presenter.failViewCalled);
        assertNotNull(presenter.outputData);
        assertNull(presenter.outputData.getUsername());
    }

    @Test
    @DisplayName("View Recipe: Success - Empty username")
    void testViewRecipeSuccessEmptyUsername() {
        // Arrange
        String recipeId = "testRecipeId";
        Recipe expectedRecipe = createTestRecipe(recipeId, "");
        dataAccess.setRecipeToReturn(expectedRecipe);
        ViewRecipeInputData inputData = new ViewRecipeInputData(expectedRecipe, "");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.successViewCalled);
        assertFalse(presenter.failViewCalled);
        assertNotNull(presenter.outputData);
        assertEquals("", presenter.outputData.getUsername());
    }

    @Test
    @DisplayName("View Recipe: Database error on recordView")
    void testRecordViewDatabaseError() throws InterruptedException {
        // Arrange
        String recipeId = "testRecipeId";
        String username = "testUser";
        Recipe expectedRecipe = createTestRecipe(recipeId, username);
        dataAccess.setRecipeToReturn(expectedRecipe);
        dataAccess.setShouldThrowException(true); // Configure to throw an exception
        ViewRecipeInputData inputData = new ViewRecipeInputData(expectedRecipe, username);

        // Act
        interactor.execute(inputData);
        dataAccess.await(); // Wait for the async operation to complete

        // Assert
        assertTrue(presenter.successViewCalled); // The view should still succeed
        // We can't directly assert the exception was thrown, but we can check if it was logged (if we had logging)
        // For now, we just ensure the main flow completes.
    }

    private Recipe createTestRecipe(String recipeId, String username) {
        return new Recipe(
                recipeId,
                username,
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
    }

    // Test double for ViewRecipeDataAccessInterface
    private static class TestViewRecipeDataAccess implements ViewRecipeDataAccessInterface {
        private Recipe recipeToReturn;
        private boolean shouldThrowException = false;
        private final CountDownLatch latch = new CountDownLatch(1);

        public void setRecipeToReturn(Recipe recipe) {
            this.recipeToReturn = recipe;
        }

        public void setShouldThrowException(boolean shouldThrow) {
            this.shouldThrowException = shouldThrow;
        }

        @Override
        public Recipe findById(String recipeId) {
            return recipeToReturn;
        }

        @Override
        public void recordView(String recipeId) {
            try {
                if (shouldThrowException) {
                    throw new RuntimeException("Database error");
                }
            } finally {
                latch.countDown();
            }
        }

        public void await() throws InterruptedException {
            latch.await(1, TimeUnit.SECONDS);
        }
    }

    // Test double for SaveRecipeDataAccessInterface
    private static class TestSaveRecipeDataAccess implements SaveRecipeDataAccessInterface {
        private boolean isSaved = false;

        public void setIsSaved(boolean isSaved) {
            this.isSaved = isSaved;
        }

        @Override
        public boolean isRecipeSaved(String username, String recipeID) {
            return isSaved;
        }

        @Override
        public void saveRecipe(String username, String recipeID) {
            // Do nothing for testing purposes
        }

        @Override
        public void unsaveRecipe(String username, String recipeID) {
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
