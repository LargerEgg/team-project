package data_access;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import entity.Ingredient;
import entity.Recipe;
import entity.Review;
import use_case.save_recipe.SaveRecipeDataAccessInterface;
import use_case.saved_recipes.ShowSavedRecipesDataAccessInterface;
import use_case.unsave_recipe.UnsaveRecipeDataAccessInterface;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class FirebaseSaveRecipeDataAccessObject implements SaveRecipeDataAccessInterface, ShowSavedRecipesDataAccessInterface, UnsaveRecipeDataAccessInterface {

    private final Firestore db;
    private final CollectionReference recipesCollection;
    private final RecipeDataAccessObject apiRecipeDAO;
    private final FirebaseRecipeDataAccessObject firebaseRecipeDAO; // Add this line

    public FirebaseSaveRecipeDataAccessObject(RecipeDataAccessObject apiRecipeDAO) {
        this.db = FirebaseInitializer.getFirestore();
        this.recipesCollection = this.db.collection("recipes");
        this.apiRecipeDAO = apiRecipeDAO;
        this.firebaseRecipeDAO = new FirebaseRecipeDataAccessObject(db); // Initialize it here
    }

    @Override
    public boolean isRecipeSaved(String username, String recipeID) {
        DocumentReference userDocRef = db.collection("users").document(username);
        try {
            DocumentSnapshot userDoc = userDocRef.get().get();
            if (userDoc.exists()) {
                List<String> savedRecipeIds = (List<String>) userDoc.get("recipes");
                return savedRecipeIds != null && savedRecipeIds.contains(recipeID);
            }
            return false;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error checking if recipe is saved", e);
        }
    }


    @Override
    public void saveRecipe(String username, String recipeID) {
        DocumentReference doc = db.collection("users").document(username);

        try {
            doc.update("recipes", FieldValue.arrayUnion(recipeID)).get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving recipe", e);
        }

    }

    public Recipe findById(String recipeId) {
        try {
            DocumentReference docRef = recipesCollection.document(recipeId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return documentToRecipe(document);
            }

            if (apiRecipeDAO != null) {
                Recipe apiRecipe = apiRecipeDAO.findById(recipeId);
                if (apiRecipe != null) {
                    // Fetch views from the separate recipe_views collection
                    try {
                        Integer actualViews = firebaseRecipeDAO.getViewCount(recipeId).get();
                        apiRecipe.setViews(actualViews);
                    } catch (InterruptedException | ExecutionException e) {
                        System.err.println("Error fetching view count for API recipe " + recipeId + ": " + e.getMessage());
                        apiRecipe.setViews(0);
                    }

                    // Fetch saves from the recipes collection (where it's stored for all recipes, including API ones after saving)
                    try {
                        Integer actualSaves = firebaseRecipeDAO.getSaveCount(recipeId).get();
                        apiRecipe.setSaves(actualSaves);
                    } catch (InterruptedException | ExecutionException e) {
                        System.err.println("Error fetching save count for API recipe " + recipeId + ": " + e.getMessage());
                        apiRecipe.setSaves(0);
                    }
                    return apiRecipe;
                }
            }

            return null;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding recipe", e);
        }
    }

    private Recipe documentToRecipe(DocumentSnapshot doc) {
        String recipeId = doc.getString("recipeId");
        String authorId = doc.getString("authorId");
        String title = doc.getString("title");
        String description = doc.getString("description");
        String category = doc.getString("category");
        String imagePath = doc.getString("imagePath");
        String statusStr = doc.getString("status");

        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) doc.get("tags");
        if (tags == null) {
            tags = new ArrayList<>();
        }

        @SuppressWarnings("unchecked")
        List<HashMap<String, Object>> hashmap = (List<HashMap<String, Object>>) doc.get("reviews");
        List<Review> reviews = new ArrayList<>();
        if (hashmap != null && !hashmap.isEmpty()) {
            for (HashMap<String, Object> map : hashmap) {
                String reviewId = (String) map.get("reviewId");
                String authorId1 = (String) map.get("authorId");
                Timestamp ts = (Timestamp) map.get("dateCreated");
                Date dateCreated = ts.toDate();
                String title1 = (String) map.get("title");
                String description1 = (String) map.get("description");
                Long ratingLong = (Long) map.get("rating");
                int rating = ratingLong.intValue();
                reviews.add(new Review(reviewId, recipeId, authorId1, dateCreated, title1, description1, rating));
            }
        }

        @SuppressWarnings("unchecked")
        List<Map<String, String>> ingredientsData = (List<Map<String, String>>) doc.get("ingredients");
        List<Ingredient> ingredients = new ArrayList<>();
        if (ingredientsData != null) {
            for (Map<String, String> ingData : ingredientsData) {
                ingredients.add(new Ingredient(ingData.get("name"), ingData.get("measure")));
            }
        }

        Long creationTime = doc.getLong("creationDate");
        Long updateTime = doc.getLong("updateDate");
        Date creationDate = creationTime != null ? new Date(creationTime) : new Date();
        Date updateDate = updateTime != null ? new Date(updateTime) : new Date();

        Recipe.Status status = null;
        if (statusStr != null) {
            try {
                status = Recipe.Status.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                status = Recipe.Status.DRAFT;
            }
        }

        Recipe recipe = new Recipe(
                recipeId,
                authorId,
                title,
                description,
                ingredients,
                category,
                tags,
                status,
                creationDate,
                updateDate,
                imagePath
        );

        // Fetch views from the separate recipe_views collection using firebaseRecipeDAO
        try {
            Integer actualViews = firebaseRecipeDAO.getViewCount(recipeId).get(); // Synchronously get views
            recipe.setViews(actualViews);
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error fetching view count for recipe " + recipeId + " in FirebaseSaveRecipeDataAccessObject: " + e.getMessage());
            recipe.setViews(0); // Default to 0 on error
        }

        Long saves = doc.getLong("saves");
        Boolean shareable = doc.getBoolean("shareable");

        if (saves != null) recipe.setSaves(saves.intValue());
        if (shareable != null) recipe.setShareable(shareable);

        recipe.setReviews(reviews);
        recipe.recalculateAverageRating();

        return recipe;
    }

    @Override
    public List<Recipe> getRecipes(String username) {
        DocumentReference doc = db.collection("users").document(username);

        try {
            DocumentSnapshot snapshot = doc.get().get();

            if (!snapshot.exists()) {
                return new ArrayList<>();
            }

            List<String> recipeIDs = (List<String>) snapshot.get("recipes");

            if (recipeIDs == null || recipeIDs.isEmpty()) {
                return new ArrayList<>();
            }

            List<Recipe> recipes = new ArrayList<>();
            for (String recipeID : recipeIDs) {

                if (recipeID == null || recipeID.isEmpty()) {continue;}

                Recipe recipe = findById(recipeID);
                if (recipe != null) {
                    recipes.add(recipe);
                }
            }

            return recipes;


        } catch (Exception e) {
            throw new RuntimeException("Error getting recipes", e);
        }

    }

    @Override
    public void unsaveRecipe(String username, String recipeID) {
        DocumentReference doc = db.collection("users").document(username);

        try {
            doc.update("recipes", FieldValue.arrayRemove(recipeID)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error removing recipe", e);
        }
    }

    @Override
    public void unsave(String username, String recipeId) {
        unsaveRecipe(username, recipeId);
    }
}
