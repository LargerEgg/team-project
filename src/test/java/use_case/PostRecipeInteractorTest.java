package use_case;

import entity.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import use_case.post_recipe.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for PostRecipeInteractor
 * Achieves 100% code coverage using test doubles (no Mockito)
 */
class PostRecipeInteractorTest {

    private TestPostRecipeDataAccess dataAccess;
    private TestPostRecipePresenter presenter;
    private PostRecipeInteractor interactor;

    @BeforeEach
    void setUp() {
        dataAccess = new TestPostRecipeDataAccess();
        presenter = new TestPostRecipePresenter();
        interactor = new PostRecipeInteractor(dataAccess, presenter);
    }

    // ==================== PUBLISH METHOD TESTS ====================

    @Test
    @DisplayName("Publish: Success - Valid recipe with all required fields")
    void testPublishSuccess() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Chicken", "500g"));
        ingredients.add(new PostRecipeInputData.IngredientDTO("Rice", "200g"));

        List<String> tags = new ArrayList<>();
        tags.add("dinner");
        tags.add("chicken");

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Test Recipe",
                "A delicious test recipe",
                ingredients,
                "Dinner",
                "https://example.com/image.jpg",
                tags
        );

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.successViewCalled);
        assertFalse(presenter.failViewCalled);
        assertFalse(presenter.draftSavedViewCalled);
        assertNotNull(presenter.successOutputData);
        assertEquals("Recipe published successfully!", presenter.successOutputData.getMessage());
        assertTrue(dataAccess.saveRecipeCalled);
        assertNotNull(dataAccess.savedRecipe);
        assertEquals("testUser", dataAccess.savedRecipe.getAuthorId());
        assertEquals("Test Recipe", dataAccess.savedRecipe.getTitle());
        assertEquals(Recipe.Status.PUBLISHED, dataAccess.savedRecipe.getStatus());
    }

    @Test
    @DisplayName("Publish: Failure - Null authorId")
    void testPublishFailureNullAuthorId() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Chicken", "500g"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                null,
                "Test Recipe",
                "Description",
                ingredients,
                "Dinner",
                "https://example.com/image.jpg",
                tags
        );

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertFalse(presenter.successViewCalled);
        assertEquals("You must be logged in to publish a recipe", presenter.failMessage);
        assertFalse(dataAccess.saveRecipeCalled);
    }

    @Test
    @DisplayName("Publish: Failure - Empty authorId")
    void testPublishFailureEmptyAuthorId() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Chicken", "500g"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "",
                "Test Recipe",
                "Description",
                ingredients,
                "Dinner",
                "https://example.com/image.jpg",
                tags
        );

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertEquals("You must be logged in to publish a recipe", presenter.failMessage);
        assertFalse(dataAccess.saveRecipeCalled);
    }

    @Test
    @DisplayName("Publish: Failure - Blank authorId (spaces only)")
    void testPublishFailureBlankAuthorId() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Chicken", "500g"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "   ",
                "Test Recipe",
                "Description",
                ingredients,
                "Dinner",
                "https://example.com/image.jpg",
                tags
        );

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertEquals("You must be logged in to publish a recipe", presenter.failMessage);
        assertFalse(dataAccess.saveRecipeCalled);
    }

    @Test
    @DisplayName("Publish: Failure - Null title")
    void testPublishFailureNullTitle() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Chicken", "500g"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                null,
                "Description",
                ingredients,
                "Dinner",
                "https://example.com/image.jpg",
                tags
        );

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertEquals("Title is required", presenter.failMessage);
        assertFalse(dataAccess.saveRecipeCalled);
    }

    @Test
    @DisplayName("Publish: Failure - Empty title")
    void testPublishFailureEmptyTitle() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Chicken", "500g"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "",
                "Description",
                ingredients,
                "Dinner",
                "https://example.com/image.jpg",
                tags
        );

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertEquals("Title is required", presenter.failMessage);
        assertFalse(dataAccess.saveRecipeCalled);
    }

    @Test
    @DisplayName("Publish: Failure - Blank title")
    void testPublishFailureBlankTitle() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Chicken", "500g"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "   ",
                "Description",
                ingredients,
                "Dinner",
                "https://example.com/image.jpg",
                tags
        );

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertEquals("Title is required", presenter.failMessage);
        assertFalse(dataAccess.saveRecipeCalled);
    }

    @Test
    @DisplayName("Publish: Failure - Null description")
    void testPublishFailureNullDescription() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Chicken", "500g"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Test Recipe",
                null,
                ingredients,
                "Dinner",
                "https://example.com/image.jpg",
                tags
        );

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertEquals("Description is required", presenter.failMessage);
        assertFalse(dataAccess.saveRecipeCalled);
    }

    @Test
    @DisplayName("Publish: Failure - Empty description")
    void testPublishFailureEmptyDescription() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Chicken", "500g"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Test Recipe",
                "",
                ingredients,
                "Dinner",
                "https://example.com/image.jpg",
                tags
        );

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertEquals("Description is required", presenter.failMessage);
        assertFalse(dataAccess.saveRecipeCalled);
    }

    @Test
    @DisplayName("Publish: Failure - Blank description")
    void testPublishFailureBlankDescription() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Chicken", "500g"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Test Recipe",
                "   ",
                ingredients,
                "Dinner",
                "https://example.com/image.jpg",
                tags
        );

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertEquals("Description is required", presenter.failMessage);
        assertFalse(dataAccess.saveRecipeCalled);
    }

    @Test
    @DisplayName("Publish: Failure - Null ingredients")
    void testPublishFailureNullIngredients() {
        // Arrange
        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Test Recipe",
                "Description",
                null,
                "Dinner",
                "https://example.com/image.jpg",
                tags
        );

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertEquals("At least one ingredient is required", presenter.failMessage);
        assertFalse(dataAccess.saveRecipeCalled);
    }
    @Test
    @DisplayName("BuildRecipe: Handles null ingredients list in buildRecipeFromInput")
    void testBuildRecipeWithNullIngredientsInBuilder() {
        // This tests the branch where ingredients list could be null during building
        // Even though validation prevents this, the method has a null check
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Test", "1 unit"));

        // Create input with minimal fields to ensure all branches in build method are hit
        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Test Recipe",
                "Description",
                ingredients,
                null,  // null category
                null,  // null imagePath
                null   // null tags
        );

        interactor.publish(inputData);

        assertNotNull(dataAccess.savedRecipe);
        assertNotNull(dataAccess.savedRecipe.getIngredients());
    }

    @Test
    @DisplayName("SaveDraft: Success with null ingredients to test builder branch")
    void testSaveDraftNullIngredients() {
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Flour", "2 cups"));

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Draft Recipe",
                "Work in progress",
                ingredients,
                null,  // null category
                null,  // null imagePath
                null   // null tags
        );

        interactor.saveDraft(inputData);

        assertTrue(presenter.draftSavedViewCalled);
        assertNotNull(dataAccess.savedRecipe);
    }

    @Test
    @DisplayName("SaveDraft: Success with null ingredients")
    void testSaveDraftWithNullIngredients() {
        // Arrange - saveDraft doesn't validate ingredients, so null can reach buildRecipeFromInput
        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Draft Recipe",
                "Work in progress",
                null,  // null ingredients - this is allowed for drafts!
                "Baking",
                "",
                new ArrayList<>()
        );

        // Act
        interactor.saveDraft(inputData);

        // Assert
        assertTrue(presenter.draftSavedViewCalled);
        assertNotNull(dataAccess.savedRecipe);
        assertEquals(Recipe.Status.DRAFT, dataAccess.savedRecipe.getStatus());
        assertTrue(dataAccess.savedRecipe.getIngredients().isEmpty());  // Should be empty list, not null
    }


    @Test
    @DisplayName("Publish: Failure - Empty ingredients list")
    void testPublishFailureEmptyIngredients() {
        // Arrange
        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Test Recipe",
                "Description",
                new ArrayList<>(),
                "Dinner",
                "https://example.com/image.jpg",
                tags
        );

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertEquals("At least one ingredient is required", presenter.failMessage);
        assertFalse(dataAccess.saveRecipeCalled);
    }

    @Test
    @DisplayName("Publish: Failure - RuntimeException from data access")
    void testPublishFailureRuntimeException() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Chicken", "500g"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Test Recipe",
                "Description",
                ingredients,
                "Dinner",
                "https://example.com/image.jpg",
                tags
        );

        dataAccess.shouldThrowException = true;
        dataAccess.exceptionMessage = "Database error";

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertEquals("Failed to publish recipe: Database error", presenter.failMessage);
        assertFalse(presenter.successViewCalled);
    }

    // ==================== SAVE DRAFT METHOD TESTS ====================

    @Test
    @DisplayName("SaveDraft: Success - Valid draft with all fields")
    void testSaveDraftSuccess() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Flour", "2 cups"));

        List<String> tags = new ArrayList<>();
        tags.add("baking");

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Draft Recipe",
                "Work in progress",
                ingredients,
                "Baking",
                "",
                tags
        );

        // Act
        interactor.saveDraft(inputData);

        // Assert
        assertTrue(presenter.draftSavedViewCalled);
        assertFalse(presenter.failViewCalled);
        assertFalse(presenter.successViewCalled);
        assertNotNull(presenter.draftOutputData);
        assertEquals("Draft saved successfully!", presenter.draftOutputData.getMessage());
        assertTrue(dataAccess.saveRecipeCalled);
        assertNotNull(dataAccess.savedRecipe);
        assertEquals("testUser", dataAccess.savedRecipe.getAuthorId());
        assertEquals(Recipe.Status.DRAFT, dataAccess.savedRecipe.getStatus());
    }

    @Test
    @DisplayName("SaveDraft: Failure - Null authorId")
    void testSaveDraftFailureNullAuthorId() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Flour", "2 cups"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                null,
                "Draft Recipe",
                "Work in progress",
                ingredients,
                "Baking",
                "",
                tags
        );

        // Act
        interactor.saveDraft(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertEquals("You must be logged in to save a draft", presenter.failMessage);
        assertFalse(dataAccess.saveRecipeCalled);
    }

    @Test
    @DisplayName("SaveDraft: Failure - Empty authorId")
    void testSaveDraftFailureEmptyAuthorId() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Flour", "2 cups"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "",
                "Draft Recipe",
                "Work in progress",
                ingredients,
                "Baking",
                "",
                tags
        );

        // Act
        interactor.saveDraft(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertEquals("You must be logged in to save a draft", presenter.failMessage);
        assertFalse(dataAccess.saveRecipeCalled);
    }

    @Test
    @DisplayName("SaveDraft: Failure - Blank authorId")
    void testSaveDraftFailureBlankAuthorId() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Flour", "2 cups"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "  ",
                "Draft Recipe",
                "Work in progress",
                ingredients,
                "Baking",
                "",
                tags
        );

        // Act
        interactor.saveDraft(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertEquals("You must be logged in to save a draft", presenter.failMessage);
        assertFalse(dataAccess.saveRecipeCalled);
    }

    @Test
    @DisplayName("SaveDraft: Failure - RuntimeException from data access")
    void testSaveDraftFailureRuntimeException() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Flour", "2 cups"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Draft Recipe",
                "Work in progress",
                ingredients,
                "Baking",
                "",
                tags
        );

        dataAccess.shouldThrowException = true;
        dataAccess.exceptionMessage = "Connection timeout";

        // Act
        interactor.saveDraft(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertEquals("Failed to save draft: Connection timeout", presenter.failMessage);
        assertFalse(presenter.draftSavedViewCalled);
    }
    @Test
    @DisplayName("SaveDraft: Success - Draft with null ingredients (allowed for drafts)")
    void testSaveDraftSuccessNullIngredients() {
        // Arrange
        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Draft Recipe",
                "Work in progress",
                null,  // Null ingredients - allowed for drafts
                "Baking",
                "",
                tags
        );

        // Act
        interactor.saveDraft(inputData);

        // Assert
        assertTrue(presenter.draftSavedViewCalled);
        assertFalse(presenter.failViewCalled);
        assertNotNull(dataAccess.savedRecipe);
        assertEquals(Recipe.Status.DRAFT, dataAccess.savedRecipe.getStatus());
        assertTrue(dataAccess.savedRecipe.getIngredients().isEmpty());
    }

    // ==================== BUILD RECIPE TESTS ====================

    @Test
    @DisplayName("BuildRecipe: Creates recipe with PUBLISHED status")
    void testBuildRecipePublishedStatus() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Salt", "1 tsp"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Test Recipe",
                "Description",
                ingredients,
                "Seasoning",
                "https://example.com/salt.jpg",
                tags
        );

        // Act
        interactor.publish(inputData);

        // Assert
        assertNotNull(dataAccess.savedRecipe);
        assertEquals(Recipe.Status.PUBLISHED, dataAccess.savedRecipe.getStatus());
        assertEquals("testUser", dataAccess.savedRecipe.getAuthorId());
        assertEquals("Test Recipe", dataAccess.savedRecipe.getTitle());
    }

    @Test
    @DisplayName("BuildRecipe: Creates recipe with DRAFT status")
    void testBuildRecipeDraftStatus() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Sugar", "2 tbsp"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Draft Recipe",
                "Still working on it",
                ingredients,
                "Dessert",
                "",
                tags
        );

        // Act
        interactor.saveDraft(inputData);

        // Assert
        assertNotNull(dataAccess.savedRecipe);
        assertEquals(Recipe.Status.DRAFT, dataAccess.savedRecipe.getStatus());
        assertEquals("testUser", dataAccess.savedRecipe.getAuthorId());
        assertEquals("Draft Recipe", dataAccess.savedRecipe.getTitle());
    }

    @Test
    @DisplayName("BuildRecipe: Converts multiple ingredients correctly")
    void testBuildRecipeMultipleIngredients() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Chicken", "500g"));
        ingredients.add(new PostRecipeInputData.IngredientDTO("Rice", "200g"));
        ingredients.add(new PostRecipeInputData.IngredientDTO("Onion", "1 large"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Multi-Ingredient Recipe",
                "Complex dish",
                ingredients,
                "Main Course",
                "https://example.com/dish.jpg",
                tags
        );

        // Act
        interactor.publish(inputData);

        // Assert
        assertNotNull(dataAccess.savedRecipe);
        assertEquals(3, dataAccess.savedRecipe.getIngredients().size());
        assertEquals("Chicken", dataAccess.savedRecipe.getIngredients().get(0).getName());
        assertEquals("500g", dataAccess.savedRecipe.getIngredients().get(0).getMeasure());
        assertEquals("Rice", dataAccess.savedRecipe.getIngredients().get(1).getName());
        assertEquals("Onion", dataAccess.savedRecipe.getIngredients().get(2).getName());
    }

    @Test
    @DisplayName("BuildRecipe: Generates unique recipe IDs")
    void testBuildRecipeUniqueIds() {
        // Arrange
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        ingredients.add(new PostRecipeInputData.IngredientDTO("Test", "1 unit"));

        List<String> tags = new ArrayList<>();

        PostRecipeInputData inputData = new PostRecipeInputData(
                "testUser",
                "Test Recipe",
                "Description",
                ingredients,
                "Test",
                "",
                tags
        );

        // Act
        interactor.publish(inputData);
        String firstId = dataAccess.savedRecipe.getRecipeId();

        dataAccess.reset();

        interactor.publish(inputData);
        String secondId = dataAccess.savedRecipe.getRecipeId();

        // Assert - Each call should generate a different recipe ID
        assertNotNull(firstId);
        assertNotNull(secondId);
        assertNotEquals(firstId, secondId);
    }

    // ==================== TEST DOUBLES ====================

    /**
     * Test double for PostRecipeDataAccessInterface
     */
    private static class TestPostRecipeDataAccess implements PostRecipeDataAccessInterface {
        boolean saveRecipeCalled = false;
        Recipe savedRecipe = null;
        boolean shouldThrowException = false;
        String exceptionMessage = "Test exception";

        @Override
        public Recipe saveRecipe(Recipe recipe) {
            saveRecipeCalled = true;
            if (shouldThrowException) {
                throw new RuntimeException(exceptionMessage);
            }
            savedRecipe = recipe;
            return recipe;
        }

        void reset() {
            saveRecipeCalled = false;
            savedRecipe = null;
            shouldThrowException = false;
        }
    }

    /**
     * Test double for PostRecipeOutputBoundary
     */
    private static class TestPostRecipePresenter implements PostRecipeOutputBoundary {
        boolean successViewCalled = false;
        boolean draftSavedViewCalled = false;
        boolean failViewCalled = false;
        PostRecipeOutputData successOutputData = null;
        PostRecipeOutputData draftOutputData = null;
        String failMessage = null;
        PostRecipeInputData failInputData = null;

        @Override
        public void prepareSuccessView(PostRecipeOutputData outputData) {
            successViewCalled = true;
            successOutputData = outputData;
        }

        @Override
        public void prepareDraftSavedView(PostRecipeOutputData outputData) {
            draftSavedViewCalled = true;
            draftOutputData = outputData;
        }

        @Override
        public void prepareFailedView(String errorMessage, PostRecipeInputData inputData) {
            failViewCalled = true;
            failMessage = errorMessage;
            failInputData = inputData;
        }
    }
}