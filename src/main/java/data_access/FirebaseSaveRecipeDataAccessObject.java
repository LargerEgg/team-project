package data_access;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import entity.Recipe;
import use_case.save_recipe.SaveRecipeDataAccessInterface;
import use_case.saved_recipes.ShowSavedRecipesDataAccessInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirebaseSaveRecipeDataAccessObject implements SaveRecipeDataAccessInterface, ShowSavedRecipesDataAccessInterface {

    private final Firestore db;

    public FirebaseSaveRecipeDataAccessObject(Firestore db) {
        this.db = db;
    }

    private CollectionReference savedRecipesCollectionForUser(String username) {
        return db.collection("users") //IMPORTANT: CHANGE TO WHATEVER THE NAME IS CHOSEN TO BE
                .document(username)
                .collection("savedRecipes"); //IMPORTANT: CHANGE TO WHATEVER THE NAME IS CHOSEN TO BE
    }

    @Override
    public boolean isRecipeSaved(String username, Recipe recipe) {
        try {
            DocumentSnapshot doc = savedRecipesCollectionForUser(username)
                    .document(recipe.getRecipeId())
                    .get()
                    .get();

            return doc.exists();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error checking saved recipe", e);
        }

    }


    @Override
    public void saveRecipe(String username, Recipe recipe) {

        try {
            CollectionReference colRef = savedRecipesCollectionForUser(username);

            Map<String, Object> map = new HashMap<>();

            map.put("recipe_id", recipe.getRecipeId());
            map.put("title", recipe.getTitle());
            map.put("description", recipe.getDescription()); //IMPORTANT: CHANGE THESE TO WHATEVER FIELDS NEED TO BE SHOWN ACCORUDNG TO TEAM

            String recipeID = recipe.getRecipeId();
            colRef.document(recipeID).set(map).get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving recipe", e);
        }

    }

    @Override
    public List<Recipe> getRecipes(String username) {
        try {
            CollectionReference colRef = savedRecipesCollectionForUser(username);
            ApiFuture<QuerySnapshot> future = colRef.get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();

            List<Recipe> recipes = new ArrayList<>();

            for (DocumentSnapshot doc : docs) {
                String id = doc.getString("recipe_id"); //IMPORTANT: CHANGE TO WHATEVER THE NAME ACVTUALLY IS SET
                String title = doc.getString("title"); // SAME AS ABOVE
                String description = doc.getString("description"); //SAME AS ABOVE

                Recipe recipe = new Recipe(id, ' ', title, description, ' ', ' ', ' ', ' ', ' ', ' ', ' '); //NOTE: CREATE RECIPE FACTORY TO FIX THIS

                recipes.add(recipe);
            }
            return recipes;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error getting recipes", e);
        }

    }

    @Override
    public void unsaveRecipe(String username, Recipe recipe) {
        try {
            CollectionReference colRef = savedRecipesCollectionForUser(username);
            colRef.document(recipe.getRecipeId()).delete().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error removing recipe", e);
        }
    }
}
