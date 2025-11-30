package use_case.edit_review;

import entity.Review;
import entity.User;

/**
 * The DAO interface for the Change Password Use Case.
 */
public interface EditReviewDataAccessInterface {

    /**
     * Updates the system to record this recipe.
     * @param review the review which is to be updated
     */
    void changeReview(Review review);

    /**
     * Saves the review.
     * @param review the review to save
     */
    Review saveReview(Review review);

    /**
     * Checks if the given recipe exists.
     * @param reviewId the review to look for
     * @return true if the review exists; false otherwise
     */
    boolean existsByReviewId(String reviewId);

    /**
     * Returns the review with the given reviewID.
     * @param reviewId the username to look up
     * @return the review with the given reviewID
     */
    Review findById(String reviewId);

    User findUserByUsername(String username);

    void recordReviewRecipe(String recipeId, Review review);

    Review findByAuthor(String authorId);
}
