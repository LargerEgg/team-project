package use_case;

import entity.Recipe;
import entity.Ingredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import use_case.recipe_search.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
        interactor.setTestMode(true); // Enable test mode for synchronous execution
    }

    @Test
    @DisplayName("Search: Success - Recipes found with valid criteria")
    void testSearchSuccess() {
        // Arrange
        List<Recipe> mockRecipes = new ArrayList<>();
        List<Ingredient> dummyIngredients = new ArrayList<>();
        dummyIngredients.add(new Ingredient("Chicken", "500g"));
        List<String> dummyTags = new ArrayList<>();
        Date now = new Date();

        mockRecipes.add(new Recipe("id1", "user1", "Chicken Curry", "Spicy chicken dish", dummyIngredients, "Indian", dummyTags, Recipe.Status.PUBLISHED, now, now, "url1"));
        mockRecipes.add(new Recipe("id2", "user2", "Butter Chicken", "Creamy chicken dish", dummyIngredients, "Indian", dummyTags, Recipe.Status.PUBLISHED, now, now, "url2"));
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

    // Test double for RecipeSearchRecipeDataAccessInterface
    private static class TestRecipeSearchDataAccess implements RecipeSearchRecipeDataAccessInterface {
        private List<Recipe> recipesToReturn;
        private boolean shouldThrowException = false;
        private String exceptionMessage;

        public void setRecipesToReturn(List<Recipe> recipesToReturn) {
            this.recipesToReturn = recipesToReturn;
        }

        public void setShouldThrowException(boolean shouldThrowException) {
            this.shouldThrowException = shouldThrowException;
        }

        public void setExceptionMessage(String exceptionMessage) {
            this.exceptionMessage = exceptionMessage;
        }

        @Override
        public List<Recipe> search(String name, String category, RecipeSearchOutputBoundary presenter) {
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
}
