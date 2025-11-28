package data_access;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import entity.Ingredient;
import use_case.post_recipe.PostRecipeDataAccessInterface;
import use_case.view_recipe.ViewRecipeDataAccessInterface;
import entity.Recipe;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


public class FirebaseRecipeDataAccessObject implements PostRecipeDataAccessInterface, ViewRecipeDataAccessInterface {
    private final Firestore db;
    private final CollectionReference recipesCollection;
    private final CollectionReference recipeViewsCollection;

    public FirebaseRecipeDataAccessObject() {
        this(FirebaseInitializer.getFirestore());
    }

    public FirebaseRecipeDataAccessObject(Firestore db) {
        this.db = db;
        this.recipesCollection = db.collection("recipes");
        this.recipeViewsCollection = db.collection("recipe_views");
    }

    @Override
    public Recipe saveRecipe(Recipe recipe) {
        try {
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
            data.put("views", recipe.getViews());
            data.put("saves", recipe.getSaves());
            data.put("averageRating", recipe.getAverageRating());
            data.put("shareable", recipe.isShareable());

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

            return recipe;

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error saving recipe to Firebase: " + e.getMessage());
            throw new RuntimeException("Error saving recipe", e);
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

        // Convert ingredients
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

        Recipe.Status status = Recipe.Status.valueOf(statusStr);

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

        // Set additional fields
        Long views = doc.getLong("views");
        Long saves = doc.getLong("saves");
        Double avgRating = doc.getDouble("averageRating");
        Boolean shareable = doc.getBoolean("shareable");

        if (views != null) recipe.setViews(views.intValue());
        if (saves != null) recipe.setSaves(saves.intValue());
        if (avgRating != null) recipe.recalculateAverageRating();
        if (shareable != null) recipe.setShareable(shareable);

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
