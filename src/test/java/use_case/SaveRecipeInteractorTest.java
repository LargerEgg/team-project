package use_case;

import entity.Recipe;
import use_case.save_recipe.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for SaveRecipeInteractor.
 * This test suite aims for 100% code coverage, including edge cases and exception handling.
 */
class SaveRecipeInteractorTest {

    // ==============================================================================
    // Helper Classes (Stubs & Fakes)
    // ==============================================================================

    /**
     * A concrete implementation of Recipe for testing purposes.
     */
    static Recipe createTestRecipe(String recipeId, String title) {
        return new Recipe(
                recipeId,               // recipeId
                "testAuthor",           // authorId
                title,                  // title
                "Test description",     // description
                new ArrayList<>(),      // ingredients
                "TestCategory",         // category
                new ArrayList<>(),      // tags
                Recipe.Status.PUBLISHED,// status
                new Date(),             // creationDate
                new Date(),             // updateDate
                "image/path"            // imagePath
        );
    }

    /**
     * A simple Stub for the Data Access Interface.
     * Tracks the state of saved recipes internally.
     */
    static class DataAccessStub implements SaveRecipeDataAccessInterface {
        private boolean recipeSaved = false;
        private boolean shouldThrowException = false;
        private String exceptionMessage = "Database error";
        
        // Track method calls for verification
        String lastSavedUsername;
        String lastSavedRecipeId;
        String lastUnsavedUsername;
        String lastUnsavedRecipeId;

        public void setRecipeSaved(boolean saved) {
            this.recipeSaved = saved;
        }

        public void setShouldThrowException(boolean shouldThrow, String message) {
            this.shouldThrowException = shouldThrow;
            this.exceptionMessage = message;
        }

        @Override
        public boolean isRecipeSaved(String username, String recipeID) {
            if (shouldThrowException) {
                throw new RuntimeException(exceptionMessage);
            }
            return recipeSaved;
        }

        @Override
        public void saveRecipe(String username, String recipeID) {
            if (shouldThrowException) {
                throw new RuntimeException(exceptionMessage);
            }
            lastSavedUsername = username;
            lastSavedRecipeId = recipeID;
            recipeSaved = true;
        }

        @Override
        public void unsaveRecipe(String username, String recipeID) {
            if (shouldThrowException) {
                throw new RuntimeException(exceptionMessage);
            }
            lastUnsavedUsername = username;
            lastUnsavedRecipeId = recipeID;
            recipeSaved = false;
        }
    }

    /**
     * A capturing fake for the Output Boundary (Presenter).
     * Allows verification of what data was passed to each method.
     */
    static class TestPresenter implements SaveRecipeOutputBoundary {
        SaveRecipeOutputData successData;
        SaveRecipeOutputData unsaveData;
        String failureMessage;
        
        boolean prepareSuccessCalled = false;
        boolean prepareUnsaveCalled = false;
        boolean prepareFailureCalled = false;

        @Override
        public void prepareSuccess(SaveRecipeOutputData saveRecipeOutputData) {
            this.successData = saveRecipeOutputData;
            this.prepareSuccessCalled = true;
        }

        @Override
        public void prepareUnsave(SaveRecipeOutputData saveRecipeOutputData) {
            this.unsaveData = saveRecipeOutputData;
            this.prepareUnsaveCalled = true;
        }

        @Override
        public void prepareFailure(String errorMessage) {
            this.failureMessage = errorMessage;
            this.prepareFailureCalled = true;
        }
    }

    // ==============================================================================
    // Test Cases for SaveRecipeInteractor
    // ==============================================================================

    /**
     * Test Case 1: Save Recipe Success
     * When recipe is NOT already saved, it should be saved and prepareSuccess is called.
     */
    @Test
    void testExecute_SaveRecipeSuccess() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        dao.setRecipeSaved(false); // Recipe is not saved initially
        TestPresenter presenter = new TestPresenter();
        
        SaveRecipeInteractor interactor = new SaveRecipeInteractor(dao, presenter);
        
        Recipe recipe = createTestRecipe("recipe123", "Delicious Pasta");
        SaveRecipeInputData inputData = new SaveRecipeInputData("testUser", recipe);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.prepareSuccessCalled, "prepareSuccess should be called");
        assertFalse(presenter.prepareUnsaveCalled, "prepareUnsave should not be called");
        assertFalse(presenter.prepareFailureCalled, "prepareFailure should not be called");
        
        assertNotNull(presenter.successData);
        assertEquals("Delicious Pasta", presenter.successData.getRecipeName());
        assertTrue(presenter.successData.isSaved(), "Recipe should be marked as saved");
        
        // Verify DAO was called correctly
        assertEquals("testUser", dao.lastSavedUsername);
        assertEquals("recipe123", dao.lastSavedRecipeId);
    }

    /**
     * Test Case 2: Unsave Recipe Success
     * When recipe IS already saved, it should be unsaved and prepareUnsave is called.
     */
    @Test
    void testExecute_UnsaveRecipeSuccess() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        dao.setRecipeSaved(true); // Recipe is already saved
        TestPresenter presenter = new TestPresenter();
        
        SaveRecipeInteractor interactor = new SaveRecipeInteractor(dao, presenter);
        
        Recipe recipe = createTestRecipe("recipe456", "Tasty Salad");
        SaveRecipeInputData inputData = new SaveRecipeInputData("anotherUser", recipe);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.prepareUnsaveCalled, "prepareUnsave should be called");
        assertFalse(presenter.prepareSuccessCalled, "prepareSuccess should not be called");
        assertFalse(presenter.prepareFailureCalled, "prepareFailure should not be called");
        
        assertNotNull(presenter.unsaveData);
        assertEquals("Tasty Salad", presenter.unsaveData.getRecipeName());
        assertFalse(presenter.unsaveData.isSaved(), "Recipe should be marked as not saved");
        
        // Verify DAO was called correctly
        assertEquals("anotherUser", dao.lastUnsavedUsername);
        assertEquals("recipe456", dao.lastUnsavedRecipeId);
    }

    /**
     * Test Case 3: Exception Handling - isRecipeSaved throws exception
     * When an exception occurs during isRecipeSaved check, prepareFailure should be called.
     */
    @Test
    void testExecute_ExceptionDuringIsRecipeSavedCheck() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        dao.setShouldThrowException(true, "Connection timeout");
        TestPresenter presenter = new TestPresenter();
        
        SaveRecipeInteractor interactor = new SaveRecipeInteractor(dao, presenter);
        
        Recipe recipe = createTestRecipe("recipe789", "Grilled Chicken");
        SaveRecipeInputData inputData = new SaveRecipeInputData("errorUser", recipe);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.prepareFailureCalled, "prepareFailure should be called");
        assertFalse(presenter.prepareSuccessCalled, "prepareSuccess should not be called");
        assertFalse(presenter.prepareUnsaveCalled, "prepareUnsave should not be called");
        
        assertEquals("Failed to save recipe: Connection timeout", presenter.failureMessage);
    }

    /**
     * Test Case 4: Exception during save operation
     * When save operation throws exception, prepareFailure should be called.
     */
    @Test
    void testExecute_ExceptionDuringSaveOperation() {
        // Arrange
        DataAccessStub dao = new DataAccessStub() {
            @Override
            public boolean isRecipeSaved(String username, String recipeID) {
                return false; // Not saved, will trigger save
            }
            
            @Override
            public void saveRecipe(String username, String recipeID) {
                throw new RuntimeException("Save operation failed");
            }
        };
        TestPresenter presenter = new TestPresenter();
        
        SaveRecipeInteractor interactor = new SaveRecipeInteractor(dao, presenter);
        
        Recipe recipe = createTestRecipe("recipe101", "Fish Tacos");
        SaveRecipeInputData inputData = new SaveRecipeInputData("saveErrorUser", recipe);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.prepareFailureCalled, "prepareFailure should be called");
        assertEquals("Failed to save recipe: Save operation failed", presenter.failureMessage);
    }

    /**
     * Test Case 5: Exception during unsave operation
     * When unsave operation throws exception, prepareFailure should be called.
     */
    @Test
    void testExecute_ExceptionDuringUnsaveOperation() {
        // Arrange
        DataAccessStub dao = new DataAccessStub() {
            @Override
            public boolean isRecipeSaved(String username, String recipeID) {
                return true; // Already saved, will trigger unsave
            }
            
            @Override
            public void unsaveRecipe(String username, String recipeID) {
                throw new RuntimeException("Unsave operation failed");
            }
        };
        TestPresenter presenter = new TestPresenter();
        
        SaveRecipeInteractor interactor = new SaveRecipeInteractor(dao, presenter);
        
        Recipe recipe = createTestRecipe("recipe202", "Caesar Salad");
        SaveRecipeInputData inputData = new SaveRecipeInputData("unsaveErrorUser", recipe);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.prepareFailureCalled, "prepareFailure should be called");
        assertEquals("Failed to save recipe: Unsave operation failed", presenter.failureMessage);
    }

    /**
     * Test Case 6: Constructor test - verify dependencies are properly injected
     */
    @Test
    void testConstructor() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        TestPresenter presenter = new TestPresenter();

        // Act
        SaveRecipeInteractor interactor = new SaveRecipeInteractor(dao, presenter);

        // Assert
        assertNotNull(interactor, "Interactor should be created successfully");
    }

    // ==============================================================================
    // Test Cases for SaveRecipeInputData
    // ==============================================================================

    /**
     * Test SaveRecipeInputData getUsername method
     */
    @Test
    void testInputData_GetUsername() {
        // Arrange
        Recipe recipe = createTestRecipe("recipe1", "Test Recipe");
        SaveRecipeInputData inputData = new SaveRecipeInputData("testUser123", recipe);

        // Act & Assert
        assertEquals("testUser123", inputData.getUsername());
    }

    /**
     * Test SaveRecipeInputData getRecipe method
     */
    @Test
    void testInputData_GetRecipe() {
        // Arrange
        Recipe recipe = createTestRecipe("recipe2", "Another Recipe");
        SaveRecipeInputData inputData = new SaveRecipeInputData("user456", recipe);

        // Act & Assert
        assertEquals(recipe, inputData.getRecipe());
        assertEquals("recipe2", inputData.getRecipe().getRecipeId());
        assertEquals("Another Recipe", inputData.getRecipe().getTitle());
    }

    // ==============================================================================
    // Test Cases for SaveRecipeOutputData
    // ==============================================================================

    /**
     * Test SaveRecipeOutputData getRecipeName method
     */
    @Test
    void testOutputData_GetRecipeName() {
        // Arrange
        SaveRecipeOutputData outputData = new SaveRecipeOutputData("Chocolate Cake", true);

        // Act & Assert
        assertEquals("Chocolate Cake", outputData.getRecipeName());
    }

    /**
     * Test SaveRecipeOutputData isSaved method when saved is true
     */
    @Test
    void testOutputData_IsSaved_True() {
        // Arrange
        SaveRecipeOutputData outputData = new SaveRecipeOutputData("Apple Pie", true);

        // Act & Assert
        assertTrue(outputData.isSaved());
    }

    /**
     * Test SaveRecipeOutputData isSaved method when saved is false
     */
    @Test
    void testOutputData_IsSaved_False() {
        // Arrange
        SaveRecipeOutputData outputData = new SaveRecipeOutputData("Banana Bread", false);

        // Act & Assert
        assertFalse(outputData.isSaved());
    }

    /**
     * Test SaveRecipeOutputData constructor with both parameters
     */
    @Test
    void testOutputData_Constructor() {
        // Arrange & Act
        SaveRecipeOutputData outputData1 = new SaveRecipeOutputData("Recipe A", true);
        SaveRecipeOutputData outputData2 = new SaveRecipeOutputData("Recipe B", false);

        // Assert
        assertEquals("Recipe A", outputData1.getRecipeName());
        assertTrue(outputData1.isSaved());
        
        assertEquals("Recipe B", outputData2.getRecipeName());
        assertFalse(outputData2.isSaved());
    }

    // ==============================================================================
    // Additional Edge Cases
    // ==============================================================================

    /**
     * Test with different user names to ensure proper delegation
     */
    @Test
    void testExecute_DifferentUsers() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        dao.setRecipeSaved(false);
        TestPresenter presenter = new TestPresenter();
        
        SaveRecipeInteractor interactor = new SaveRecipeInteractor(dao, presenter);

        // Test with various usernames
        String[] usernames = {"user1", "admin", "test@email.com", "用户名"};
        
        for (String username : usernames) {
            Recipe recipe = createTestRecipe("recipeId", "Recipe Title");
            SaveRecipeInputData inputData = new SaveRecipeInputData(username, recipe);
            
            // Reset state
            dao.setRecipeSaved(false);
            presenter.prepareSuccessCalled = false;
            
            // Act
            interactor.execute(inputData);
            
            // Assert
            assertEquals(username, dao.lastSavedUsername);
            assertTrue(presenter.prepareSuccessCalled);
        }
    }

    /**
     * Test toggle behavior - save then unsave
     */
    @Test
    void testExecute_ToggleBehavior() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        dao.setRecipeSaved(false); // Start as not saved
        TestPresenter presenter = new TestPresenter();
        
        SaveRecipeInteractor interactor = new SaveRecipeInteractor(dao, presenter);
        
        Recipe recipe = createTestRecipe("toggleRecipe", "Toggle Test Recipe");
        SaveRecipeInputData inputData = new SaveRecipeInputData("toggleUser", recipe);

        // First execution - should save
        interactor.execute(inputData);
        assertTrue(presenter.prepareSuccessCalled, "First call should save");
        assertTrue(presenter.successData.isSaved());
        
        // Reset presenter flags
        presenter.prepareSuccessCalled = false;
        presenter.prepareUnsaveCalled = false;
        
        // Second execution - should unsave (dao now has recipeSaved = true)
        interactor.execute(inputData);
        assertTrue(presenter.prepareUnsaveCalled, "Second call should unsave");
        assertFalse(presenter.unsaveData.isSaved());
    }
}

