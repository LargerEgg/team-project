package use_case;

import entity.Recipe;
import org.junit.jupiter.api.Test;
import use_case.view_recipe.ViewRecipeOutputData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewRecipeOutputDataTest {

    @Test
    void testViewRecipeOutputData() {
        Recipe recipe = new Recipe("1", "user", "title", "desc", null, "cuisine", null, Recipe.Status.PUBLISHED, null, null, "");
        String username = "testUser";
        boolean isSaved = true;

        ViewRecipeOutputData outputData = new ViewRecipeOutputData(recipe, username, isSaved);

        assertEquals(recipe, outputData.getRecipe());
        assertEquals(username, outputData.getUsername());
        assertTrue(outputData.isSaved());
    }
}
