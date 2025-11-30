package use_case;

import entity.Recipe;
import entity.User;
import use_case.recommend_recipe.*;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Updated Unit Test for RecommendRecipeInteractor.
 * Covers new features: mixing top 3 categories, shuffling, and limiting results to 20.
 */
class RecommendRecipeInteractorTest {

    // ==============================================================================
    // Helper Classes (Stubs & Fakes)
    // ==============================================================================

    static class TestRecipe extends Recipe {
        private final String category;
        private final boolean shouldThrowException;

        public TestRecipe(String id, String category) {
            super(
                    id, "author", "title", "desc", new ArrayList<>(),
                    category, new ArrayList<>(), Recipe.Status.PUBLISHED,
                    new Date(), new Date(), "image/path"
            );
            this.category = category;
            this.shouldThrowException = false;
        }

        public TestRecipe(String category) {
            this("id", category);
        }

        public TestRecipe(boolean shouldThrowException) {
            super(
                    "id", "author", "title", "desc", new ArrayList<>(),
                    null, new ArrayList<>(), Recipe.Status.PUBLISHED,
                    new Date(), new Date(), "image/path"
            );
            this.category = null;
            this.shouldThrowException = shouldThrowException;
        }

        @Override
        public String getCategory() {
            if (shouldThrowException) {
                throw new RuntimeException("DB Error");
            }
            return category;
        }

        @Override
        public String toString() {
            return "Recipe{" + getRecipeId() + ", cat='" + category + "'}";
        }
    }

    static class DataAccessStub implements RecommendRecipeDataAccessInterface {
        private User user;
        private final Map<String, List<Recipe>> categoryDatabase = new HashMap<>();

        public void setUser(User user) {
            this.user = user;
        }

        public void addRecipesToCategory(String category, List<Recipe> recipes) {
            categoryDatabase.put(category, recipes);
        }

        @Override
        public User getUser(String username) {
            return user;
        }

        @Override
        public List<Recipe> getRecipesByCategory(String category) {
            return categoryDatabase.get(category);
        }
    }

    static class TestPresenter implements RecommendRecipeOutputBoundary {
        RecommendRecipeOutputData successData;
        String failMessage;

        @Override
        public void prepareSuccessView(RecommendRecipeOutputData outputData) {
            this.successData = outputData;
        }

        @Override
        public void prepareFailView(String error) {
            this.failMessage = error;
        }
    }

    // ==============================================================================
    // Test Cases
    // ==============================================================================

    @Test
    void testExecute_Success_MixTop3Categories() {
        DataAccessStub dao = new DataAccessStub();
        TestPresenter presenter = new TestPresenter();
        RecommendRecipeInteractor interactor = new RecommendRecipeInteractor(dao, presenter);

        User user = new User("ChefAlice", "pass");
        for(int i=0; i<4; i++) user.saveRecipe(new TestRecipe("Italian"));
        for(int i=0; i<3; i++) user.saveRecipe(new TestRecipe("Chinese"));
        for(int i=0; i<2; i++) user.saveRecipe(new TestRecipe("Mexican"));
        user.saveRecipe(new TestRecipe("French"));

        dao.setUser(user);

        List<Recipe> italianRecs = Arrays.asList(new TestRecipe("ID_I1", "Italian"));
        List<Recipe> chineseRecs = Arrays.asList(new TestRecipe("ID_C1", "Chinese"));
        List<Recipe> mexicanRecs = Arrays.asList(new TestRecipe("ID_M1", "Mexican"));
        List<Recipe> frenchRecs = Arrays.asList(new TestRecipe("ID_F1", "French"));

        dao.addRecipesToCategory("Italian", italianRecs);
        dao.addRecipesToCategory("Chinese", chineseRecs);
        dao.addRecipesToCategory("Mexican", mexicanRecs);
        dao.addRecipesToCategory("French", frenchRecs);

        interactor.execute(new RecommendRecipeInputData("ChefAlice"));

        assertNotNull(presenter.successData);
        List<Recipe> results = presenter.successData.getRecipes();

        assertEquals(3, results.size(), "Should contain 1 Italian + 1 Chinese + 1 Mexican");

        assertTrue(results.contains(italianRecs.get(0)));
        assertTrue(results.contains(chineseRecs.get(0)));
        assertTrue(results.contains(mexicanRecs.get(0)));
        assertFalse(results.contains(frenchRecs.get(0)), "Rank 4 category should be ignored");

        assertEquals("Mix of your Top Favorites", presenter.successData.getCategoryName());
    }

    @Test
    void testExecute_LimitTo20() {
        DataAccessStub dao = new DataAccessStub();
        TestPresenter presenter = new TestPresenter();
        User user = new User("BigEater", "pass");
        user.saveRecipe(new TestRecipe("American"));
        dao.setUser(user);

        List<Recipe> hugeList = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            hugeList.add(new TestRecipe("ID_" + i, "American"));
        }
        dao.addRecipesToCategory("American", hugeList);

        RecommendRecipeInteractor interactor = new RecommendRecipeInteractor(dao, presenter);

        interactor.execute(new RecommendRecipeInputData("BigEater"));

        assertNotNull(presenter.successData);
        assertEquals(20, presenter.successData.getRecipes().size(), "Result should be truncated to 20");
    }

    @Test
    void testExecute_UserNotFound() {
        DataAccessStub dao = new DataAccessStub();
        TestPresenter presenter = new TestPresenter();
        new RecommendRecipeInteractor(dao, presenter).execute(new RecommendRecipeInputData("Ghost"));
        assertEquals("User not found: Ghost", presenter.failMessage);
    }

    @Test
    void testExecute_NoFavorites() {
        DataAccessStub dao = new DataAccessStub();
        dao.setUser(new User("Newbie", "pass"));
        TestPresenter presenter = new TestPresenter();

        new RecommendRecipeInteractor(dao, presenter).execute(new RecommendRecipeInputData("Newbie"));

        assertEquals("No favorites found. Please save some recipes first!", presenter.failMessage);
    }

    @Test
    void testExecute_FavoritesNull() {
        DataAccessStub dao = new DataAccessStub();
        User user = new User("NullFavUser", "pass") {
            @Override
            public List<Recipe> getSavedRecipes() { return null; }
        };
        dao.setUser(user);
        TestPresenter presenter = new TestPresenter();

        new RecommendRecipeInteractor(dao, presenter).execute(new RecommendRecipeInputData("NullFavUser"));

        assertEquals("No favorites found. Please save some recipes first!", presenter.failMessage);
    }

    @Test
    void testExecute_CouldNotDetermineCategory() {
        DataAccessStub dao = new DataAccessStub();
        User user = new User("MessyUser", "pass");

        // [修改] 增加 null category 的测试用例，确保覆盖 if (category != null) 的 False 分支
        user.saveRecipe(new TestRecipe((String)null));

        // 空字符串 category
        user.saveRecipe(new TestRecipe(""));

        dao.setUser(user);
        TestPresenter presenter = new TestPresenter();

        new RecommendRecipeInteractor(dao, presenter).execute(new RecommendRecipeInputData("MessyUser"));

        assertEquals("Could not determine favorite category.", presenter.failMessage);
    }

    @Test
    void testExecute_NoRecommendationsFound() {
        DataAccessStub dao = new DataAccessStub();
        User user = new User("SadGourmet", "pass");
        user.saveRecipe(new TestRecipe("RareFood"));
        dao.setUser(user);

        TestPresenter presenter = new TestPresenter();

        new RecommendRecipeInteractor(dao, presenter).execute(new RecommendRecipeInputData("SadGourmet"));

        assertEquals("Sorry, no recommendations found.", presenter.failMessage);
    }

    @Test
    void testExecute_SkipNullRecipesListFromDao() {
        DataAccessStub dao = new DataAccessStub();
        TestPresenter presenter = new TestPresenter();
        User user = new User("MixedUser", "pass");

        user.saveRecipe(new TestRecipe("Italian"));
        user.saveRecipe(new TestRecipe("Italian"));
        user.saveRecipe(new TestRecipe("French"));
        dao.setUser(user);

        dao.addRecipesToCategory("Italian", Arrays.asList(new TestRecipe("Pizza", "Italian")));
        dao.addRecipesToCategory("French", null);

        new RecommendRecipeInteractor(dao, presenter).execute(new RecommendRecipeInputData("MixedUser"));

        assertNotNull(presenter.successData);
        assertEquals(1, presenter.successData.getRecipes().size());
        assertEquals("Pizza", presenter.successData.getRecipes().get(0).getRecipeId());
    }
}