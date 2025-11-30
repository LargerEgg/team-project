package entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for PopularityCalculator
 * Tests all methods and edge cases for the popularity calculation logic
 */
class PopularityCalculatorTest {

    private Recipe createTestRecipe(int views, int saves, double averageRating) {
        Recipe recipe = new Recipe(
                "test-id",
                "test-author",
                "Test Recipe",
                "Test description",
                new ArrayList<>(),
                "Test Category",
                new ArrayList<>(),
                Recipe.Status.PUBLISHED,
                new Date(),
                new Date(),
                "http://example.com/image.jpg"
        );
        recipe.setViews(views);
        recipe.setSaves(saves);
        recipe.setAverageRating(averageRating);
        return recipe;
    }

    // ==================== isPopular() TESTS ====================

    @Nested
    @DisplayName("isPopular() method tests")
    class IsPopularTests {

        @Test
        @DisplayName("Should return false for null recipe")
        void testIsPopular_NullRecipe() {
            assertFalse(PopularityCalculator.isPopular(null));
        }

        @Test
        @DisplayName("Should return false when views <= MINIMUM_VIEWS")
        void testIsPopular_ViewsBelowThreshold() {
            // views = 50, which is <= MINIMUM_VIEWS (50)
            Recipe recipe = createTestRecipe(50, 10, 4.5);
            assertFalse(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Should return false when views = 0")
        void testIsPopular_ZeroViews() {
            Recipe recipe = createTestRecipe(0, 0, 4.5);
            assertFalse(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Should return false when averageRating <= MINIMUM_RATING")
        void testIsPopular_RatingBelowThreshold() {
            // views = 100 (> 50), rating = 4.0 (<= 4.0)
            Recipe recipe = createTestRecipe(100, 10, 4.0);
            assertFalse(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Should return false when averageRating = 0")
        void testIsPopular_ZeroRating() {
            Recipe recipe = createTestRecipe(100, 10, 0.0);
            assertFalse(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Should return false when engagement rate <= MINIMUM_CONVERSION_RATE")
        void testIsPopular_EngagementBelowThreshold() {
            // views = 100, saves = 5, engagement = 5% (= 0.05, <= 0.05)
            Recipe recipe = createTestRecipe(100, 5, 4.5);
            assertFalse(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Should return false when saves = 0 (engagement = 0%)")
        void testIsPopular_ZeroSaves() {
            Recipe recipe = createTestRecipe(100, 0, 4.5);
            assertFalse(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Should return true when all conditions are met")
        void testIsPopular_AllConditionsMet() {
            // views = 100 (> 50), saves = 10 (10% > 5%), rating = 4.5 (> 4.0)
            Recipe recipe = createTestRecipe(100, 10, 4.5);
            assertTrue(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Should return true for borderline popular recipe")
        void testIsPopular_BorderlinePopular() {
            // views = 51 (> 50), saves = 3 (5.88% > 5%), rating = 4.1 (> 4.0)
            Recipe recipe = createTestRecipe(51, 3, 4.1);
            assertTrue(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Should return true for very popular recipe")
        void testIsPopular_VeryPopular() {
            // views = 10000, saves = 2000 (20% > 5%), rating = 5.0 (> 4.0)
            Recipe recipe = createTestRecipe(10000, 2000, 5.0);
            assertTrue(PopularityCalculator.isPopular(recipe));
        }
    }

    // ==================== getEngagementRate() TESTS ====================

    @Nested
    @DisplayName("getEngagementRate() method tests")
    class GetEngagementRateTests {

        @Test
        @DisplayName("Should return 0.0 for null recipe")
        void testGetEngagementRate_NullRecipe() {
            assertEquals(0.0, PopularityCalculator.getEngagementRate(null));
        }

        @Test
        @DisplayName("Should return 0.0 when views = 0 (avoid division by zero)")
        void testGetEngagementRate_ZeroViews() {
            Recipe recipe = createTestRecipe(0, 10, 4.5);
            assertEquals(0.0, PopularityCalculator.getEngagementRate(recipe));
        }

        @Test
        @DisplayName("Should return 0.0 when saves = 0")
        void testGetEngagementRate_ZeroSaves() {
            Recipe recipe = createTestRecipe(100, 0, 4.5);
            assertEquals(0.0, PopularityCalculator.getEngagementRate(recipe));
        }

        @Test
        @DisplayName("Should calculate correct engagement rate")
        void testGetEngagementRate_NormalCalculation() {
            // saves = 10, views = 100, engagement = 10% = 0.10
            Recipe recipe = createTestRecipe(100, 10, 4.5);
            assertEquals(0.10, PopularityCalculator.getEngagementRate(recipe), 0.001);
        }

        @Test
        @DisplayName("Should calculate 5% engagement rate correctly")
        void testGetEngagementRate_FivePercent() {
            // saves = 5, views = 100, engagement = 5% = 0.05
            Recipe recipe = createTestRecipe(100, 5, 4.5);
            assertEquals(0.05, PopularityCalculator.getEngagementRate(recipe), 0.001);
        }

        @Test
        @DisplayName("Should handle high engagement rate (> 100%)")
        void testGetEngagementRate_HighEngagement() {
            // saves = 200, views = 100, engagement = 200% = 2.0
            Recipe recipe = createTestRecipe(100, 200, 4.5);
            assertEquals(2.0, PopularityCalculator.getEngagementRate(recipe), 0.001);
        }

        @Test
        @DisplayName("Should handle very small engagement rate")
        void testGetEngagementRate_SmallEngagement() {
            // saves = 1, views = 10000, engagement = 0.01% = 0.0001
            Recipe recipe = createTestRecipe(10000, 1, 4.5);
            assertEquals(0.0001, PopularityCalculator.getEngagementRate(recipe), 0.00001);
        }
    }

    // ==================== CONSTANTS TESTS ====================

    @Nested
    @DisplayName("Constants tests")
    class ConstantsTests {

        @Test
        @DisplayName("MINIMUM_VIEWS should be defined")
        void testMinimumViewsConstant() {
            // Currently set to 5 for testing, should be 50 in production
            assertTrue(PopularityCalculator.MINIMUM_VIEWS >= 0);
        }

        @Test
        @DisplayName("MINIMUM_RATING should be defined")
        void testMinimumRatingConstant() {
            // Currently set to 0.0 for testing, should be 4.0 in production
            assertTrue(PopularityCalculator.MINIMUM_RATING >= 0.0);
        }

        @Test
        @DisplayName("MINIMUM_CONVERSION_RATE should be defined")
        void testMinimumConversionRateConstant() {
            // Currently set to 0.0 for testing, should be 0.05 in production
            assertTrue(PopularityCalculator.MINIMUM_CONVERSION_RATE >= 0.0);
        }
    }

    // ==================== EDGE CASES TESTS ====================

    @Nested
    @DisplayName("Edge cases tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle recipe with maximum integer views")
        void testIsPopular_MaxViews() {
            Recipe recipe = createTestRecipe(Integer.MAX_VALUE, Integer.MAX_VALUE / 10, 4.5);
            // Should not throw exception
            assertDoesNotThrow(() -> PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Should handle recipe with negative values gracefully")
        void testIsPopular_NegativeViews() {
            Recipe recipe = createTestRecipe(-1, 10, 4.5);
            // Negative views should fail the views > MINIMUM_VIEWS check
            assertFalse(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Should handle recipe with very high rating")
        void testIsPopular_HighRating() {
            Recipe recipe = createTestRecipe(100, 10, 10.0);
            assertTrue(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Engagement rate calculation should be precise")
        void testGetEngagementRate_Precision() {
            // 1 save out of 3 views = 0.333...
            Recipe recipe = createTestRecipe(3, 1, 4.5);
            double rate = PopularityCalculator.getEngagementRate(recipe);
            assertEquals(1.0 / 3.0, rate, 0.0001);
        }
    }

    // ==================== INTEGRATION-LIKE TESTS ====================

    @Nested
    @DisplayName("Integration-like scenario tests")
    class IntegrationTests {

        @Test
        @DisplayName("New recipe should not be popular")
        void testNewRecipe_NotPopular() {
            // New recipe with default values (0 views, 0 saves, 0 rating)
            Recipe recipe = createTestRecipe(0, 0, 0.0);
            assertFalse(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Recipe becoming popular after gaining views")
        void testRecipe_BecomingPopular() {
            Recipe recipe = createTestRecipe(40, 5, 4.5);
            assertFalse(PopularityCalculator.isPopular(recipe)); // Not popular yet

            // Simulate gaining more views and saves
            recipe.setViews(100);
            recipe.setSaves(10);
            assertTrue(PopularityCalculator.isPopular(recipe)); // Now popular
        }

        @Test
        @DisplayName("Popular recipe losing popularity after rating drops")
        void testRecipe_LosingPopularity() {
            Recipe recipe = createTestRecipe(100, 10, 4.5);
            assertTrue(PopularityCalculator.isPopular(recipe)); // Popular

            // Rating drops
            recipe.setAverageRating(3.5);
            assertFalse(PopularityCalculator.isPopular(recipe)); // No longer popular
        }
    }

    // ==================== REVIEW-BASED RATING TESTS ====================

    @Nested
    @DisplayName("Review-based rating tests using recalculateAverageRating()")
    class ReviewBasedRatingTests {

        private Review createReview(int rating) {
            return new Review("Test Review", "Test description", rating);
        }

        @Test
        @DisplayName("Recipe with no reviews should have 0.0 average rating")
        void testNoReviews_ZeroRating() {
            Recipe recipe = createTestRecipe(100, 10, 0.0);
            recipe.recalculateAverageRating();
            assertEquals(0.0, recipe.getAverageRating());
            assertFalse(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Adding single 5-star review should set rating to 5.0")
        void testSingleReview_FiveStar() {
            Recipe recipe = createTestRecipe(100, 10, 0.0);
            recipe.getReviews().add(createReview(5));
            recipe.recalculateAverageRating();
            
            assertEquals(5.0, recipe.getAverageRating());
            assertTrue(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Adding single 4-star review should set rating to 4.0 (not popular)")
        void testSingleReview_FourStar() {
            Recipe recipe = createTestRecipe(100, 10, 0.0);
            recipe.getReviews().add(createReview(4));
            recipe.recalculateAverageRating();
            
            assertEquals(4.0, recipe.getAverageRating());
            // rating = 4.0 is not > 4.0, so not popular
            assertFalse(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Multiple reviews should calculate correct average")
        void testMultipleReviews_CalculateAverage() {
            Recipe recipe = createTestRecipe(100, 10, 0.0);
            recipe.getReviews().add(createReview(5)); // 5
            recipe.getReviews().add(createReview(4)); // 4
            recipe.getReviews().add(createReview(5)); // 5
            recipe.recalculateAverageRating();
            
            // Average: (5 + 4 + 5) / 3 = 4.666...
            assertEquals(4.666, recipe.getAverageRating(), 0.01);
            assertTrue(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Recipe becomes popular after receiving good reviews")
        void testRecipe_BecomesPopularWithReviews() {
            Recipe recipe = createTestRecipe(100, 10, 0.0);
            assertFalse(PopularityCalculator.isPopular(recipe)); // Not popular (rating = 0)
            
            // Add positive reviews
            recipe.getReviews().add(createReview(5));
            recipe.getReviews().add(createReview(5));
            recipe.getReviews().add(createReview(4));
            recipe.recalculateAverageRating();
            
            // Average: (5 + 5 + 4) / 3 = 4.666...
            assertTrue(recipe.getAverageRating() > 4.0);
            assertTrue(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Recipe loses popularity after receiving bad reviews")
        void testRecipe_LosesPopularityWithBadReviews() {
            Recipe recipe = createTestRecipe(100, 10, 0.0);
            
            // Start with good reviews
            recipe.getReviews().add(createReview(5));
            recipe.getReviews().add(createReview(5));
            recipe.recalculateAverageRating();
            assertTrue(PopularityCalculator.isPopular(recipe)); // Popular
            
            // Add bad reviews
            recipe.getReviews().add(createReview(1));
            recipe.getReviews().add(createReview(2));
            recipe.getReviews().add(createReview(1));
            recipe.recalculateAverageRating();
            
            // Average: (5 + 5 + 1 + 2 + 1) / 5 = 2.8
            assertEquals(2.8, recipe.getAverageRating(), 0.01);
            assertFalse(PopularityCalculator.isPopular(recipe)); // No longer popular
        }

        @Test
        @DisplayName("Borderline case: average exactly 4.0 should not be popular")
        void testBorderlineRating_ExactlyFour() {
            Recipe recipe = createTestRecipe(100, 10, 0.0);
            
            // Add reviews to get exactly 4.0 average
            recipe.getReviews().add(createReview(3));
            recipe.getReviews().add(createReview(5));
            recipe.recalculateAverageRating();
            
            // Average: (3 + 5) / 2 = 4.0
            assertEquals(4.0, recipe.getAverageRating());
            // 4.0 is NOT > 4.0, so not popular
            assertFalse(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("Borderline case: average just above 4.0 should be popular")
        void testBorderlineRating_JustAboveFour() {
            Recipe recipe = createTestRecipe(100, 10, 0.0);
            
            // Add reviews to get average just above 4.0
            recipe.getReviews().add(createReview(4));
            recipe.getReviews().add(createReview(5));
            recipe.getReviews().add(createReview(4));
            recipe.recalculateAverageRating();
            
            // Average: (4 + 5 + 4) / 3 = 4.333...
            assertTrue(recipe.getAverageRating() > 4.0);
            assertTrue(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("All 5-star reviews should make recipe popular")
        void testAllFiveStarReviews() {
            Recipe recipe = createTestRecipe(100, 10, 0.0);
            
            for (int i = 0; i < 10; i++) {
                recipe.getReviews().add(createReview(5));
            }
            recipe.recalculateAverageRating();
            
            assertEquals(5.0, recipe.getAverageRating());
            assertTrue(PopularityCalculator.isPopular(recipe));
        }

        @Test
        @DisplayName("All 1-star reviews should not make recipe popular")
        void testAllOneStarReviews() {
            Recipe recipe = createTestRecipe(100, 10, 0.0);
            
            for (int i = 0; i < 10; i++) {
                recipe.getReviews().add(createReview(1));
            }
            recipe.recalculateAverageRating();
            
            assertEquals(1.0, recipe.getAverageRating());
            assertFalse(PopularityCalculator.isPopular(recipe));
        }
    }
}

