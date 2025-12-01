package use_case;

import org.junit.jupiter.api.Test;
import use_case.save_recipe.SaveRecipeOutputData;

import static org.junit.jupiter.api.Assertions.*;

class SaveRecipeOutputDataTest {

    @Test
    void testGetRecipeName() {
        SaveRecipeOutputData outputData = new SaveRecipeOutputData("Chocolate Cake", true);
        assertEquals("Chocolate Cake", outputData.getRecipeName());
    }

    @Test
    void testIsSaved_True() {
        SaveRecipeOutputData outputData = new SaveRecipeOutputData("Apple Pie", true);
        assertTrue(outputData.isSaved());
    }

    @Test
    void testIsSaved_False() {
        SaveRecipeOutputData outputData = new SaveRecipeOutputData("Banana Bread", false);
        assertFalse(outputData.isSaved());
    }

    @Test
    void testConstructor() {
        String recipeName = "Grilled Salmon";
        boolean saved = true;
        SaveRecipeOutputData outputData = new SaveRecipeOutputData(recipeName, saved);

        assertEquals(recipeName, outputData.getRecipeName());
        assertEquals(saved, outputData.isSaved());
    }
}

