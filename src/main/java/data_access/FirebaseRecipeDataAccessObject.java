package data_access;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import entity.Ingredient;
import entity.Recipe;
import entity.Review;
import use_case.post_recipe.PostRecipeDataAccessInterface;
import use_case.view_recipe.ViewRecipeDataAccessInterface;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FirebaseRecipeDataAccessObject implements PostRecipeDataAccessInterface, ViewRecipeDataAccessInterface {
    private final Firestore db;
    private final CollectionReference recipesCollection;
    private final CollectionReference recipeViewsCollection;
    private final CollectionReference categoriesCollection;

    public FirebaseRecipeDataAccessObject() {
        this(FirebaseInitializer.getFirestore());
    }

    public FirebaseRecipeDataAccessObject(Firestore db) {
        this.db = db;
        this.recipesCollection = db.collection("recipes");
        this.recipeViewsCollection = db.collection("recipe_views");
        this.categoriesCollection = db.collection("categories");
    }

    @Override
    public Recipe saveRecipe(Recipe recipe) {
        try {
            // 1. Save the recipe to the "recipes" collection
            Map<String, Object> data = new HashMap<>();
            data.put("recipeId", recipe.getRecipeId());
            data.put("authorId", recipe.getAuthorId());
            data.put("title", recipe.getTitle());
            data.put("description", recipe.getDescription());
            data.put("category", recipe.getCategory());
            data.put("tags", recipe.getTags());
            data.put("imagePath", recipe.getImagePath());
            data.put("status", recipe.getStatus().toString());
            data.put("creationDate", recipe.getCreationDate().getTime());
            data.put("updateDate", recipe.getUpdateDate().getTime());
            data.put("saves", recipe.getSaves());
            data.put("averageRating", recipe.getAverageRating());
            data.put("shareable", recipe.isShareable());
            data.put("reviews", recipe.getReviews());

            List<Map<String, String>> ingredientsList = recipe.getIngredients().stream()
                    .map(ingredient -> {
                        Map<String, String> ingMap = new HashMap<>();
                        ingMap.put("name", ingredient.getName());
                        ingMap.put("measure", ingredient.getMeasure());
                        return ingMap;
                    })
                    .collect(Collectors.toList());
            data.put("ingredients", ingredientsList);

            recipesCollection.document(recipe.getRecipeId()).set(data).get();

            // 2. Update the "categories" collection
            String category = recipe.getCategory();
            if (category != null && !category.isEmpty()) {
                DocumentReference categoryDocRef = categoriesCollection.document(category);
                ApiFuture<Void> future = db.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(categoryDocRef).get();
                    if (snapshot.exists()) {
                        transaction.update(categoryDocRef, "recipes", FieldValue.arrayUnion(recipe.getRecipeId()));
                    } else {
                        Map<String, Object> catData = new HashMap<>();
                        catData.put("recipes", Collections.singletonList(recipe.getRecipeId()));
                        transaction.set(categoryDocRef, catData);
                    }
                    return null;
                });
                future.get(); // Wait for transaction to complete
            }

            return recipe;

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error saving recipe to Firebase: " + e.getMessage());
            throw new RuntimeException("Error saving recipe", e);
        }
    }

    public List<String> getAllCategories() {
        try {
            ApiFuture<QuerySnapshot> future = categoriesCollection.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream()
                    .map(QueryDocumentSnapshot::getId)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error fetching categories from Firebase: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Recipe> findByCategory(String category, String name) {
        try {
            DocumentReference categoryDocRef = categoriesCollection.document(category);
            ApiFuture<DocumentSnapshot> future = categoryDocRef.get();
            DocumentSnapshot document = future.get();

            if (!document.exists()) {
                return new ArrayList<>();
            }

            @SuppressWarnings("unchecked")
            List<String> recipeIds = (List<String>) document.get("recipes");
            if (recipeIds == null || recipeIds.isEmpty()) {
                return new ArrayList<>();
            }

            // Fetch all recipes by their IDs
            List<ApiFuture<DocumentSnapshot>> futures = new ArrayList<>();
            for (String id : recipeIds) {
                if (id != null && !id.isEmpty()) {
                    futures.add(recipesCollection.document(id).get());
                }
            }

            List<Recipe> recipes = new ArrayList<>();
            for (ApiFuture<DocumentSnapshot> f : futures) {
                DocumentSnapshot doc = f.get();
                if (doc.exists()) {
                    recipes.add(documentToRecipe(doc));
                }
            }

            Stream<Recipe> recipeStream = recipes.stream();

            // Filter by name if provided
            if (name != null && !name.isEmpty()) {
                recipeStream = recipeStream.filter(recipe ->
                        recipe.getTitle() != null &&
                        recipe.getTitle().toLowerCase().contains(name.toLowerCase())
                );
            }

            return recipeStream.collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error finding recipes by category: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Recipe findById(String recipeId) {
        try {
            DocumentReference docRef = recipesCollection.document(recipeId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (!document.exists()) {
                return null;
            }

            return documentToRecipe(document);

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error finding recipe by ID: " + e.getMessage());
            throw new RuntimeException("Error finding recipe", e);
        }
    }

    public List<Recipe> search(String name, String category) {
        try {
            Query query = recipesCollection.whereEqualTo("status", "PUBLISHED");

            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            Stream<Recipe> recipeStream = documents.stream().map(this::documentToRecipe);

            // Filter by name
            if (name != null && !name.isEmpty()) {
                recipeStream = recipeStream.filter(recipe ->
                        recipe.getTitle() != null &&
                        recipe.getTitle().toLowerCase().contains(name.toLowerCase())
                );
            }

            // Filter by category
            if (category != null && !category.isEmpty()) {
                recipeStream = recipeStream.filter(recipe ->
                        recipe.getCategory() != null &&
                        recipe.getCategory().equalsIgnoreCase(category)
                );
            }

            return recipeStream.collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error searching recipes in Firebase: " + e.getMessage());
            throw new RuntimeException("Error searching recipes", e);
        }
    }

    @Override
    public void recordView(String recipeId) {
        DocumentReference viewDocRef = recipeViewsCollection.document(recipeId);
        ApiFuture<Void> future = db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(viewDocRef).get();
            if (snapshot.exists()) {
                long newViews = snapshot.getLong("num_views") + 1;
                transaction.update(viewDocRef, "num_views", newViews);
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("num_views", 1);
                transaction.set(viewDocRef, data);
            }
            return null;
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error recording view", e);
        }
    }

    public CompletableFuture<Integer> getViewCount(String recipeId) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        DocumentReference docRef = recipeViewsCollection.document(recipeId);
        ApiFuture<DocumentSnapshot> apiFuture = docRef.get();
        apiFuture.addListener(() -> {
            try {
                DocumentSnapshot document = apiFuture.get();
                if (document.exists()) {
                    Long views = document.getLong("num_views");
                    future.complete(views != null ? views.intValue() : 0);
                } else {
                    future.complete(0);
                }
            } catch (InterruptedException | ExecutionException e) {
                future.completeExceptionally(e);
            }
        }, Runnable::run);
        return future;
    }

    /**
     * Asynchronously fetches the save count for a recipe from Firebase.
     */
    public CompletableFuture<Integer> getSaveCount(String recipeId) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        DocumentReference docRef = recipesCollection.document(recipeId);
        ApiFuture<DocumentSnapshot> apiFuture = docRef.get();
        apiFuture.addListener(() -> {
            try {
                DocumentSnapshot document = apiFuture.get();
                if (document.exists()) {
                    Long saves = document.getLong("saves");
                    future.complete(saves != null ? saves.intValue() : 0);
                } else {
                    future.complete(0);
                }
            } catch (InterruptedException | ExecutionException e) {
                future.completeExceptionally(e);
            }
        }, Runnable::run);
        return future;
    }

    /**
     * Asynchronously fetches the average rating for a recipe from Firebase.
     */
    public CompletableFuture<Double> getAverageRating(String recipeId) {
        CompletableFuture<Double> future = new CompletableFuture<>();
        DocumentReference docRef = recipesCollection.document(recipeId);
        ApiFuture<DocumentSnapshot> apiFuture = docRef.get();
        apiFuture.addListener(() -> {
            try {
                DocumentSnapshot document = apiFuture.get();
                if (document.exists()) {
                    Double rating = document.getDouble("averageRating");
                    future.complete(rating != null ? rating : 0.0);
                } else {
                    future.complete(0.0);
                }
            } catch (InterruptedException | ExecutionException e) {
                future.completeExceptionally(e);
            }
        }, Runnable::run);
        return future;
    }

    public List<Recipe> findByAuthor(String authorId) {
        try {
            ApiFuture<QuerySnapshot> future = recipesCollection
                    .whereEqualTo("authorId", authorId)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Recipe> recipes = new ArrayList<>();

            for (DocumentSnapshot doc : documents) {
                recipes.add(documentToRecipe(doc));
            }

            return recipes;

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error finding recipes by author: " + e.getMessage());
            throw new RuntimeException("Error finding recipes by author", e);
        }
    }

    public List<Recipe> findPublishedRecipes() {
        try {
            ApiFuture<QuerySnapshot> future = recipesCollection
                    .whereEqualTo("status", "PUBLISHED")
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Recipe> recipes = new ArrayList<>();

            for (DocumentSnapshot doc : documents) {
                recipes.add(documentToRecipe(doc));
            }

            return recipes;

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error finding published recipes: " + e.getMessage());
            throw new RuntimeException("Error finding published recipes", e);
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
        if (hashmap == null || hashmap.isEmpty()) {

        }
        else {
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
                System.err.println("Invalid status value: " + statusStr);
                // Handle error, maybe default to DRAFT
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

        Long views = doc.getLong("views");
        Long saves = doc.getLong("saves");
        Boolean shareable = doc.getBoolean("shareable");

        if (views != null) recipe.setViews(views.intValue());
        if (saves != null) recipe.setSaves(saves.intValue());
        if (shareable != null) recipe.setShareable(shareable);

        // Recalculate average rating based on reviews (if any)
        // This assumes reviews are fetched separately or handled elsewhere
        recipe.setReviews(reviews);
        recipe.recalculateAverageRating();

        return recipe;
    }

    public void deleteRecipe(String recipeId) {
        try {
            recipesCollection.document(recipeId).delete().get();
            System.out.println("Recipe deleted: " + recipeId);
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error deleting recipe: " + e.getMessage());
            throw new RuntimeException("Error deleting recipe", e);
        }
    }
}
