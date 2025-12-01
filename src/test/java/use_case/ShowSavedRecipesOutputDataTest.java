package use_case;

import entity.Recipe;
import org.junit.jupiter.api.Test;
import use_case.saved_recipes.ShowSavedRecipesOutputData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShowSavedRecipesOutputDataTest {

    static Recipe createTestRecipe(String recipeId, String title) {
        return new Recipe(
                recipeId,
                "testAuthor",
                title,
                "Test description",
                new ArrayList<>(),
                "TestCategory",
                new ArrayList<>(),
                Recipe.Status.PUBLISHED,
                new Date(),
                new Date(),
                "image/path"
        );
    }

    @Test
    void testGetSavedRecipes_MultipleRecipes() {
        List<Recipe> recipes = Arrays.asList(
                createTestRecipe("r1", "Recipe 1"),
                createTestRecipe("r2", "Recipe 2")
        );
        ShowSavedRecipesOutputData outputData = new ShowSavedRecipesOutputData(recipes);

        assertEquals(2, outputData.getSaved_recipes().size());
        assertEquals("Recipe 1", outputData.getSaved_recipes().get(0).getTitle());
        assertEquals("Recipe 2", outputData.getSaved_recipes().get(1).getTitle());
    }

    @Test
    void testGetSavedRecipes_EmptyList() {
        ShowSavedRecipesOutputData outputData = new ShowSavedRecipesOutputData(new ArrayList<>());

        assertNotNull(outputData.getSaved_recipes());
        assertTrue(outputData.getSaved_recipes().isEmpty());
    }

    @Test
    void testGetSavedRecipes_NullList() {
        ShowSavedRecipesOutputData outputData = new ShowSavedRecipesOutputData(null);

        assertNull(outputData.getSaved_recipes());
    }

    @Test
    void testConstructor() {
        List<Recipe> recipes = Arrays.asList(createTestRecipe("test", "Test Recipe"));
        ShowSavedRecipesOutputData outputData = new ShowSavedRecipesOutputData(recipes);

        assertNotNull(outputData);
        assertEquals(1, outputData.getSaved_recipes().size());
    }
}

