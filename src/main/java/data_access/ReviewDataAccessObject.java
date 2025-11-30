package data_access;

import entity.Review;
import entity.User;
import use_case.edit_review.EditReviewDataAccessInterface;

public class ReviewDataAccessObject implements EditReviewDataAccessInterface {
    //TODO: implement methods
    @Override
    public void changeReview(Review review) {
    }

    @Override
    public Review saveReview(Review review) {
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
    }

    @Override
    public Review findByAuthor(String authorId) {
        return null;
    }
}
