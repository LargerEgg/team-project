package use_case;

import entity.Recipe;
import entity.Review;
import entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import use_case.edit_review.*;
import use_case.view_recipe.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class EditReviewInteractorTest {
    private TestEditReviewDataAccess dataAccess;
    private TestEditRecipePresenter presenter;
    private EditReviewInteractor interactor;

    @BeforeEach
    void setUp() {
        dataAccess = new TestEditReviewDataAccess();
        presenter = new TestEditRecipePresenter();
        interactor = new EditReviewInteractor(dataAccess, presenter);
    }

    @Test
    @DisplayName("Edit Recipe: Success - valid input data")
    void testEditRecipeSuccess() {
        // Arrange
        String title = "testTitle";
        String description = "testDescription";
        int rating = 5;
        String author = "testAuthor";
        String recipeId = "testRecipeId";

        EditReviewInputData inputData = new EditReviewInputData(title, description, rating, author, recipeId);

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.successViewCalled);
        assertFalse(presenter.failViewCalled);
        assertNotNull(presenter.outputData);
        String message = presenter.outputData.getMessage();
        String actualReviewId = presenter.outputData.getReviewId();
        assertNotNull(actualReviewId);
        assertNotNull(message);
    }

    @Test
    @DisplayName("View Recipe: Failure - invalid rating")
    void testViewRecipeFailureWrongRating() {
        // Arrange
        String title = "testTitle";
        String description = "testDescription";
        int rating = 6;
        String author = "testAuthor";
        String recipeId = "testRecipeId";
        EditReviewInputData inputData = new EditReviewInputData(title, description, rating, author, recipeId);

        // Act
        interactor.publish(inputData);


        // Assert
        assertTrue(presenter.failViewCalled);
        assertFalse(presenter.successViewCalled);
        assertEquals("Rating must be between 1 and 5.", presenter.errorMessage);
    }

    @Test
    @DisplayName("View Recipe: Failure - empty title")
    void testViewRecipeFailureNoTitle() {
        // Arrange
        String title = "";
        String description = "testDescription";
        int rating = 5;
        String author = "testAuthor";
        String recipeId = "testRecipeId";
        EditReviewInputData inputData = new EditReviewInputData(title, description, rating, author, recipeId);

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertFalse(presenter.successViewCalled);
        assertEquals("Title cannot be empty.", presenter.errorMessage);
    }

    @Test
    @DisplayName("View Recipe: Failure - empty description")
    void testViewRecipeFailureNoDescription() {
        // Arrange
        String title = "testTitle";
        String description = "";
        int rating = 5;
        String author = "testAuthor";
        String recipeId = "testRecipeId";
        EditReviewInputData inputData = new EditReviewInputData(title, description, rating, author, recipeId);

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertFalse(presenter.successViewCalled);
        assertEquals("Description cannot be empty.", presenter.errorMessage);
    }

    @Test
    @DisplayName("View Recipe: Success - Null username")
    void testViewRecipeSuccessNullUsername() {
        String title = "testTitle";
        String description = "testDescription";
        int rating = 5;
        String author = null;
        String recipeId = "testRecipeId";

        EditReviewInputData inputData = new EditReviewInputData(title, description, rating, author, recipeId);

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.successViewCalled);
        assertFalse(presenter.failViewCalled);
        assertNotNull(presenter.outputData);
        String message = presenter.outputData.getMessage();
        String actualReviewId = presenter.outputData.getReviewId();
        assertNotNull(actualReviewId);
        assertNotNull(message);
    }

    private Review createTestReview(String reviewId) {
        return new Review(
                reviewId,
                "valid recipe ID",
                "valid author ID",
                new Date(),
                "Test Title",
                "Test Description",
                5
        );
    }

    // Test double for EditReviewOutputBoundary
    private static class TestEditRecipePresenter implements EditReviewOutputBoundary {
        boolean successViewCalled = false;
        boolean failViewCalled = false;
        EditReviewOutputData outputData = null;
        String errorMessage = null;

        @Override
        public void prepareSuccessView(EditReviewOutputData outputData) {
            successViewCalled = true;
            this.outputData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage, EditReviewInputData inputData) {
            failViewCalled = true;
            this.errorMessage = errorMessage;
        }
    }

    public static class TestEditReviewDataAccess implements EditReviewDataAccessInterface {

        @Override
        public void changeReview(Review review) {

        }

        @Override
        public Review saveReview(Review review) {
            return null;
        }

        @Override
        public boolean existsByReviewId(String reviewId) {
            return false;
        }

        @Override
        public Review findById(String reviewId) {
            return null;
        }

        @Override
        public User findUserByUsername(String username) {
            return null;
        }

        @Override
        public void recordReviewRecipe(String recipeId, Review review) {

        }

        @Override
        public Review findByAuthor(String authorId, String recipeId) {
            return null;
        }

        @Override
        public List<Review> findByRecipe(String recipeId) {
            return List.of();
        }
    }
}
