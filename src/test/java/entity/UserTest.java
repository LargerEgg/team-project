package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for User Entity.
 * Targets 100% Line and Branch Coverage.
 * Uses real Recipe and Review objects.
 * JUnit 5 package-private style.
 */
class UserTest {

    private User user;
    private Recipe recipe;
    private Review review;

    @BeforeEach
    void setUp() {
        // Initialize a clean user object before each test
        user = new User("testUser", "password123");

        // Initialize a real Review object
        review = new Review("Delicious!", "Great recipe, loved it.", 5);

        // Initialize a real Recipe object
        recipe = new Recipe(
                "recipe123",
                "author456",
                "Test Recipe",
                "A test description",
                null, // ingredients
                "Dinner",
                null, // tags
                Recipe.Status.PUBLISHED,
                new Date(),
                new Date(),
                "path/to/image.jpg"
        );
    }

    // --- Constructor Tests ---

    @Test
    void testConstructorSuccess() {
        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertNotNull(user.getSavedRecipes());
        assertNotNull(user.getPublishedRecipes());
        assertNotNull(user.getReviews());
        assertTrue(user.getSavedRecipes().isEmpty());
    }

    @Test
    void testConstructorThrowsExceptionOnEmptyUsername() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("", "password123");
        });
        assertEquals("Username cannot be empty", exception.getMessage());
    }

    @Test
    void testConstructorThrowsExceptionOnEmptyPassword() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("testUser", "");
        });
        assertEquals("Password cannot be empty", exception.getMessage());
    }

    // --- Getter Tests (Basic Types) ---

    @Test
    void testGetUserId() {
        assertNull(user.getUserid());
    }

    @Test
    void testGetPublishedRecipes() {
        List<Recipe> published = user.getPublishedRecipes();
        assertNotNull(published);
        assertTrue(published.isEmpty());

        published.add(recipe);
        assertEquals(1, user.getPublishedRecipes().size());
        assertEquals(recipe, user.getPublishedRecipes().get(0));
    }

    // --- Saved Recipe Logic Tests ---

    @Test
    void testSaveRecipeSuccess() {
        user.saveRecipe(recipe);
        assertEquals(1, user.getSavedRecipes().size());
        assertTrue(user.getSavedRecipes().contains(recipe));
    }

    @Test
    void testSaveRecipeNull() {
        user.saveRecipe(null);
        assertEquals(0, user.getSavedRecipes().size());
    }

    @Test
    void testSaveRecipeDuplicate() {
        user.saveRecipe(recipe);
        user.saveRecipe(recipe);
        assertEquals(1, user.getSavedRecipes().size());
    }

    @Test
    void testRemoveSavedRecipeSuccess() {
        user.saveRecipe(recipe);
        assertEquals(1, user.getSavedRecipes().size());

        user.removeSavedRecipe(recipe);
        assertEquals(0, user.getSavedRecipes().size());
    }

    @Test
    void testRemoveSavedRecipeNull() {
        user.saveRecipe(recipe);
        user.removeSavedRecipe(null);
        assertEquals(1, user.getSavedRecipes().size());
    }

    @Test
    void testRemoveSavedRecipeFromEmptyList() {
        user.removeSavedRecipe(recipe);
        assertTrue(user.getSavedRecipes().isEmpty());
    }

    @Test
    void testRemoveSavedRecipeNotPresent() {
        user.saveRecipe(recipe);

        Recipe anotherRecipe = new Recipe(
                "recipe999", "auth9", "Other", "Desc", null, "Lunch", null,
                Recipe.Status.DRAFT, new Date(), new Date(), null
        );

        user.removeSavedRecipe(anotherRecipe);
        assertEquals(1, user.getSavedRecipes().size());
    }

    @Test
    void testGetSavedRecipesIsUnmodifiable() {
        List<Recipe> savedList = user.getSavedRecipes();

        assertThrows(UnsupportedOperationException.class, () -> {
            savedList.add(recipe);
        });
    }

    // --- Review Logic Tests ---

    @Test
    void testSaveReviewSuccess() {
        user.saveReview(review);
        assertEquals(1, user.getReviews().size());
        assertTrue(user.getReviews().contains(review));
    }

    @Test
    void testSaveReviewNull() {
        user.saveReview(null);
        assertEquals(0, user.getReviews().size());
    }

    @Test
    void testSaveReviewDuplicate() {
        user.saveReview(review);
        user.saveReview(review);
        assertEquals(1, user.getReviews().size());
    }

    @Test
    void testRemoveReviewSuccess() {
        user.saveReview(review);
        assertEquals(1, user.getReviews().size());

        user.removeReview(review);
        assertEquals(0, user.getReviews().size());
    }

    @Test
    void testRemoveReviewNull() {
        user.saveReview(review);
        user.removeReview(null);
        assertEquals(1, user.getReviews().size());
    }

    @Test
    void testRemoveReviewFromEmptyList() {
        user.removeReview(review);
        assertTrue(user.getReviews().isEmpty());
    }

    @Test
    void testRemoveReviewNotPresent() {
        user.saveReview(review);
        Review anotherReview = new Review("Bad", "Did not like it", 1);

        user.removeReview(anotherReview);
        assertEquals(1, user.getReviews().size());
    }

    @Test
    void testGetReviewsIsUnmodifiable() {
        List<Review> reviewList = user.getReviews();

        assertThrows(UnsupportedOperationException.class, () -> {
            reviewList.add(review);
        });
    }
}