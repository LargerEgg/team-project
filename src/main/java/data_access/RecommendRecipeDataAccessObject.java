package data_access;

import entity.Recipe;
import entity.Ingredient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for fetching recipe recommendations from MealDB API.
 * This class only handles API calls for getting recipes by category
 * For getting user's saved recipes, use CompositeRecommendRecipeDAO which
 * combines this with FirebaseSaveRecipeDataAccessObject.
 */
public class RecommendRecipeDataAccessObject {

    private static final String API_BASE_URL = "https://www.themealdb.com/api/json/v1/1/filter.php";

    /**
     * Get recipes by category from MealDB API.
     *
     * @param category the category name
     * @return a list of recipes matching the category
     */
    public List<Recipe> getRecipesByCategory(String category) {
        List<Recipe> recipes = new ArrayList<>();

        if (category == null || category.isEmpty()) {
            return recipes;
        }

        try {
            String encodedCategory = URLEncoder.encode(category, StandardCharsets.UTF_8);
            String urlString = String.format("%s?c=%s", API_BASE_URL, encodedCategory);

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                parseResponseToRecipes(response.toString(), recipes, category);

            } else {
                System.out.println("API Request failed. Response Code: " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return recipes;
    }

    private void parseResponseToRecipes(String jsonResponse, List<Recipe> recipes, String category) {
        JSONObject jsonObject = new JSONObject(jsonResponse);

        if (jsonObject.isNull("meals")) {
            return;
        }

        JSONArray results = jsonObject.getJSONArray("meals");

        for (int i = 0; i < results.length(); i++) {
            JSONObject recipeJson = results.getJSONObject(i);

            String title = recipeJson.getString("strMeal");
            String imageUrl = recipeJson.getString("strMealThumb");
            String id = recipeJson.getString("idMeal");

            String defaultAuthorId = "TheMealDB";
            String defaultDescription = "Recommended recipe from TheMealDB";
            List<Ingredient> emptyIngredients = new ArrayList<>();
            List<String> emptyTags = new ArrayList<>();
            Date now = new Date();

            Recipe recipe = new Recipe(
                    id,
                    defaultAuthorId,
                    title,
                    defaultDescription,
                    emptyIngredients,
                    category,
                    emptyTags,
                    Recipe.Status.PUBLISHED,
                    now,
                    now,
                    imageUrl
            );

            recipes.add(recipe);
        }
    }
}