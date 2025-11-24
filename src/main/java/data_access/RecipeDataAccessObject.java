package data_access;

import entity.Ingredient;
import entity.Recipe;
import use_case.recipe_search.RecipeSearchRecipeDataAccessInterface;

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

public class RecipeDataAccessObject implements RecipeSearchRecipeDataAccessInterface {
    private static final int SUCCESS_CODE = 200;
    private final OkHttpClient client = new OkHttpClient().newBuilder().build();

    @Override
    public List<Recipe> search(String name, String category) {
        if (category != null && !category.isEmpty()) {
            return searchByCategoryAndName(category, name);
        } else {
            return searchByName(name);
        }
    }

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
            for (JSONObject meal : filteredMeals) {
                String mealId = meal.getString("idMeal");
                recipes.addAll(lookupById(mealId));
            }
            return recipes;
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Recipe> lookupById(String id) {
        Request request = new Request.Builder()
                .url(String.format("https://www.themealdb.com/api/json/v1/1/lookup.php?i=%s", id))
                .build();
        return executeAndParse(request);
    }

    private List<Recipe> executeAndParse(Request request) {
        try {
            Response response = client.newCall(request).execute();
            if (response.code() != SUCCESS_CODE) throw new RuntimeException("API request failed");

            JSONObject responseJson = new JSONObject(response.body().string());
            if (responseJson.isNull("meals")) {
                return new ArrayList<>();
            }
            JSONArray meals = responseJson.getJSONArray("meals");
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
        String imageUrl = recipeJson.getString("strMealThumb");
        BufferedImage image = downloadImage(imageUrl);

        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            String ingredientName = recipeJson.optString("strIngredient" + i);
            String measure = recipeJson.optString("strMeasure" + i);
            if (ingredientName != null && !ingredientName.isEmpty()) {
                ingredients.add(new Ingredient(ingredientName, measure));
            }
        }

        return new Recipe(
                recipeJson.getString("idMeal"), "N/A", recipeJson.getString("strMeal"),
                recipeJson.getString("strInstructions"), ingredients, recipeJson.getString("strCategory"),
                Arrays.asList(recipeJson.optString("strTags", "").split(",")),
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
