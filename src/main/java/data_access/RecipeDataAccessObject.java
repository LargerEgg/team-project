package data_access;

import entity.Ingredient;
import entity.Recipe;
import use_case.recipe_search.RecipeSearchRecipeDataAccessInterface;
import use_case.view_recipe.ViewRecipeDataAccessInterface;

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

public class RecipeDataAccessObject implements RecipeSearchRecipeDataAccessInterface, ViewRecipeDataAccessInterface {

    private static final int SUCCESS_CODE = 200;
    private final OkHttpClient client = new OkHttpClient().newBuilder().build();

    // =================================================================================
    // PART 1: Categories + Search
    // =================================================================================

    @Override
    public List<String> getAllCategories() {
        Request request = new Request.Builder()
                .url("https://www.themealdb.com/api/json/v1/1/categories.php")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() != SUCCESS_CODE)
                throw new RuntimeException("API request failed");

            JSONObject json = new JSONObject(response.body().string());
            JSONArray arr = json.getJSONArray("categories");

            List<String> categories = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                categories.add(arr.getJSONObject(i).getString("strCategory"));
            }

            return categories;

        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Recipe> search(String name, String category) {
        if (category != null && !category.isEmpty()) {
            return searchByCategoryAndName(category, name);
        } else {
            return searchByName(name);
        }
    }

    private List<Recipe> searchByName(String name) {
        Request request = new Request.Builder()
                .url(String.format("https://www.themealdb.com/api/json/v1/1/search.php?s=%s", name))
                .build();

        return executeAndParse(request);
    }

    private List<Recipe> searchByCategoryAndName(String category, String name) {
        Request request = new Request.Builder()
                .url(String.format("https://www.themealdb.com/api/json/v1/1/filter.php?c=%s", category))
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.code() != SUCCESS_CODE)
                throw new RuntimeException("API request failed");

            JSONObject json = new JSONObject(response.body().string());
            if (json.isNull("meals")) {
                return new ArrayList<>();
            }

            JSONArray meals = json.getJSONArray("meals");
            List<JSONObject> filtered = new ArrayList<>();

            for (int i = 0; i < meals.length(); i++) {
                filtered.add(meals.getJSONObject(i));
            }

            // name filter
            if (name != null && !name.isEmpty()) {
                String lower = name.toLowerCase();
                filtered = filtered.stream()
                        .filter(m -> m.getString("strMeal").toLowerCase().contains(lower))
                        .collect(Collectors.toList());
            }

            // now lookup full detail via ID
            List<Recipe> recipes = new ArrayList<>();
            for (JSONObject meal : filtered) {
                List<Recipe> lookedUp = lookupById(meal.getString("idMeal"));
                if (!lookedUp.isEmpty()) {
                    recipes.add(lookedUp.get(0));
                }
            }

            return recipes;

        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // =================================================================================
    // PART 2: View Recipe Detail
    // =================================================================================

    private List<Recipe> lookupById(String id) {
        Request request = new Request.Builder()
                .url(String.format("https://www.themealdb.com/api/json/v1/1/lookup.php?i=%s", id))
                .build();

        return executeAndParse(request);
    }

    @Override
    public Recipe findById(String recipeId) {
        List<Recipe> list = lookupById(recipeId);
        if (!list.isEmpty()) return list.get(0);
        return null;
    }

    @Override
    public void recordView(String recipeId) {
        // no-op, since this DAO is read-only
    }

    public void save(Recipe recipe) {
        System.out.println("Recipe saved (not really, read-only DAO): " + recipe.getTitle());
    }

    // =================================================================================
    // PART 3: Core Fetch + Parse
    // =================================================================================

    private List<Recipe> executeAndParse(Request request) {
        try {
            Response response = client.newCall(request).execute();
            if (response.code() != SUCCESS_CODE)
                throw new RuntimeException("API request failed");

            JSONObject json = new JSONObject(response.body().string());
            if (json.isNull("meals")) {
                return new ArrayList<>();
            }

            JSONArray meals = json.getJSONArray("meals");
            List<Recipe> recipes = new ArrayList<>();

            for (int i = 0; i < meals.length(); i++) {
                recipes.add(parseRecipe(meals.getJSONObject(i)));
            }

            return recipes;

        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private Recipe parseRecipe(JSONObject recipeJson) {

        // --------- image ----------
        String imageUrl = recipeJson.optString("strMealThumb", null);
        BufferedImage image = null;
        if (imageUrl != null) {
            image = downloadImage(imageUrl);
        }

        // --------- ingredients ----------
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            String ing = recipeJson.optString("strIngredient" + i);
            String measure = recipeJson.optString("strMeasure" + i);

            if (ing != null && !ing.trim().isEmpty()) {
                ingredients.add(new Ingredient(ing.trim(), measure));
            }
        }

        // --------- tags ----------
        String tagStr = recipeJson.optString("strTags", "");
        List<String> tags = new ArrayList<>();
        if (!tagStr.isBlank()) {
            tags = Arrays.stream(tagStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        return new Recipe(
                recipeJson.getString("idMeal"),
                "N/A",
                recipeJson.getString("strMeal"),
                recipeJson.getString("strInstructions"),
                ingredients,
                recipeJson.getString("strCategory"),
                tags,
                Recipe.Status.PUBLISHED,
                new Date(),
                new Date(),
                imageUrl,
                image
        );
    }

    private BufferedImage downloadImage(String imageUrl) {
        try {
            return ImageIO.read(new URL(imageUrl));
        } catch (IOException e) {
            return null; // fail silently
        }
    }
}
