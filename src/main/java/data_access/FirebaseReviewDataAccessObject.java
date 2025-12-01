package data_access;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;

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


    public FirebaseReviewDataAccessObject() {
        this.db = FirebaseInitializer.getFirestore();
        this.reviewsCollection = db.collection("reviews");
        this.recipesCollection = db.collection("recipes");
    }


    @Override
    public void changeReview(Review review) {
        try {
            String title = review.getTitle();
            String description = review.getDescription();
            DocumentReference reviewDocRef = reviewsCollection.document(review.getReviewId());

            reviewDocRef.update("title", title);
            reviewDocRef.update("description", description);
            reviewDocRef.update("rating", review.getRating());
            reviewDocRef.update("dateCreated", review.getDateCreated());

            DocumentReference recipeDocRef = recipesCollection.document(review.getRecipeId());
            ApiFuture<DocumentSnapshot> future = recipeDocRef.get();
            DocumentSnapshot doc = future.get();

            List<HashMap<String, Object>> hashmap = (List<HashMap<String, Object>>) doc.get("reviews");
            List<Review> tempReviews = new ArrayList<>();
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
                    tempReviews.add(new Review(reviewId, reviewRecipeId, authorId1, dateCreated, title1, description1, rating));
                }
            }

            for (Review review1 : tempReviews) {
                if (!(review1.getReviewId().equals(review.getReviewId()))) {
                    reviews.add(review1);
                }
            }
            reviews.add(review);
            double totalRating = 0.0;
            for (Review review2 : reviews) {
                totalRating += review2.getRating();
            }
            double averageRating = totalRating / tempReviews.size();
            recipeDocRef.update("reviews", reviews);
            recipeDocRef.update("averageRating", averageRating);
        } catch (InterruptedException | ExecutionException e) {
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
            data.put("dateCreated", review.getDateCreated());

            reviewsCollection.document(review.getReviewId()).set(data).get();

            System.out.println("review saved successfully: " + review.getReviewId());

            return review;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving review", e);
        }
    }

    public void recordReviewRecipe(String recipeId, Review review) {
        try {
            DocumentReference recipeDocRef = recipesCollection.document(recipeId);
            ApiFuture<DocumentSnapshot> future = recipeDocRef.get();
            DocumentSnapshot doc = future.get();

            List<HashMap<String, Object>> hashmap = (List<HashMap<String, Object>>) doc.get("reviews");
            List<Review> reviews = new ArrayList<>();
            if (hashmap == null || hashmap.isEmpty()) {
            } else {
                for (HashMap<String, Object> map : hashmap) {
                    String reviewId = (String) map.get("reviewId");
                    String authorId1 = (String) map.get("authorId");
                    Timestamp ts = (Timestamp) map.get("dateCreated");
                    Date dateCreated = new Date();
                    if (ts != null) {
                        dateCreated = ts.toDate();
                    }
                    String title1 = (String) map.get("title");
                    String description1 = (String) map.get("description");
                    Long ratingLong = (Long) map.get("rating");
                    int rating = ratingLong.intValue();
                    reviews.add(new Review(reviewId, recipeId, authorId1, dateCreated, title1, description1, rating));
                }
            }
            reviews.add(review);
            double totalRating = 0.0;
            for (Review review2 : reviews) {
                totalRating += review2.getRating();
            }
            double averageRating = totalRating / reviews.size();

            recipeDocRef.update("reviews", reviews);
            recipeDocRef.update("averageRating", averageRating);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error recording review", e);
        }
    }

    @Override
    public boolean existsByReviewId(String reviewId) {
        try {
            DocumentReference docref = reviewsCollection.document(reviewId);
            ApiFuture<DocumentSnapshot> future = docref.get();
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
    public Review findByAuthor(String authorId, String recipeId) {
        List<Review> reviews = findByRecipe(recipeId);

        if (reviews == null || reviews.isEmpty()) {
            return null;
        }

        for (Review review : reviews) {
            if (review.getAuthorId().equals(authorId)) {
                return review;
            }
        }

        return null;
    }

    @Override
    public List<Review> findByRecipe(String recipeId) {
        try {
            ApiFuture<QuerySnapshot> future = reviewsCollection.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Review> reviews = new ArrayList<>();

            for (DocumentSnapshot doc : documents) {
                Review tempReview = documentToReview(doc);
                if (tempReview != null && tempReview.getRecipeId().equals(recipeId)) {
                    reviews.add(tempReview);
                }
            }

            // legacy from when multiple reviews could be made by one guy
            if (reviews.isEmpty()) {
                return null;
            }
            return reviews;

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error finding recipes by recipe: " + e.getMessage());
            throw new RuntimeException("Error finding recipes by recipe", e);
        }
    }

    public User findUserByUsername(String username) {
        return null;
    }

    private Review documentToReview(DocumentSnapshot document) {
        String reviewId = document.getString("review_id");
        String recipeId = document.getString("recipe_id");
        String authorId = document.getString("author_id");
        Date dateCreated = document.getDate("dateCreated");
        if (dateCreated == null) {
            dateCreated = new Date();
        }
        String title = document.getString("title");
        String description = document.getString("description");

        if (document.getLong("rating") == null) {
            return null;
        }
        int rating = document.getLong("rating").intValue();


        return new Review(reviewId, recipeId, authorId, dateCreated, title, description, rating);

    }

}

