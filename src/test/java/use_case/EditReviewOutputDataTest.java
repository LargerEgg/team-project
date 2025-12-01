package use_case;

import org.junit.jupiter.api.Test;
import use_case.edit_review.EditReviewOutputData;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EditReviewOutputDataTest {

    @Test
    void testRecipeSearchOutputData_finalResult() {
        EditReviewOutputData outputData = new EditReviewOutputData("testReviewID", "testMessage");

        assertEquals("testReviewID", outputData.getReviewId());
        assertEquals("testMessage", outputData.getMessage());
    }
}
