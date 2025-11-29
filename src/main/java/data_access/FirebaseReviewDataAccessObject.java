package data_access;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import entity.Recipe;
import entity.Review;
import entity.User;

import use_case.edit_review.EditReviewDataAccessInterface;

import java.util.*;
import java.util.concurrent.ExecutionException;


/**
 * The DAO for recipes from the API.
 */
public class FirebaseReviewDataAccessObject implements EditReviewDataAccessInterface {
    private final Firestore db;
    private final CollectionReference reviewsCollection;
    private final CollectionReference recipesCollection;
    private final FirebaseRecipeDataAccessObject firebaseRecipeDataAccessObject;


    public FirebaseReviewDataAccessObject() {
        this.db = FirebaseInitializer.getFirestore();
        this.reviewsCollection = db.collection("reviews");
        this.recipesCollection = db.collection("recipes");
        this.firebaseRecipeDataAccessObject = new FirebaseRecipeDataAccessObject();
    }


    @Override
    public void changeReview(Review review) {

    }

    @Override
    public Review saveReview(Review review) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("review_id", review.getReviewId());
            data.put("recipe_id", review.getRecipeId());
            data.put("author_id", review.getAuthorId());

            data.put("title", review.getTitle());
            data.put("description", review.getDescription());
            data.put("rating", review.getRating());

            reviewsCollection.document(review.getReviewId()).set(data).get();

            System.out.println("review saved successfully: " + review.getReviewId());

            return review;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving review", e);
        }
    }

    public void recordReviewRecipe(String recipeId, Review review) {
        DocumentReference reviewDocRef = recipesCollection.document(recipeId);

        ApiFuture<Void> future = db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(reviewDocRef).get();
            if (snapshot.exists()) {
                ArrayList<Review> reviews = (ArrayList<Review>) snapshot.get("reviews");
                reviews.add(review);
                transaction.update(reviewDocRef, "reviews", reviews);
            } else {
                Map<String, Object> data = new HashMap<>();
                ArrayList<Review> reviews = new ArrayList<>();
                reviews.add(review);
                data.put("reviews", reviews);
                transaction.set(reviewDocRef, data);
            }
            return null;
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error recording review", e);
        }
    }

    @Override
    public boolean existsByReviewId(String reviewId) {
        try {
            DocumentReference docref = reviewsCollection.document(reviewId);
            ApiFuture<DocumentSnapshot> future =docref.get();
            DocumentSnapshot document = future.get();
            return document.exists();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error checking if review exists", e);
        }
    }

    @Override
    public Review get(String reviewId) {
        try {
            DocumentReference docref = reviewsCollection.document(reviewId);
            ApiFuture<DocumentSnapshot> future = docref.get();
            DocumentSnapshot document = future.get();

            if (!document.exists()) {
                return null;
            }

            String recipeId = document.getString("recipeId");
            String authorId = document.getString("authorId");
            Date dateCreated = document.getDate("dateCreated");
            String title = document.getString("title");
            String description = document.getString("description");

            int rating = document.getLong("rating").intValue();


            return new Review(reviewId, recipeId, authorId, dateCreated, title, description, rating);

        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            throw new RuntimeException("Error retrieving review", e);
        }
    }

    public User findUserByUsername(String username){
        return null;
    }

}

