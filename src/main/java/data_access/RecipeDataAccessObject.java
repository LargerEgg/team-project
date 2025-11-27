package data_access;

import entity.Ingredient;
import entity.Recipe;
import entity.User;
import use_case.recipe_search.RecipeSearchOutputBoundary;
import use_case.recipe_search.RecipeSearchRecipeDataAccessInterface;
import use_case.recipe_search.RecipeSearchOutputData;
import use_case.view_recipe.ViewRecipeDataAccessInterface;
import use_case.recommend_recipe.RecommendRecipeDataAccessInterface;

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

public class RecipeDataAccessObject implements RecipeSearchRecipeDataAccessInterface, ViewRecipeDataAccessInterface, RecommendRecipeDataAccessInterface {
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
                List<Recipe> lookedUpRecipes = lookupById(mealId, null);
                if (!lookedUpRecipes.isEmpty()) {
                    recipes.add(lookedUpRecipes.get(0));
                    currentImageCount++;
                    if (presenter != null) {
                        presenter.prepareProgressView(new RecipeSearchOutputData(currentImageCount, totalImageCount));
                    }
                }
            }
            return recipes;
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Recipe> lookupById(String id, RecipeSearchOutputBoundary presenter) {
        Request request = new Request.Builder()
                .url(String.format("https://www.themealdb.com/api/json/v1/1/lookup.php?i=%s", id))
                .build();
        return executeAndParse(request, presenter);
    }

    @Override
    public Recipe findById(String recipeId) {
        List<Recipe> recipes = lookupById(recipeId, null);
        if (!recipes.isEmpty()) {
            return recipes.get(0);
        }
        return null;
    }

    @Override
    public void save(Recipe recipe) {
        System.out.println("Recipe saved (not really, this DAO is read-only): " + recipe.getTitle());
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
            int currentImageCount = 0;
            int totalImageCount = meals.length();

            for (int i = 0; i < meals.length(); i++) {
                recipes.add(parseRecipe(meals.getJSONObject(i), presenter, currentImageCount, totalImageCount));
                currentImageCount++;
            }
            return recipes;
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private Recipe parseRecipe(JSONObject recipeJson, RecipeSearchOutputBoundary presenter, int currentImageCount, int totalImageCount) {
        String imageUrl = recipeJson.getString("strMealThumb");
        BufferedImage image = downloadImage(imageUrl);

        if (presenter != null) {
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

        String tagsString = recipeJson.optString("strTags", "");
        List<String> tags = new ArrayList<>();
        if (!tagsString.trim().isEmpty()) {
            tags = Arrays.stream(tagsString.split(","))
                    .map(String::trim)
                    .filter(tag -> !tag.isEmpty())
                    .collect(Collectors.toList());
        }

        return new Recipe(
                recipeJson.getString("idMeal"), "N/A", recipeJson.getString("strMeal"),
                recipeJson.getString("strInstructions"), ingredients, recipeJson.getString("strCategory"),
                tags,
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

    @Override
    public User getUser(String username) {
        System.out.println("Generating mock user data for: " + username);

        Recipe mockRecipe = new Recipe(
                "52772", "TheMealDB", "Teriyaki Chicken Casserole", "A dummy recipe",
                new ArrayList<>(), "Chicken", new ArrayList<>(),
                Recipe.Status.PUBLISHED, new Date(), new Date(),
                "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg", null
        );

        User user = new User(username, "password123");

        if (user.getSavedRecipes() != null) {
            user.getSavedRecipes().add(mockRecipe);
        } else {
            System.out.println("Error: User favorites list is null. Please check User entity.");
        }

        return user;
    }

    @Override
    public List<Recipe> getRecipesByCategory(String category) {
        List<Recipe> recipes = new ArrayList<>();
        if (category == null || category.isEmpty()) return recipes;

        Request request = new Request.Builder()
                .url(String.format("https://www.themealdb.com/api/json/v1/1/filter.php?c=%s", category))
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.code() != SUCCESS_CODE) throw new RuntimeException("API request failed");

            JSONObject responseJson = new JSONObject(response.body().string());
            if (responseJson.isNull("meals")) {
                return recipes;
            }
            JSONArray meals = responseJson.getJSONArray("meals");

            for (int i = 0; i < meals.length(); i++) {
                JSONObject recipeJson = meals.getJSONObject(i);
                String title = recipeJson.getString("strMeal");
                String imageUrl = recipeJson.getString("strMealThumb");
                String id = recipeJson.getString("idMeal");

                Recipe recipe = new Recipe(
                        id, "TheMealDB", title, "Recommended from TheMealDB",
                        new ArrayList<>(), category, new ArrayList<>(),
                        Recipe.Status.PUBLISHED, new Date(), new Date(),
                        imageUrl, null
                );
                recipes.add(recipe);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return recipes;
    }
}