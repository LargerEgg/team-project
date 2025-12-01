package use_case;

import entity.Recipe;
import use_case.saved_recipes.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for ShowSavedRecipesInteractor.
 * This test suite aims for 100% code coverage, including edge cases and exception handling.
 */
class ShowSavedRecipesInteractorTest {

    // ==============================================================================
    // Helper Classes (Stubs & Fakes)
    // ==============================================================================

    /**
     * Helper method to create a test recipe.
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
     */
    static class DataAccessStub implements ShowSavedRecipesDataAccessInterface {
        private List<Recipe> recipesToReturn = new ArrayList<>();
        private boolean shouldThrowException = false;
        private String exceptionMessage = "Database error";
        
        // Track method calls for verification
        String lastRequestedUsername;

        public void setRecipesToReturn(List<Recipe> recipes) {
            this.recipesToReturn = recipes;
        }

        public void setShouldThrowException(boolean shouldThrow, String message) {
            this.shouldThrowException = shouldThrow;
            this.exceptionMessage = message;
        }

        @Override
        public List<Recipe> getRecipes(String username) {
            lastRequestedUsername = username;
            if (shouldThrowException) {
                throw new RuntimeException(exceptionMessage);
            }
            return recipesToReturn;
        }
    }

    /**
     * A capturing fake for the Output Boundary (Presenter).
     */
    static class TestPresenter implements ShowSavedRecipesOutputBoundary {
        ShowSavedRecipesOutputData successData;
        String failureMessage;
        
        boolean prepareSuccessCalled = false;
        boolean prepareFailureCalled = false;

        @Override
        public void prepareSuccess(ShowSavedRecipesOutputData outputData) {
            this.successData = outputData;
            this.prepareSuccessCalled = true;
        }

        @Override
        public void prepareFailure(String message) {
            this.failureMessage = message;
            this.prepareFailureCalled = true;
        }
    }

    // ==============================================================================
    // Test Cases for ShowSavedRecipesInteractor
    // ==============================================================================

    /**
     * Test Case 1: Success with multiple saved recipes
     * When user has saved recipes, prepareSuccess should be called with the recipe list.
     */
    @Test
    void testExecute_SuccessWithMultipleRecipes() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        List<Recipe> savedRecipes = Arrays.asList(
                createTestRecipe("recipe1", "Pasta Carbonara"),
                createTestRecipe("recipe2", "Caesar Salad"),
                createTestRecipe("recipe3", "Grilled Salmon")
        );
        dao.setRecipesToReturn(savedRecipes);
        
        TestPresenter presenter = new TestPresenter();
        ShowSavedRecipesInteractor interactor = new ShowSavedRecipesInteractor(dao, presenter);
        
        ShowSavedRecipesInputData inputData = new ShowSavedRecipesInputData("testUser");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.prepareSuccessCalled, "prepareSuccess should be called");
        assertFalse(presenter.prepareFailureCalled, "prepareFailure should not be called");
        
        assertNotNull(presenter.successData);
        assertEquals(3, presenter.successData.getSaved_recipes().size());
        assertEquals("testUser", dao.lastRequestedUsername);
    }

    /**
     * Test Case 2: Success with empty saved recipes list
     * When user has no saved recipes, prepareSuccess should be called with empty list.
     */
    @Test
    void testExecute_SuccessWithEmptyList() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        dao.setRecipesToReturn(new ArrayList<>()); // Empty list
        
        TestPresenter presenter = new TestPresenter();
        ShowSavedRecipesInteractor interactor = new ShowSavedRecipesInteractor(dao, presenter);
        
        ShowSavedRecipesInputData inputData = new ShowSavedRecipesInputData("newUser");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.prepareSuccessCalled, "prepareSuccess should be called");
        assertFalse(presenter.prepareFailureCalled, "prepareFailure should not be called");
        
        assertNotNull(presenter.successData);
        assertTrue(presenter.successData.getSaved_recipes().isEmpty());
        assertEquals("newUser", dao.lastRequestedUsername);
    }

    /**
     * Test Case 3: Success with single saved recipe
     * When user has exactly one saved recipe.
     */
    @Test
    void testExecute_SuccessWithSingleRecipe() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        List<Recipe> savedRecipes = Arrays.asList(
                createTestRecipe("singleRecipe", "Chocolate Cake")
        );
        dao.setRecipesToReturn(savedRecipes);
        
        TestPresenter presenter = new TestPresenter();
        ShowSavedRecipesInteractor interactor = new ShowSavedRecipesInteractor(dao, presenter);
        
        ShowSavedRecipesInputData inputData = new ShowSavedRecipesInputData("cakeUser");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.prepareSuccessCalled, "prepareSuccess should be called");
        assertEquals(1, presenter.successData.getSaved_recipes().size());
        assertEquals("Chocolate Cake", presenter.successData.getSaved_recipes().get(0).getTitle());
    }

    /**
     * Test Case 4: Exception handling
     * When DAO throws an exception, prepareFailure should be called.
     */
    @Test
    void testExecute_ExceptionDuringGetRecipes() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        dao.setShouldThrowException(true, "Connection timeout");
        
        TestPresenter presenter = new TestPresenter();
        ShowSavedRecipesInteractor interactor = new ShowSavedRecipesInteractor(dao, presenter);
        
        ShowSavedRecipesInputData inputData = new ShowSavedRecipesInputData("errorUser");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.prepareFailureCalled, "prepareFailure should be called");
        assertFalse(presenter.prepareSuccessCalled, "prepareSuccess should not be called");
        
        assertEquals("Failed to load saved recipesConnection timeout", presenter.failureMessage);
    }

    /**
     * Test Case 5: Exception with different error message
     * Test different exception messages are properly forwarded.
     */
    @Test
    void testExecute_ExceptionWithDifferentMessage() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        dao.setShouldThrowException(true, "User not authorized");
        
        TestPresenter presenter = new TestPresenter();
        ShowSavedRecipesInteractor interactor = new ShowSavedRecipesInteractor(dao, presenter);
        
        ShowSavedRecipesInputData inputData = new ShowSavedRecipesInputData("unauthorizedUser");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.prepareFailureCalled);
        assertEquals("Failed to load saved recipesUser not authorized", presenter.failureMessage);
    }

    /**
     * Test Case 6: Constructor test
     * Verify dependencies are properly injected.
     */
    @Test
    void testConstructor() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        TestPresenter presenter = new TestPresenter();

        // Act
        ShowSavedRecipesInteractor interactor = new ShowSavedRecipesInteractor(dao, presenter);

        // Assert
        assertNotNull(interactor, "Interactor should be created successfully");
    }

    // ==============================================================================
    // Test Cases for ShowSavedRecipesInputData
    // ==============================================================================

    /**
     * Test ShowSavedRecipesInputData getUsername method
     */
    @Test
    void testInputData_GetUsername() {
        // Arrange
        ShowSavedRecipesInputData inputData = new ShowSavedRecipesInputData("myUsername");

        // Act & Assert
        assertEquals("myUsername", inputData.getUsername());
    }

    /**
     * Test ShowSavedRecipesInputData constructor with different usernames
     */
    @Test
    void testInputData_DifferentUsernames() {
        // Test with various usernames
        String[] usernames = {"user1", "admin", "test@email.com", "用户名", ""};
        
        for (String username : usernames) {
            ShowSavedRecipesInputData inputData = new ShowSavedRecipesInputData(username);
            assertEquals(username, inputData.getUsername());
        }
    }

    // ==============================================================================
    // Additional Edge Cases
    // ==============================================================================

    /**
     * Test with different users to ensure proper delegation
     */
    @Test
    void testExecute_DifferentUsers() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        dao.setRecipesToReturn(Arrays.asList(createTestRecipe("r1", "Test")));
        TestPresenter presenter = new TestPresenter();
        ShowSavedRecipesInteractor interactor = new ShowSavedRecipesInteractor(dao, presenter);

        // Test with various usernames
        String[] usernames = {"user1", "admin", "chef@kitchen.com"};
        
        for (String username : usernames) {
            ShowSavedRecipesInputData inputData = new ShowSavedRecipesInputData(username);
            
            // Reset presenter
            presenter.prepareSuccessCalled = false;
            
            // Act
            interactor.execute(inputData);
            
            // Assert
            assertEquals(username, dao.lastRequestedUsername);
            assertTrue(presenter.prepareSuccessCalled);
        }
    }

    /**
     * Test that recipe data is correctly passed through
     */
    @Test
    void testExecute_RecipeDataIntegrity() {
        // Arrange
        DataAccessStub dao = new DataAccessStub();
        Recipe expectedRecipe = createTestRecipe("unique123", "Unique Recipe Title");
        dao.setRecipesToReturn(Arrays.asList(expectedRecipe));
        
        TestPresenter presenter = new TestPresenter();
        ShowSavedRecipesInteractor interactor = new ShowSavedRecipesInteractor(dao, presenter);
        
        ShowSavedRecipesInputData inputData = new ShowSavedRecipesInputData("integrityUser");

        // Act
        interactor.execute(inputData);

        // Assert
        Recipe returnedRecipe = presenter.successData.getSaved_recipes().get(0);
        assertEquals("unique123", returnedRecipe.getRecipeId());
        assertEquals("Unique Recipe Title", returnedRecipe.getTitle());
        assertEquals("testAuthor", returnedRecipe.getAuthorId());
        assertEquals("TestCategory", returnedRecipe.getCategory());
    }
}

