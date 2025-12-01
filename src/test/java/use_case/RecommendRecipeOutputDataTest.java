package use_case.recommend_recipe;

import entity.Recipe;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecommendRecipeOutputDataTest {

    @Test
    void testOutputDataDataStorage() {
        // Arrange
        // Create a simulated list of Recipes
        List<Recipe> recipes = new ArrayList<>();
        // Here we can use an empty list for testing,
        // because OutputData simply passes through this list and doesn't care about the content.

        String categoryName = "Mix of your Top Favorites";

        // Act
        RecommendRecipeOutputData outputData = new RecommendRecipeOutputData(recipes, categoryName);

        // Assert
        // Verify getter returns the exact same object passed in constructor
        assertSame(recipes, outputData.getRecipes(), "The recipes list should be the same object passed in constructor");
        assertEquals(categoryName, outputData.getCategoryName(), "The category name should match the one passed in constructor");
    }

    @Test
    void testOutputDataWithNullValues() {
        // Arrange & Act
        // Edge Case: Test if the constructor accepts null values (if the design allows it)
        // If your OutputData is not supposed to accept nulls, this test should expect an Exception.
        // But typically simple DTOs just store whatever is passed.
        RecommendRecipeOutputData outputData = new RecommendRecipeOutputData(null, null);

        // Assert
        assertNull(outputData.getRecipes(), "Recipes list should be null if initialized with null");
        assertNull(outputData.getCategoryName(), "Category name should be null if initialized with null");
    }
}