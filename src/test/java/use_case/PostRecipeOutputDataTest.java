package use_case;

import org.junit.jupiter.api.Test;
import use_case.post_recipe.PostRecipeOutputData;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostRecipeOutputDataTest {

    @Test
    void testPostRecipeOutputData() {
        String recipeId = "123";
        String message = "Success";
        PostRecipeOutputData outputData = new PostRecipeOutputData(recipeId, message);

        assertEquals(recipeId, outputData.getRecipeId());
        assertEquals(message, outputData.getMessage());
    }
}
