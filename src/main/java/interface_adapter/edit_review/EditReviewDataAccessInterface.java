package interface_adapter.edit_review;

import entity.Review;

import java.util.UUID;

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
    void save(Review review);

    /**
     * Checks if the given recipe exists.
     * @param recipeId the recipe to look for
     * @param authorId the author to look for
     * @return true if a recipe with the given author and recipe exists; false otherwise
     */
    boolean existsByAuthorRecipe(UUID recipeId, UUID authorId);

    /**
     * Returns the review with the given reviewID.
     * @param reviewId the username to look up
     * @return the review with the given reviewID
     */
    Review get(UUID reviewId);

}
