package data_access;

import entity.Recipe;

import entity.Review;
import entity.User;
import interface_adapter.edit_review.EditReviewDataAccessInterface;

import java.io.IOException;
import java.util.*;

import okhttp3.*;
        import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * The DAO for recipes from the API.
 */
public class ReviewDataAccessObject implements EditReviewDataAccessInterface {
    private static final int SUCCESS_CODE = 200;
    private static final String CONTENT_TYPE_LABEL = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String STATUS_CODE_LABEL = "status_code";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String MESSAGE = "message";

    private static final String API_KEY = "1";
    private static final String API_BASE_URL = "https://www.themealdb.com/api/json/v1/1/filter.php";
    private final OkHttpClient client = new OkHttpClient().newBuilder().build();


    /*
    public List<Recipe> search(String name) {
        // Make an API call to get the user object.
        final OkHttpClient client = new OkHttpClient().newBuilder().build();
        final Request request = new Request.Builder()
                .url(String.format("https://www.themealdb.com/api/json/v1/1/search.php?s=%s", name))
                .build();
        try {
            final Response response = client.newCall(request).execute();
            final String responseBody = response.body().string();
            final JSONObject responseJson = new JSONObject(responseBody);

            if (response.code() == SUCCESS_CODE) {
                final JSONArray recipesJSONArray = responseJson.getJSONArray("meals");
                List<Recipe> recipeList = new ArrayList<>();
                for (int i = 0; i < recipesJSONArray.length(); i++) {
                    JSONObject recipeJson = recipesJSONArray.getJSONObject(i);
                    Recipe recipe = new Recipe(
                            recipeJson.getString("idMeal"),
                            "N/A", // authorId is not available in the API response
                            recipeJson.getString("strMeal"),
                            recipeJson.getString("strInstructions"),
                            new ArrayList<>(), // Ingredients are not provided in a structured way
                            recipeJson.getString("strCategory"),
                            Arrays.asList(recipeJson.optString("strTags", "").split(",")),
                            Recipe.Status.PUBLISHED,
                            new Date(), // creationDate is not available
                            new Date(), // updateDate is not available
                            recipeJson.getString("strMealThumb")
                    );
                    recipeList.add(recipe);
                }
                return recipeList;
            } else {
                throw new RuntimeException("API request failed with code: " + response.code());
            }
        } catch (IOException | JSONException ex) {
            throw new RuntimeException(ex);
        }
    }
     */

    @Override
    public void changeReview(Review review) {

    }

    @Override
    public void save(Review review) {

    }

    @Override
    public boolean existsByAuthorRecipe(UUID recipeId, UUID authorId) {
        return false;
    }

    @Override
    public Review get(UUID reviewId) {
        return null;
    }

    public User findUserByUsername(String username){
        return null;
    }

    public Recipe findRecipeById(String recipeId){
        return null;
    };

}

