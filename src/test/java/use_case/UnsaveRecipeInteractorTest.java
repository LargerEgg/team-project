package use_case;

import use_case.unsave_recipe.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for UnsaveRecipeInteractor.
 * This test suite aims for 100% code coverage, including edge cases and exception handling.
 */
class UnsaveRecipeInteractorTest {

    // ==============================================================================
    // Helper Classes (Stubs & Fakes)
    // ==============================================================================

    /**
     * A simple Stub for the Data Access Interface.
     */
    static class DataAccessStub implements UnsaveRecipeDataAccessInterface {
        private boolean shouldThrowException = false;
        private String exceptionMessage = "Database error";
        
        // Track method calls for verification
        String lastUnsavedUsername;
        String lastUnsavedRecipeId;
        boolean unsaveCalled = false;

        public void setShouldThrowException(boolean shouldThrow, String message) {
            this.shouldThrowException = shouldThrow;
            this.exceptionMessage = message;
        }

        @Override
        public void unsave(String username, String recipeId) {
            unsaveCalled = true;
            lastUnsavedUsername = username;
            lastUnsavedRecipeId = recipeId;
            if (shouldThrowException) {
                throw new RuntimeException(exceptionMessage);
            }
        }
    }

    /**
     * A capturing fake for the Output Boundary (Presenter).
     */
    static class TestPresenter implements UnsaveRecipeOutputBoundary {
        String successMessage;
        String failMessage;
        
        boolean prepareSuccessViewCalled = false;
        boolean prepareFailViewCalled = false;

        @Override
        public void prepareSuccessView(String message) {
            this.successMessage = message;
            this.prepareSuccessViewCalled = true;
        }

        @Override
        public void prepareFailView(String error) {
            this.failMessage = error;
            this.prepareFailViewCalled = true;
        }
    }

    // ==============================================================================
    // Test Cases for UnsaveRecipeInteractor
    // ==============================================================================

    /**
     * Test Case 1: Unsave Recipe Success
     * When unsave operation succeeds, prepareSuccessView should be called.
     */
    @Test
    void testExecute_UnsaveSuccess() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        TestPresenter presenter = new TestPresenter();
        
        UnsaveRecipeInteractor interactor = new UnsaveRecipeInteractor(dao, presenter);

        // Act
        interactor.execute("testUser", "recipe123");

        // Assert
        assertTrue(presenter.prepareSuccessViewCalled, "prepareSuccessView should be called");
        assertFalse(presenter.prepareFailViewCalled, "prepareFailView should not be called");
        
        assertEquals("Recipe unsaved successfully.", presenter.successMessage);
        
        // Verify DAO was called correctly
        assertTrue(dao.unsaveCalled);
        assertEquals("testUser", dao.lastUnsavedUsername);
        assertEquals("recipe123", dao.lastUnsavedRecipeId);
    }

    /**
     * Test Case 2: Exception Handling
     * When unsave operation throws exception, prepareFailView should be called.
     */
    @Test
    void testExecute_ExceptionDuringUnsave() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        dao.setShouldThrowException(true, "Connection timeout");
        TestPresenter presenter = new TestPresenter();
        
        UnsaveRecipeInteractor interactor = new UnsaveRecipeInteractor(dao, presenter);

        // Act
        interactor.execute("errorUser", "recipe456");

        // Assert
        assertTrue(presenter.prepareFailViewCalled, "prepareFailView should be called");
        assertFalse(presenter.prepareSuccessViewCalled, "prepareSuccessView should not be called");
        
        assertEquals("Failed to unsave recipe: Connection timeout", presenter.failMessage);
    }

    /**
     * Test Case 3: Exception with different error message
     */
    @Test
    void testExecute_ExceptionWithDifferentMessage() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        dao.setShouldThrowException(true, "Recipe not found");
        TestPresenter presenter = new TestPresenter();
        
        UnsaveRecipeInteractor interactor = new UnsaveRecipeInteractor(dao, presenter);

        // Act
        interactor.execute("user", "nonExistentRecipe");

        // Assert
        assertTrue(presenter.prepareFailViewCalled);
        assertEquals("Failed to unsave recipe: Recipe not found", presenter.failMessage);
    }

    /**
     * Test Case 4: Constructor test
     * Verify dependencies are properly injected.
     */
    @Test
    void testConstructor() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        TestPresenter presenter = new TestPresenter();

        // Act
        UnsaveRecipeInteractor interactor = new UnsaveRecipeInteractor(dao, presenter);

        // Assert
        assertNotNull(interactor, "Interactor should be created successfully");
    }

    /**
     * Test Case 5: Test with different usernames and recipe IDs
     */
    @Test
    void testExecute_DifferentUsers() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        TestPresenter presenter = new TestPresenter();
        UnsaveRecipeInteractor interactor = new UnsaveRecipeInteractor(dao, presenter);

        // Test with various usernames
        String[][] testCases = {
                {"user1", "recipeA"},
                {"admin", "recipeB"},
                {"test@email.com", "recipe123"},
                {"用户名", "菜谱ID"}
        };
        
        for (String[] testCase : testCases) {
            String username = testCase[0];
            String recipeId = testCase[1];
            
            // Reset presenter
            presenter.prepareSuccessViewCalled = false;
            dao.unsaveCalled = false;
            
            // Act
            interactor.execute(username, recipeId);
            
            // Assert
            assertEquals(username, dao.lastUnsavedUsername);
            assertEquals(recipeId, dao.lastUnsavedRecipeId);
            assertTrue(presenter.prepareSuccessViewCalled);
            assertTrue(dao.unsaveCalled);
        }
    }

    /**
     * Test Case 6: Multiple consecutive unsave operations
     */
    @Test
    void testExecute_MultipleUnsaveOperations() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        TestPresenter presenter = new TestPresenter();
        UnsaveRecipeInteractor interactor = new UnsaveRecipeInteractor(dao, presenter);

        // First unsave
        interactor.execute("user1", "recipe1");
        assertTrue(presenter.prepareSuccessViewCalled);
        assertEquals("user1", dao.lastUnsavedUsername);
        assertEquals("recipe1", dao.lastUnsavedRecipeId);

        // Reset
        presenter.prepareSuccessViewCalled = false;

        // Second unsave
        interactor.execute("user2", "recipe2");
        assertTrue(presenter.prepareSuccessViewCalled);
        assertEquals("user2", dao.lastUnsavedUsername);
        assertEquals("recipe2", dao.lastUnsavedRecipeId);
    }

    /**
     * Test Case 7: Empty strings as parameters
     */
    @Test
    void testExecute_EmptyStrings() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        TestPresenter presenter = new TestPresenter();
        UnsaveRecipeInteractor interactor = new UnsaveRecipeInteractor(dao, presenter);

        // Act
        interactor.execute("", "");

        // Assert
        assertTrue(presenter.prepareSuccessViewCalled);
        assertEquals("", dao.lastUnsavedUsername);
        assertEquals("", dao.lastUnsavedRecipeId);
    }
}

