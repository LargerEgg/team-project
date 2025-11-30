package use_case;

import entity.Ingredient;
import entity.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import use_case.recipe_search.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class RecipeSearchInteractorTest {

    private TestRecipeSearchDataAccess dataAccess;
    private TestRecipeSearchPresenter presenter;
    private RecipeSearchInteractor interactor;

    @BeforeEach
    void setUp() {
        dataAccess = new TestRecipeSearchDataAccess();
        presenter = new TestRecipeSearchPresenter();
        interactor = new RecipeSearchInteractor(dataAccess, presenter);
    }

    @Test
    @DisplayName("Search: Success - Recipes found with valid criteria")
    void testSearchSuccess() {
        // Arrange
        interactor.setTestMode(true); // Enable test mode for synchronous execution
        List<Recipe> mockRecipes = createMockRecipes();
        dataAccess.setRecipesToReturn(mockRecipes);

        RecipeSearchInputData inputData = new RecipeSearchInputData("chicken", "Indian");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.successViewCalled);
        assertFalse(presenter.failViewCalled);
        assertNotNull(presenter.outputData);
        assertEquals(2, presenter.outputData.getRecipes().size());
        assertEquals("Chicken Curry", presenter.outputData.getRecipes().get(0).getTitle());
    }

    @Test
    @DisplayName("Search: Success - No recipes found")
    void testSearchSuccessNoResults() {
        // Arrange
        interactor.setTestMode(true);
        dataAccess.setRecipesToReturn(new ArrayList<>()); // Simulate no results

        RecipeSearchInputData inputData = new RecipeSearchInputData("nonexistent", "");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.successViewCalled);
        assertFalse(presenter.failViewCalled);
        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.getRecipes().isEmpty());
    }

    @Test
    @DisplayName("Search: Failure - Data access throws exception")
    void testSearchFailureDataAccessError() {
        // Arrange
        interactor.setTestMode(true);
        dataAccess.setShouldThrowException(true);
        dataAccess.setExceptionMessage("Database connection error");

        RecipeSearchInputData inputData = new RecipeSearchInputData("chicken", "");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertFalse(presenter.successViewCalled);
        assertEquals("Error searching for recipes: Database connection error", presenter.errorMessage);
    }

    @Test
    @DisplayName("Search: Success - Search with filters")
    void testSearchSuccessWithFilters() {
        // Arrange
        interactor.setTestMode(true);
        List<Recipe> mockRecipes = new ArrayList<>();
        List<Ingredient> dummyIngredients = new ArrayList<>();
        dummyIngredients.add(new Ingredient("Lettuce", "1 head"));
        List<String> veganTags = Collections.singletonList("Vegan");
        Date now = new Date();

        mockRecipes.add(new Recipe("id3", "user3", "Vegan Salad", "Healthy vegan salad", dummyIngredients, "Mediterranean", veganTags, Recipe.Status.PUBLISHED, now, now, "url3"));
        dataAccess.setRecipesToReturn(mockRecipes);

        RecipeSearchInputData inputData = new RecipeSearchInputData("salad", "Mediterranean");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.successViewCalled);
        assertFalse(presenter.failViewCalled);
        assertNotNull(presenter.outputData);
        assertEquals(1, presenter.outputData.getRecipes().size());
        assertEquals("Vegan Salad", presenter.outputData.getRecipes().get(0).getTitle());
    }

    @Test
    @DisplayName("Search: Success - Async")
    void testSearchSuccessAsync() throws InterruptedException {
        // Arrange
        interactor.setTestMode(false);
        List<Recipe> mockRecipes = createMockRecipes();
        dataAccess.setRecipesToReturn(mockRecipes);
        RecipeSearchInputData inputData = new RecipeSearchInputData("chicken", "Indian");

        // Act
        interactor.execute(inputData);
        presenter.await(1000); // Wait for async operation

        // Assert
        assertTrue(presenter.successViewCalled);
        assertFalse(presenter.failViewCalled);
        assertNotNull(presenter.outputData);
        assertEquals(2, presenter.outputData.getRecipes().size());
        assertTrue(presenter.progressViewCalled);
    }

    @Test
    @DisplayName("Search: Failure - Async")
    void testSearchFailureAsync() throws InterruptedException {
        // Arrange
        interactor.setTestMode(false);
        dataAccess.setShouldThrowException(true);
        dataAccess.setExceptionMessage("Database connection error");
        RecipeSearchInputData inputData = new RecipeSearchInputData("chicken", "");

        // Act
        interactor.execute(inputData);
        presenter.await(1000); // Wait for async operation

        // Assert
        assertTrue(presenter.failViewCalled);
        assertFalse(presenter.successViewCalled);
        assertEquals("Error searching for recipes: Database connection error", presenter.errorMessage);
    }

    @Test
    @DisplayName("Search: Cancellation")
    void testSearchCancellation() throws InterruptedException {
        // Arrange
        interactor.setTestMode(false);
        List<Recipe> slowRecipes = createMockRecipes();
        List<Recipe> fastRecipes = Collections.singletonList(new Recipe("id3", "user3", "Fast Dish", "", new ArrayList<>(), "Cuisine", new ArrayList<>(), Recipe.Status.PUBLISHED, new Date(), new Date(), ""));

        // Configure first call to be slow
        dataAccess.setRecipesToReturn(slowRecipes);
        dataAccess.setDelay(500);

        // Act
        // Start the slow operation
        interactor.execute(new RecipeSearchInputData("slow", ""));

        // Configure second call to be fast
        presenter.reset();
        dataAccess.setRecipesToReturn(fastRecipes);
        dataAccess.setDelay(0);

        // Start the fast operation, which should cancel the slow one
        interactor.execute(new RecipeSearchInputData("fast", ""));

        presenter.await(1000); // Wait for the fast operation to complete

        // Assert
        assertTrue(presenter.successViewCalled, "Success view should be called for the second, fast operation.");
        assertNotNull(presenter.outputData);
        assertEquals(1, presenter.outputData.getRecipes().size(), "Should have results from the fast operation.");
        assertEquals("Fast Dish", presenter.outputData.getRecipes().get(0).getTitle());
    }

    @Test
    @DisplayName("Search: Interrupted")
    void testSearchInterrupted() throws InterruptedException {
        // Arrange
        interactor.setTestMode(false);
        dataAccess.setShouldThrowInterruptedException(true);
        RecipeSearchInputData inputData = new RecipeSearchInputData("any", "any");

        // Act
        interactor.execute(inputData);
        presenter.await(1000);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertFalse(presenter.successViewCalled);
        assertEquals("Error searching for recipes: The search was interrupted.", presenter.errorMessage);
    }

    @Test
    @DisplayName("Search: Failure - Async with Null Message")
    void testSearchFailureAsyncWithNullMessage() throws InterruptedException {
        // Arrange
        interactor.setTestMode(false);
        dataAccess.setShouldThrowException(true);
        dataAccess.setExceptionMessage(null); // Set a null message
        RecipeSearchInputData inputData = new RecipeSearchInputData("chicken", "");

        // Act
        interactor.execute(inputData);
        presenter.await(1000); // Wait for async operation

        // Assert
        assertTrue(presenter.failViewCalled);
        assertFalse(presenter.successViewCalled);
        assertEquals("Error searching for recipes: null", presenter.errorMessage);
    }

    // Helper for creating mock recipes
    private List<Recipe> createMockRecipes() {
        List<Recipe> mockRecipes = new ArrayList<>();
        List<Ingredient> dummyIngredients = new ArrayList<>();
        dummyIngredients.add(new Ingredient("Chicken", "500g"));
        List<String> dummyTags = new ArrayList<>();
        Date now = new Date();

        mockRecipes.add(new Recipe("id1", "user1", "Chicken Curry", "Spicy chicken dish", dummyIngredients, "Indian", dummyTags, Recipe.Status.PUBLISHED, now, now, "url1"));
        mockRecipes.add(new Recipe("id2", "user2", "Butter Chicken", "Creamy chicken dish", dummyIngredients, "Indian", dummyTags, Recipe.Status.PUBLISHED, now, now, "url2"));
        return mockRecipes;
    }

    // Test double for RecipeSearchRecipeDataAccessInterface
    private static class TestRecipeSearchDataAccess implements RecipeSearchRecipeDataAccessInterface {
        private List<Recipe> recipesToReturn;
        private boolean shouldThrowException = false;
        private boolean shouldThrowInterruptedException = false;
        private String exceptionMessage;
        private long delay = 0;

        public void setRecipesToReturn(List<Recipe> recipesToReturn) {
            this.recipesToReturn = recipesToReturn;
        }

        public void setShouldThrowException(boolean shouldThrowException) {
            this.shouldThrowException = shouldThrowException;
        }

        public void setShouldThrowInterruptedException(boolean shouldThrowInterruptedException) {
            this.shouldThrowInterruptedException = shouldThrowInterruptedException;
        }

        public void setExceptionMessage(String exceptionMessage) {
            this.exceptionMessage = exceptionMessage;
        }

        public void setDelay(long delay) {
            this.delay = delay;
        }

        @Override
        public List<Recipe> search(String name, String category) {
            try {
                if (delay > 0) {
                    Thread.sleep(delay);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Search was interrupted", e);
            }

            if (shouldThrowInterruptedException) {
                throw new RuntimeException(new InterruptedException("Simulated interrupt."));
            }

            if (shouldThrowException) {
                throw new RuntimeException(exceptionMessage);
            }
            return recipesToReturn;
        }

        @Override
        public List<String> getAllCategories() {
            return Arrays.asList("Indian", "Mediterranean", "Mexican");
        }
    }

    // Test double for RecipeSearchOutputBoundary
    private static class TestRecipeSearchPresenter implements RecipeSearchOutputBoundary {
        boolean successViewCalled = false;
        boolean failViewCalled = false;
        boolean progressViewCalled = false;
        RecipeSearchOutputData outputData = null;
        String errorMessage = null;
        private CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void prepareSuccessView(RecipeSearchOutputData outputData) {
            successViewCalled = true;
            this.outputData = outputData;
            latch.countDown();
        }

        @Override
        public void prepareFailView(String errorMessage) {
            failViewCalled = true;
            this.errorMessage = errorMessage;
            latch.countDown();
        }

        @Override
        public void prepareProgressView(RecipeSearchOutputData progressData) {
            progressViewCalled = true;
            // In a real app, you might accumulate progress, but for this test, just recording it is enough.
            if (this.outputData == null) {
                this.outputData = progressData;
            }
        }

        public void await(long timeoutMillis) throws InterruptedException {
            latch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        }

        public void reset() {
            successViewCalled = false;
            failViewCalled = false;
            progressViewCalled = false;
            outputData = null;
            errorMessage = null;
            latch = new CountDownLatch(1);
        }
    }
}
