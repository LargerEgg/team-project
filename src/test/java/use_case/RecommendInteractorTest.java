package use_case;

import entity.Recipe;
import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.recommend_recipe.RecommendRecipeDataAccessInterface;
import use_case.recommend_recipe.RecommendRecipeInteractor;
import use_case.recommend_recipe.RecommendRecipeOutputData;
import use_case.recommend_recipe.RecommendRecipeInputData;
import use_case.recommend_recipe.RecommendRecipeOutputBoundary;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RecommendRecipeInteractorTest {

    private TestDataAccess dataAccessObject;
    private TestPresenter presenter;
    private RecommendRecipeInteractor interactor;

    @BeforeEach
    void setUp() {
        // Use our own defined "fake" implementation instead of Mockito mock
        dataAccessObject = new TestDataAccess();
        presenter = new TestPresenter();
        interactor = new RecommendRecipeInteractor((RecommendRecipeDataAccessInterface) dataAccessObject, presenter);
    }

    /**
     * Helper method: Create a real Recipe object.
     */
    private Recipe createRecipe(String id, String title, String category) {
        return new Recipe(
                id,
                "Author1",
                title,
                "Description",
                new ArrayList<>(), // ingredients
                category,
                new ArrayList<>(), // tags
                Recipe.Status.PUBLISHED,
                new Date(),
                new Date(),
                "" // imagePath
        );
    }

    @Test
    void execute_NoFavorites_ShouldPrepareFailView() {
        // Arrange
        String username = "UserEmpty";
        RecommendRecipeInputData inputData = new RecommendRecipeInputData(username);

        // Act
        interactor.execute(inputData);

        // Assert
        assertEquals("No favorites found. Please save some recipes first!", presenter.getFailMessage());
        assertNull(presenter.getOutputData());
    }

    /**
     * New test: Covers the branch where favorites == null
     */
    @Test
    void execute_FavoritesIsNull_ShouldPrepareFailView() {
        // Arrange
        String username = "UserNullFav";
        // Force DAO to return null
        dataAccessObject.setReturnNullForSavedRecipes(true);
        RecommendRecipeInputData inputData = new RecommendRecipeInputData(username);

        // Act
        interactor.execute(inputData);

        // Assert
        // Should be treated the same as an empty list, entering Fail View
        assertEquals("No favorites found. Please save some recipes first!", presenter.getFailMessage());
    }

    @Test
    void execute_FavoritesExistButNoCategories_ShouldPrepareFailView() {
        // Arrange
        String username = "UserNoCat";
        dataAccessObject.saveRecipeForUser(username, createRecipe("r1", "Food1", null));
        dataAccessObject.saveRecipeForUser(username, createRecipe("r2", "Food2", ""));
        dataAccessObject.saveRecipeForUser(username, createRecipe("r3", "Food3", "   "));

        RecommendRecipeInputData inputData = new RecommendRecipeInputData(username);

        // Act
        interactor.execute(inputData);

        // Assert
        assertEquals("Could not determine favorite category.", presenter.getFailMessage());
    }

    @Test
    void execute_CategoriesExistButNoRecommendationsFound_ShouldPrepareFailView() {
        // Arrange
        String username = "UserNoRec";
        dataAccessObject.saveRecipeForUser(username, createRecipe("r1", "Pizza", "Italian"));

        // Database returns empty list by default, not null

        RecommendRecipeInputData inputData = new RecommendRecipeInputData(username);

        // Act
        interactor.execute(inputData);

        // Assert
        assertEquals("Sorry, no recommendations found.", presenter.getFailMessage());
    }

    @Test
    void execute_Success_WithRankingLogic() {
        // Arrange
        String username = "UserRank";

        dataAccessObject.saveRecipeForUser(username, createRecipe("f1", "Sushi", "Asian"));
        dataAccessObject.saveRecipeForUser(username, createRecipe("f2", "Ramen", "Asian"));
        dataAccessObject.saveRecipeForUser(username, createRecipe("f3", "Dumpling", "Asian"));
        dataAccessObject.saveRecipeForUser(username, createRecipe("f4", "Burger", "Western"));

        // Construct recommendation pool in database:
        // Even if Western is saved, if Asian count is higher, algorithm should prioritize or include Asian
        dataAccessObject.addRecipeToLibrary(createRecipe("rec1", "Kung Pao Chicken", "Asian"));
        dataAccessObject.addRecipeToLibrary(createRecipe("rec2", "Steak", "Western"));

        RecommendRecipeInputData inputData = new RecommendRecipeInputData(username);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNull(presenter.getFailMessage());
        assertNotNull(presenter.getOutputData());

        List<Recipe> recommendations = presenter.getOutputData().getRecipes();
        assertEquals(2, recommendations.size());

        // Simple verification of content (since interactor might shuffle, we only verify these dishes are included)
        boolean hasAsian = recommendations.stream().anyMatch(r -> r.getCategory().equals("Asian"));
        boolean hasWestern = recommendations.stream().anyMatch(r -> r.getCategory().equals("Western"));
        assertTrue(hasAsian);
        assertTrue(hasWestern);
    }

    /**
     * New test: Covers branch where getRecipesByCategory returns null.
     * Corresponds to if (recipes != null) in code.
     */
    @Test
    void execute_OneCategoryReturnsNull_ShouldSkipAndContinue() {
        // Arrange
        String username = "UserMixedNull";

        // User likes Asian and Western
        dataAccessObject.saveRecipeForUser(username, createRecipe("f1", "Sushi", "Asian"));
        dataAccessObject.saveRecipeForUser(username, createRecipe("f2", "Burger", "Western"));

        // Database has Western recommendations, but we force it to return null when fetching Asian (simulating DB error or no data)
        dataAccessObject.addRecipeToLibrary(createRecipe("rec1", "Steak", "Western"));
        dataAccessObject.setCategoryToReturnNull("Asian");

        RecommendRecipeInputData inputData = new RecommendRecipeInputData(username);

        // Act
        interactor.execute(inputData);

        // Assert
        // Should skip Asian (null), but keep Western results, so it is Success View
        assertNotNull(presenter.getOutputData());
        List<Recipe> recommendations = presenter.getOutputData().getRecipes();

        // Should have only 1 recommendation (Western), Asian was skipped
        assertEquals(1, recommendations.size());
        assertEquals("Western", recommendations.get(0).getCategory());
    }

    @Test
    void execute_Success_TruncatesTo20() {
        // Arrange
        String username = "UserTruncate";
        // User likes Dessert
        dataAccessObject.saveRecipeForUser(username, createRecipe("fav1", "Cake", "Dessert"));

        // Database has 25 Dessert recipes
        for (int i = 0; i < 25; i++) {
            dataAccessObject.addRecipeToLibrary(createRecipe("rec" + i, "Cookie " + i, "Dessert"));
        }

        RecommendRecipeInputData inputData = new RecommendRecipeInputData(username);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull(presenter.getOutputData());
        assertEquals(20, presenter.getOutputData().getRecipes().size());
    }


    // ==============================================================================
    // TestDataAccess (Updated to support forcing NULL returns)
    // ==============================================================================

    /**
     * Simulated DataAccessObject.
     * Uses HashMap and List to store data in memory.
     */
    private static class TestDataAccess implements RecommendRecipeDataAccessInterface {
        // Simulated user favorites table: Username -> List<Recipe>
        private final Map<String, List<Recipe>> userFavorites = new HashMap<>();
        // Simulated large recipe library: Category -> List<Recipe>
        private final Map<String, List<Recipe>> allRecipesByCategory = new HashMap<>();

        // Added: Control Flag, used for testing null branches
        private boolean returnNullForSavedRecipes = false;
        private final Set<String> categoriesReturningNull = new HashSet<>();

        public void setReturnNullForSavedRecipes(boolean value) {
            this.returnNullForSavedRecipes = value;
        }

        public void setCategoryToReturnNull(String category) {
            this.categoriesReturningNull.add(category);
        }

        // --- Helper methods: Used to prepare data before tests ---

        public void saveRecipeForUser(String username, Recipe recipe) {
            userFavorites.computeIfAbsent(username, k -> new ArrayList<>()).add(recipe);
        }

        public void addRecipeToLibrary(Recipe recipe) {
            String category = recipe.getCategory();
            if (category != null) {
                allRecipesByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(recipe);
            }
        }

        @Override
        public User getUser(String username) {
            return null;
        }

        // --- Interface implementation ---

        @Override
        public List<Recipe> getSavedRecipes(String username) {
            // If test requires returning null, return null
            if (returnNullForSavedRecipes) {
                return null;
            }
            return userFavorites.getOrDefault(username, new ArrayList<>());
        }

        @Override
        public List<Recipe> getRecipesByCategory(String category) {
            // If test requires this category to return null, return null
            if (categoriesReturningNull.contains(category)) {
                return null;
            }
            return allRecipesByCategory.getOrDefault(category, new ArrayList<>());
        }
    }

    // ==============================================================================
    // TestPresenter
    // ==============================================================================

    /**
     * Simulated Presenter.
     * It won't update UI, but stores results in variables for test inspection.
     */
    private static class TestPresenter implements RecommendRecipeOutputBoundary {
        private String failMessage = null;
        private RecommendRecipeOutputData outputData = null;

        @Override
        public void prepareFailView(String error) {
            this.failMessage = error;
        }

        @Override
        public void prepareSuccessView(RecommendRecipeOutputData outputData) {
            this.outputData = outputData;
        }

        public String getFailMessage() {
            return failMessage;
        }

        public RecommendRecipeOutputData getOutputData() {
            return outputData;
        }
    }
}