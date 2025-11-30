package data_access;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;

import entity.Recipe;
import entity.Review;
import entity.User;

import use_case.edit_review.EditReviewDataAccessInterface;

import javax.swing.text.Document;
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
        try {
            DocumentReference reviewDocRef = recipesCollection.document(review.getReviewId());
            reviewDocRef.update("title", review.getTitle());
            reviewDocRef.update("description", review.getDescription());
            reviewDocRef.update("rating", review.getRating());

            DocumentReference recipeDocRef = recipesCollection.document(review.getRecipeId());
            ApiFuture<DocumentSnapshot> future = recipeDocRef.get();
            DocumentSnapshot doc = future.get();

            List<HashMap<String, Object>> hashmap = (List<HashMap<String, Object>>) doc.get("reviews");
            List<Review> reviews = new ArrayList<>();
            if (hashmap == null || hashmap.isEmpty()) {
            } else {
                for (HashMap<String, Object> map : hashmap) {
                    String reviewId = (String) map.get("reviewId");
                    String reviewRecipeId = (String) map.get("recipeId");
                    String authorId1 = (String) map.get("authorId");
                    Timestamp ts = (Timestamp) map.get("dateCreated");
                    Date dateCreated = ts.toDate();
                    String title1 = (String) map.get("title");
                    String description1 = (String) map.get("description");
                    Long ratingLong = (Long) map.get("rating");
                    int rating = ratingLong.intValue();
                    reviews.add(new Review(reviewId, reviewRecipeId, authorId1, dateCreated, title1, description1, rating));
                }
            }
            for (Review review1 : reviews) {
                if (review1.getReviewId().equals(review.getReviewId())) {
                    reviews.remove(review1);
                    reviews.add(review);
                }
            }
            recipeDocRef.update("reviews", reviews);
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error editing review", e);
        }

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
        DocumentReference recipeDocRef = recipesCollection.document(recipeId);

        ApiFuture<Void> future = db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(recipeDocRef).get();
            if (snapshot.exists()) {
                ArrayList<Review> reviews = (ArrayList<Review>) snapshot.get("reviews");
                reviews.add(review);
                transaction.update(recipeDocRef, "reviews", reviews);
            } else {
                Map<String, Object> data = new HashMap<>();
                ArrayList<Review> reviews = new ArrayList<>();
                reviews.add(review);
                data.put("reviews", reviews);
                transaction.set(recipeDocRef, data);
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
    public Review findById(String reviewId) {
        try {
            DocumentReference docref = reviewsCollection.document(reviewId);
            ApiFuture<DocumentSnapshot> future = docref.get();
            DocumentSnapshot document = future.get();

            if (!document.exists()) {
                return null;
            }

            return documentToReview(document);

        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            throw new RuntimeException("Error retrieving review", e);
        }
    }

    @Override
    public Review findByAuthor(String authorId) {
        try {
            ApiFuture<QuerySnapshot> future = reviewsCollection.get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Review> reviews = new ArrayList<>();

            for (DocumentSnapshot doc : documents) {
                Review tempReview = documentToReview(doc);
                if (tempReview != null && tempReview.getAuthorId().equals(authorId)) {
                    reviews.add(tempReview);
                }
            }

            for (Review review : reviews) {
                if (review.getAuthorId().equals(authorId)) {
                    return review;
                }
            }

            // legacy from when multiple reviews could be made by one guy
            if (reviews.isEmpty()) {
                return null;
            }
            return null;

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error finding recipes by author: " + e.getMessage());
            throw new RuntimeException("Error finding recipes by author", e);
        }
    }

    public User findUserByUsername(String username){
        return null;
    }

    private Review documentToReview(DocumentSnapshot document){
        String reviewId = document.getString("review_id");
        String recipeId = document.getString("recipe_id");
        String authorId = document.getString("author_id");
        Date dateCreated = document.getDate("dateCreated");
        String title = document.getString("title");
        String description = document.getString("description");

        if (document.getLong("rating") == null) {
            return null;
        }
        int rating = document.getLong("rating").intValue();


        return new Review(reviewId, recipeId, authorId, dateCreated, title, description, rating);

    }

}

