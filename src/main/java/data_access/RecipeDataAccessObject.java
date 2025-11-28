package data_access;

import entity.Ingredient;
import entity.Recipe;
import use_case.recipe_search.RecipeSearchOutputBoundary;
import use_case.recipe_search.RecipeSearchRecipeDataAccessInterface;
import use_case.recipe_search.RecipeSearchOutputData;
import use_case.view_recipe.ViewRecipeDataAccessInterface; // Import the new interface

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeDataAccessObject implements RecipeSearchRecipeDataAccessInterface, ViewRecipeDataAccessInterface { // Implement ViewRecipeDataAccessInterface
    private static final int SUCCESS_CODE = 200;
    private final OkHttpClient client = new OkHttpClient().newBuilder().build();

    @Override
    public List<String> getAllCategories() {
        Request request = new Request.Builder()
                .url("https://www.themealdb.com/api/json/v1/1/categories.php")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() != SUCCESS_CODE) throw new RuntimeException("API request failed");

            JSONObject responseJson = new JSONObject(response.body().string());
            JSONArray categoriesArray = responseJson.getJSONArray("categories");
            List<String> categories = new ArrayList<>();
            for (int i = 0; i < categoriesArray.length(); i++) {
                categories.add(categoriesArray.getJSONObject(i).getString("strCategory"));
            }
            return categories;
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Recipe> search(String name, String category, RecipeSearchOutputBoundary presenter) {
        if (category != null && !category.isEmpty()) {
            return searchByCategoryAndName(category, name, presenter);
        } else {
            return searchByName(name, presenter);
        }
    }

    private List<Recipe> searchByName(String name, RecipeSearchOutputBoundary presenter) {
        Request request = new Request.Builder()
                .url(String.format("https://www.themealdb.com/api/json/v1/1/search.php?s=%s", name))
                .build();
        return executeAndParse(request, presenter);
    }

    private List<Recipe> searchByCategoryAndName(String category, String name, RecipeSearchOutputBoundary presenter) {
        Request request = new Request.Builder()
                .url(String.format("https://www.themealdb.com/api/json/v1/1/filter.php?c=%s", category))
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() != SUCCESS_CODE) throw new RuntimeException("API request failed");

            JSONObject responseJson = new JSONObject(response.body().string());
            if (responseJson.isNull("meals")) {
                return new ArrayList<>();
            }
            JSONArray meals = responseJson.getJSONArray("meals");
            List<JSONObject> filteredMeals = new ArrayList<>();
            for (int i = 0; i < meals.length(); i++) {
                filteredMeals.add(meals.getJSONObject(i));
            }

            if (name != null && !name.isEmpty()) {
                String lowerCaseName = name.toLowerCase();
                filteredMeals = filteredMeals.stream()
                        .filter(meal -> meal.getString("strMeal").toLowerCase().contains(lowerCaseName))
                        .collect(Collectors.toList());
            }

            List<Recipe> recipes = new ArrayList<>();
            int currentImageCount = 0;
            int totalImageCount = filteredMeals.size();

            for (JSONObject meal : filteredMeals) {
                String mealId = meal.getString("idMeal");
                // For search, we pass a dummy presenter to lookupById as it's not directly tied to search progress
                List<Recipe> lookedUpRecipes = lookupById(mealId, null);
                if (!lookedUpRecipes.isEmpty()) {
                    recipes.add(lookedUpRecipes.get(0)); // lookupById returns a list, but we expect one recipe
                    currentImageCount++;
                    if (presenter != null) { // Only update progress if a presenter is provided
                        presenter.prepareProgressView(new RecipeSearchOutputData(currentImageCount, totalImageCount));
                    }
                }
            }
            return recipes;
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // Modified lookupById to be public and return a single Recipe for ViewRecipe use case
    private List<Recipe> lookupById(String id, RecipeSearchOutputBoundary presenter) {
        Request request = new Request.Builder()
                .url(String.format("https://www.themealdb.com/api/json/v1/1/lookup.php?i=%s", id))
                .build();
        return executeAndParse(request, presenter);
    }

    @Override
    public Recipe findById(String recipeId) {
        List<Recipe> recipes = lookupById(recipeId, null); // Pass null for presenter as it's not needed here
        if (!recipes.isEmpty()) {
            return recipes.get(0);
        }
        return null;
    }

    @Override
    public void recordView(String recipeId) {
        // This DAO is read-only.
    }

    private List<Recipe> executeAndParse(Request request, RecipeSearchOutputBoundary presenter) {
        try {
            Response response = client.newCall(request).execute();
            if (response.code() != SUCCESS_CODE) throw new RuntimeException("API request failed");

            JSONObject responseJson = new JSONObject(response.body().string());
            if (responseJson.isNull("meals")) {
                return new ArrayList<>();
            }
            JSONArray meals = responseJson.getJSONArray("meals");
            List<Recipe> recipes = new ArrayList<>();
            int currentImageCount = 0; // This will be updated by downloadImage
            int totalImageCount = meals.length();

            for (int i = 0; i < meals.length(); i++) {
                recipes.add(parseRecipe(meals.getJSONObject(i), presenter, currentImageCount, totalImageCount));
                currentImageCount++; // Increment after parsing and downloading image
            }
            return recipes;
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private Recipe parseRecipe(JSONObject recipeJson, RecipeSearchOutputBoundary presenter, int currentImageCount, int totalImageCount) {
        String imageUrl = recipeJson.getString("strMealThumb");
        BufferedImage image = downloadImage(imageUrl); // Image download happens here

        // Report progress after image download
        if (presenter != null) { // Only update progress if a presenter is provided
            presenter.prepareProgressView(new RecipeSearchOutputData(currentImageCount + 1, totalImageCount));
        }

        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            String ingredientName = recipeJson.optString("strIngredient" + i);
            String measure = recipeJson.optString("strMeasure" + i);
            if (ingredientName != null && !ingredientName.isEmpty()) {
                ingredients.add(new Ingredient(ingredientName, measure));
            }
        }

        // Handle tags: filter out empty or whitespace-only tags
        String tagsString = recipeJson.optString("strTags", "");
        List<String> tags = new ArrayList<>();
        if (!tagsString.trim().isEmpty()) { // Check if the whole string is not empty or just whitespace
            tags = Arrays.stream(tagsString.split(","))
                    .map(String::trim) // Trim each tag
                    .filter(tag -> !tag.isEmpty()) // Filter out empty strings
                    .collect(Collectors.toList());
        }

        return new Recipe(
                recipeJson.getString("idMeal"), "N/A", recipeJson.getString("strMeal"),
                recipeJson.getString("strInstructions"), ingredients, recipeJson.getString("strCategory"),
                tags, // Use the filtered tags list
                Recipe.Status.PUBLISHED, new Date(), new Date(), imageUrl, image
        );
    }

    private BufferedImage downloadImage(String imageUrl) {
        try {
            return ImageIO.read(new URL(imageUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
