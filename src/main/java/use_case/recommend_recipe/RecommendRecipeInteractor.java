package use_case.recommend_recipe;

import entity.Recipe;
import entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendRecipeInteractor implements RecommendRecipeInputBoundary {

    final RecommendRecipeDataAccessInterface userDataAccessObject;
    final RecommendRecipeOutputBoundary userPresenter;

    public RecommendRecipeInteractor(RecommendRecipeDataAccessInterface userDataAccessInterface,
                                     RecommendRecipeOutputBoundary recommendRecipeOutputBoundary) {
        this.userDataAccessObject = userDataAccessInterface;
        this.userPresenter = recommendRecipeOutputBoundary;
    }

    @Override
    public void execute(RecommendRecipeInputData inputData) {
        String username = inputData.getUsername();

        User user = userDataAccessObject.getUser(username);
        if (user == null) {
            userPresenter.prepareFailView("User not found: " + username);
            return;
        }

        List<Recipe> favorites = user.getSavedRecipes();

        if (favorites == null || favorites.isEmpty()) {
            userPresenter.prepareFailView(
                    "No favorites found. Please save some recipes first!");
            return;
        }

        List<String> rankedCategories = getFavouriteCategoriesRanked(favorites);

        if (rankedCategories.isEmpty()) {
            userPresenter.prepareFailView(
                    "Could not determine favorite category.");
            return;
        }

        String bestCategory = rankedCategories.get(0);

        List<Recipe> recommendedRecipes = userDataAccessObject.getRecipesByCategory(bestCategory);

        if (recommendedRecipes.isEmpty()) {
            userPresenter.prepareFailView(
                    "Sorry, no recommendations found for category: " + bestCategory);
            return;
        }

        RecommendRecipeOutputData outputData =
                new RecommendRecipeOutputData(recommendedRecipes, bestCategory);
        userPresenter.prepareSuccessView(outputData);
    }

    private List<String> getFavouriteCategoriesRanked(List<Recipe> favorites) {
        if (favorites == null || favorites.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Integer> categoryCounts = new HashMap<>();
        for (Recipe recipe : favorites) {
            if (recipe == null) {
                continue;
            }

            String category;
            try {
                category = recipe.getCategory();
            } catch (Exception e) {
                continue;
            }

            if (category == null || category.trim().isEmpty()) {
                continue;
            }

            category = category.trim();
            categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
        }

        if (categoryCounts.isEmpty()) {
            return new ArrayList<>();
        }

        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(categoryCounts.entrySet());

        entryList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        List<String> rankedCategories = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : entryList) {
            rankedCategories.add(entry.getKey());
        }

        return rankedCategories;
    }
}
