package entity;

/**
 * Utility class for calculating recipe popularity.
 * A recipe is considered "popular" when it meets all three criteria:
 * 1. Minimum Exposure: views > 50
 * 2. Quality Baseline: average rating > 4.0
 * 3. Engagement Conversion Rate: (saves / views) > 5%
 */
public final class PopularityCalculator {

    /** Minimum number of views required (sample size threshold) */
    public static final int MINIMUM_VIEWS = 50;

    /** Minimum average rating required (quality baseline) */
    public static final double MINIMUM_RATING = 4.0;

    /** Minimum engagement conversion rate required (saves/views ratio) */
    public static final double MINIMUM_CONVERSION_RATE = 0.05;

    /** Fire emoji for popular recipes */
    public static final String FIRE_EMOJI = "🔥";

    // Private constructor to prevent instantiation
    private PopularityCalculator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Determines if a recipe is popular based on views, rating, and engagement rate.
     *
     * @param recipe the recipe to evaluate
     * @return true if the recipe meets all popularity criteria, false otherwise
     */
    public static boolean isPopular(Recipe recipe) {
        if (recipe == null) {
            return false;
        }

        // 1. Check minimum exposure (views > 50)
        if (recipe.getViews() <= MINIMUM_VIEWS) {
            return false;
        }

        // 2. Check quality baseline (average rating > 4.0)
        if (recipe.getAverageRating() <= MINIMUM_RATING) {
            return false;
        }

        // 3. Check engagement conversion rate (saves/views > 5%)
        double engagementRate = getEngagementRate(recipe);
        if (engagementRate <= MINIMUM_CONVERSION_RATE) {
            return false;
        }

        return true;
    }

    /**
     * Calculates the engagement conversion rate (saves / views).
     *
     * @param recipe the recipe to calculate engagement rate for
     * @return the engagement rate as a decimal (e.g., 0.05 for 5%), or 0.0 if views is 0
     */
    public static double getEngagementRate(Recipe recipe) {
        if (recipe == null || recipe.getViews() == 0) {
            return 0.0;
        }
        return (double) recipe.getSaves() / recipe.getViews();
    }

    /**
     * Returns the display title with fire emoji prefix if the recipe is popular.
     *
     * @param recipe the recipe to get the display title for
     * @return the title with fire emoji if popular, otherwise the original title
     */
    public static String getDisplayTitle(Recipe recipe) {
        if (recipe == null) {
            return "";
        }
        if (isPopular(recipe)) {
            return FIRE_EMOJI + " " + recipe.getTitle();
        }
        return recipe.getTitle();
    }
}

