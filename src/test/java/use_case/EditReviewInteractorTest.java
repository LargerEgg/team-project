package use_case;

import entity.Review;
import entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import use_case.edit_review.*;
import use_case.view_recipe.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EditReviewInteractorTest {
    private TestEditReviewDataAccess dataAccess;
    private TestEditRecipePresenter presenter;
    private EditReviewInteractor interactor;
    private static List<Review> reviews = new ArrayList<>();

    @BeforeEach
    void setUp() {
        dataAccess = new TestEditReviewDataAccess();
        presenter = new TestEditRecipePresenter();
        interactor = new EditReviewInteractor(dataAccess, presenter);
    }

    @Test
    @DisplayName("Edit Review: Success - valid input data")
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
    @DisplayName("Edit Review: Failure - invalid rating")
    void testViewRecipeFailureBigRating() {
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
    @DisplayName("Edit Review: Failure - invalid rating, too small")
    void testViewRecipeFailureSmallRating() {
        // Arrange
        String title = "testTitle";
        String description = "testDescription";
        int rating = 0;
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
    @DisplayName("Edit Review: Failure - empty title")
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
    @DisplayName("Edit Review: Failure - empty description")
    void testEditReviewFailureNoDescription() {
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
    @DisplayName("Edit Review: Failure - empty authorId")
    void testEditReviewFailureNoAuthor() {
        // Arrange
        String title = "testTitle";
        String description = "testDescription";
        int rating = 5;
        String author = "";
        String recipeId = "testRecipeId";
        EditReviewInputData inputData = new EditReviewInputData(title, description, rating, author, recipeId);

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.successViewCalled);
        assertFalse(presenter.failViewCalled);
    }

    @Test
    @DisplayName("Edit Review: Success - Null username")
    void testEditReviewSuccessNullUsername() {
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

    @Test
    @DisplayName("Edit Review: Failure - Error in publish review")
    void testEditReviewPublishError() {
        String title = "Error";
        String description = "testDescription";
        int rating = 5;
        String author = "authorId2";
        String recipeId = "testRecipeId";

        EditReviewInputData inputData = new EditReviewInputData(title, description, rating, author, recipeId);

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertFalse(presenter.successViewCalled);
    }

    @Test
    @DisplayName("Edit Review: Failure - Error in change review")
    void testEditReviewChangeError() {
        String title = "Error";
        String description = "testDescription";
        int rating = 5;
        String author = "authorId";
        String recipeId = "testRecipeId";

        EditReviewInputData inputData = new EditReviewInputData(title, description, rating, author, recipeId);

        // Act
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.failViewCalled);
        assertFalse(presenter.successViewCalled);
    }

    @Test
    @DisplayName("Edit Review: Success - ChangeReview")
    void testEditReviewChangeSuccess() {
        String title = "testTitle";
        String description = "testDescription";
        int rating = 5;
        String author = "authorId";
        String recipeId = "testRecipeId";

        EditReviewInputData inputData = new EditReviewInputData(title, description, rating, author, recipeId);

        // Act
        interactor.publish(inputData);
        interactor.publish(inputData);

        // Assert
        assertTrue(presenter.successViewCalled);
        assertFalse(presenter.failViewCalled);
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
            if (Objects.equals(review.getTitle(), "Error")){
                throw new RuntimeException();
            }
        }

        @Override
        public Review saveReview(Review review) {
            if (Objects.equals(review.getTitle(), "Error")){
                throw new RuntimeException();
            }
            return review;
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
            reviews.add(review);
        }

        @Override
        public Review findByAuthor(String authorId, String recipeId) {
            for (Review review : reviews) {
                if (review.getAuthorId().equals(authorId) && review.getRecipeId().equals(recipeId)) {
                    return review;
                }
            }
            return null;
        }

        @Override
        public List<Review> findByRecipe(String recipeId) {
            List<Review> results = new ArrayList<>();
            for (Review review : reviews) {
                if (review.getRecipeId().equals(recipeId)) {
                    results.add(review);
                }
            }
            return results;
        }
    }
}
