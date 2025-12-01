package use_case;

import entity.Recipe;
import use_case.recommend_recipe.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date; // Added import for Date
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for RecommendRecipeInteractor.
 * This test suite aims for 100% code coverage, including edge cases and exception handling.
 */
class RecommendRecipeInteractorTest {

    // ==============================================================================
    // Helper Classes (Stubs & Fakes)
    // These internal classes allow testing without external mocking frameworks like Mockito.
    // ==============================================================================

    /**
     * A concrete implementation of Recipe for testing purposes.
     */
    static class TestRecipe extends Recipe {
        private final String category;
        // Flag to simulate an exception when getCategory is called
        private final boolean shouldThrowException;

        public TestRecipe(String category) {
            // Fix: Updated super() to match the 11-argument constructor in Recipe.java
            super(
                    "id",                   // recipeId
                    "author",               // authorId
                    "title",                // title
                    "desc",                 // description
                    new ArrayList<>(),      // ingredients
                    category,               // category
                    new ArrayList<>(),      // tags
                    Recipe.Status.PUBLISHED,// status (Enum)
                    new Date(),             // creationDate
                    new Date(),             // updateDate
                    "image/path"            // imagePath
            );
            this.category = category;
            this.shouldThrowException = false;
        }

        public TestRecipe(boolean shouldThrowException) {
            // Fix: Updated super() here as well
            super(
                    "id",
                    "author",
                    "title",
                    "desc",
                    new ArrayList<>(),
                    null,
                    new ArrayList<>(),
                    Recipe.Status.PUBLISHED,
                    new Date(),
                    new Date(),
                    "image/path"
            );
            this.category = null;
            this.shouldThrowException = shouldThrowException;
        }

        @Override
        public String getCategory() {
            if (shouldThrowException) {
                throw new RuntimeException("Database error simulation");
            }
            return category;
        }
    }

    /**
     * A simple Stub for the Data Access Interface.
     * We can override methods dynamically in tests using anonymous classes,
     * but this provides a base structure.
     */
    static class DataAccessStub implements RecommendRecipeDataAccessInterface {
        @Override
        public User getUser(String username) {
            return null; // Default behavior
        }

        @Override
        public List<Recipe> getRecipesByCategory(String category) {
            return new ArrayList<>(); // Default behavior
        }
    }

    /**
     * A capturing fake for the Output Boundary (Presenter).
     * It allows us to verify what data was passed to the success or fail views.
     */
    static class TestPresenter implements RecommendRecipeOutputBoundary {
        String successCategory;
        List<Recipe> successRecipes;
        String failMessage;

        @Override
        public void prepareSuccessView(RecommendRecipeOutputData outputData) {
            // Updated to match your RecommendRecipeOutputData.java file: getCategoryName()
            this.successCategory = outputData.getCategoryName();
            this.successRecipes = outputData.getRecipes();
        }

        @Override
        public void prepareFailView(String error) {
            this.failMessage = error;
        }
    }

    // ==============================================================================
    // Test Cases
    // ==============================================================================

    /**
     * Test the "Happy Path":
     * - User exists.
     * - User has saved recipes.
     * - A clear favorite category exists.
     * - Recommendations exist for that category.
     */
    @Test
    void testExecute_Success() {
        // Arrange
        String username = "ChefAlice";
        TestPresenter presenter = new TestPresenter();

        RecommendRecipeDataAccessInterface dao = new DataAccessStub() {
            @Override
            public User getUser(String name) {
                User user = new User(name, "pass");
                // User likes Italian food mostly
                // Updated method call to match User.java: saveRecipe()
                user.saveRecipe(new TestRecipe("Italian"));
                user.saveRecipe(new TestRecipe("Italian"));
                user.saveRecipe(new TestRecipe("French"));
                return user;
            }

            @Override
            public List<Recipe> getRecipesByCategory(String category) {
                if ("Italian".equals(category)) {
                    List<Recipe> recs = new ArrayList<>();
                    recs.add(new TestRecipe("Italian Pizza"));
                    return recs;
                }
                return new ArrayList<>();
            }
        };

        RecommendRecipeInteractor interactor = new RecommendRecipeInteractor(dao, presenter);

        // Act
        interactor.execute(new RecommendRecipeInputData(username));

        // Assert
        assertEquals("Italian", presenter.successCategory, "The algorithm should identify Italian as the favorite.");
        assertNotNull(presenter.successRecipes);
        assertEquals(1, presenter.successRecipes.size());
        assertNull(presenter.failMessage, "Fail view should not be triggered.");
    }

    /**
     * Edge Case: User Not Found.
     * Verifies the fail view logic when DAO returns null for the user.
     */
    @Test
    void testExecute_UserNotFound() {
        // Arrange
        TestPresenter presenter = new TestPresenter();
        RecommendRecipeDataAccessInterface dao = new DataAccessStub() {
            @Override
            public User getUser(String name) {
                return null; // Simulate user not found
            }
        };
        RecommendRecipeInteractor interactor = new RecommendRecipeInteractor(dao, presenter);

        // Act
        interactor.execute(new RecommendRecipeInputData("GhostUser"));

        // Assert
        assertEquals("User not found: GhostUser", presenter.failMessage);
        assertNull(presenter.successCategory);
    }

    /**
     * Edge Case: No Favorites (Empty List).
     * Verifies logic when user exists but saved recipes list is empty.
     */
    @Test
    void testExecute_NoFavorites_EmptyList() {
        // Arrange
        TestPresenter presenter = new TestPresenter();
        RecommendRecipeDataAccessInterface dao = new DataAccessStub() {
            @Override
            public User getUser(String name) {
                // User with empty saved recipes
                return new User(name, "pass");
            }
        };
        RecommendRecipeInteractor interactor = new RecommendRecipeInteractor(dao, presenter);

        // Act
        interactor.execute(new RecommendRecipeInputData("NewUser"));

        // Assert
        assertEquals("No favorites found. Please save some recipes first!", presenter.failMessage);
    }

    /**
     * Edge Case: No Favorites (Null List).
     * Verifies robustness when User object returns null instead of an empty list.
     * This covers the "favorites == null" check in the Interactor.
     */
    @Test
    void testExecute_NoFavorites_NullList() {
        // Arrange
        TestPresenter presenter = new TestPresenter();
        RecommendRecipeDataAccessInterface dao = new DataAccessStub() {
            @Override
            public User getUser(String name) {
                // Create a generic user, assuming getSavedRecipes() might default to null
                // depending on implementation, or we simulate it here:
                return new User(name, "pass") {
                    @Override
                    public List<Recipe> getSavedRecipes() {
                        return null; // Force null return
                    }
                };
            }
        };
        RecommendRecipeInteractor interactor = new RecommendRecipeInteractor(dao, presenter);

        // Act
        interactor.execute(new RecommendRecipeInputData("BuggyUser"));

        // Assert
        assertEquals("No favorites found. Please save some recipes first!", presenter.failMessage);
    }

    /**
     * Complex Edge Case: Dirty Data Handling.
     * This tests the loop inside 'getFavouriteCategoriesRanked'.
     * We inject:
     * 1. A null recipe.
     * 2. A recipe with a null category.
     * 3. A recipe with an empty/whitespace category.
     * 4. A recipe that throws an exception when accessed.
     * * The system should ignore these and fail gracefully if no valid category remains.
     */
    @Test
    void testExecute_DirtyData_And_Exceptions() {
        // Arrange
        TestPresenter presenter = new TestPresenter();
        RecommendRecipeDataAccessInterface dao = new DataAccessStub() {
            @Override
            public User getUser(String name) {
                User user = new User(name, "pass");
                // Updated to use saveRecipe
                user.saveRecipe(null); // Null recipe
                user.saveRecipe(new TestRecipe((String) null)); // Null category
                user.saveRecipe(new TestRecipe("   ")); // Whitespace category
                user.saveRecipe(new TestRecipe(true)); // Throws Exception
                return user;
            }
        };
        RecommendRecipeInteractor interactor = new RecommendRecipeInteractor(dao, presenter);

        // Act
        interactor.execute(new RecommendRecipeInputData("MessyUser"));

        // Assert
        // Since all inputs were invalid, rankedCategories should be empty.
        assertEquals("Could not determine favorite category.", presenter.failMessage);
    }

    /**
     * Edge Case: Whitespace Trimming.
     * Verifies that " Italian " is treated as "Italian".
     */
    @Test
    void testExecute_WhitespaceTrimming() {
        // Arrange
        TestPresenter presenter = new TestPresenter();
        RecommendRecipeDataAccessInterface dao = new DataAccessStub() {
            @Override
            public User getUser(String name) {
                User user = new User(name, "pass");
                // Updated to use saveRecipe
                user.saveRecipe(new TestRecipe(" Italian ")); // Should be trimmed
                return user;
            }
            @Override
            public List<Recipe> getRecipesByCategory(String category) {
                if ("Italian".equals(category)) {
                    List<Recipe> list = new ArrayList<>();
                    list.add(new TestRecipe("Pasta"));
                    return list;
                }
                return new ArrayList<>();
            }
        };
        RecommendRecipeInteractor interactor = new RecommendRecipeInteractor(dao, presenter);

        // Act
        interactor.execute(new RecommendRecipeInputData("TrimUser"));

        // Assert
        assertEquals("Italian", presenter.successCategory, "Category should be trimmed before processing.");
        assertNull(presenter.failMessage);
    }

    /**
     * Edge Case: No Recommendations Found.
     * The user has a favorite category, but the API/DB returns no recipes for it.
     */
    @Test
    void testExecute_NoRecommendationsAvailable() {
        // Arrange
        TestPresenter presenter = new TestPresenter();
        RecommendRecipeDataAccessInterface dao = new DataAccessStub() {
            @Override
            public User getUser(String name) {
                User user = new User(name, "pass");
                // Updated to use saveRecipe
                user.saveRecipe(new TestRecipe("RareCuisine"));
                return user;
            }

            @Override
            public List<Recipe> getRecipesByCategory(String category) {
                return new ArrayList<>(); // Empty result from API
            }
        };
        RecommendRecipeInteractor interactor = new RecommendRecipeInteractor(dao, presenter);

        // Act
        interactor.execute(new RecommendRecipeInputData("GourmetUser"));

        // Assert
        assertEquals("Sorry, no recommendations found for category: RareCuisine", presenter.failMessage);
    }

    /**
     * Edge Case: Tie Breaker.
     * When two categories have the exact same count, the system should simply pick one (usually the first one encountered/sorted).
     * This ensures the system doesn't crash or return null on a tie.
     */
    @Test
    void testExecute_TieBreaker() {
        // Arrange
        TestPresenter presenter = new TestPresenter();
        RecommendRecipeDataAccessInterface dao = new DataAccessStub() {
            @Override
            public User getUser(String name) {
                User user = new User(name, "pass");
                // Updated to use saveRecipe
                user.saveRecipe(new TestRecipe("Mexican"));
                user.saveRecipe(new TestRecipe("Chinese"));
                // Count is 1 vs 1.
                return user;
            }

            @Override
            public List<Recipe> getRecipesByCategory(String category) {
                // Return a dummy recipe regardless of which category wins
                List<Recipe> recs = new ArrayList<>();
                recs.add(new TestRecipe("Tasty Dish"));
                return recs;
            }
        };
        RecommendRecipeInteractor interactor = new RecommendRecipeInteractor(dao, presenter);

        // Act
        interactor.execute(new RecommendRecipeInputData("IndecisiveUser"));

        // Assert
        // We just want to ensure it succeeded and picked ONE of them.
        assertNotNull(presenter.successCategory);
        assertTrue(presenter.successCategory.equals("Mexican") || presenter.successCategory.equals("Chinese"));
        assertNull(presenter.failMessage);
    }
}