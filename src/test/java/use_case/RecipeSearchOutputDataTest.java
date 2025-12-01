package use_case;

import entity.Recipe;
import org.junit.jupiter.api.Test;
import use_case.recipe_search.RecipeSearchOutputData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeSearchOutputDataTest {

    @Test
    void testRecipeSearchOutputData_finalResult() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe("1", "user", "title", "desc", new ArrayList<>(), "cuisine", new ArrayList<>(), Recipe.Status.PUBLISHED, null, null, ""));
        RecipeSearchOutputData outputData = new RecipeSearchOutputData(recipes);

        assertEquals(recipes, outputData.getRecipes());
        assertEquals(0, outputData.getCurrentImageCount());
        assertEquals(0, outputData.getTotalImageCount());
        assertTrue(outputData.isFinalResult());
    }

    @Test
    void testRecipeSearchOutputData_progressUpdate() {
        RecipeSearchOutputData outputData = new RecipeSearchOutputData(5, 10);

        assertNull(outputData.getRecipes());
        assertEquals(5, outputData.getCurrentImageCount());
        assertEquals(10, outputData.getTotalImageCount());
        assertFalse(outputData.isFinalResult());
    }

    @Test
    void testRecipeSearchOutputData_fullConstructor() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe("1", "user", "title", "desc", new ArrayList<>(), "cuisine", new ArrayList<>(), Recipe.Status.PUBLISHED, null, null, ""));
        RecipeSearchOutputData outputData = new RecipeSearchOutputData(recipes, 5, 10, true);

        assertEquals(recipes, outputData.getRecipes());
        assertEquals(5, outputData.getCurrentImageCount());
        assertEquals(10, outputData.getTotalImageCount());
        assertTrue(outputData.isFinalResult());
    }
}
