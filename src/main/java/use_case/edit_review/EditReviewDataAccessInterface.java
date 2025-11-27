package use_case.edit_review;

import entity.Review;
import entity.User;

public interface EditReviewDataAccessInterface {
    Review findReviewById(String reviewId);

    boolean isReviewSavedByUser(String username, String reviewId);

    void saveReview(Review recipe);
}
