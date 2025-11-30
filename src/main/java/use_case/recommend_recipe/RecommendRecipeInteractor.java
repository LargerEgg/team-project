package use_case.recommend_recipe;

import entity.Recipe;
import entity.User;

import java.util.*;

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
            userPresenter.prepareFailView("No favorites found. Please save some recipes first!");
            return;
        }

        List<String> rankedCategories = getFavouriteCategoriesRanked(favorites);

        if (rankedCategories.isEmpty()) {
            userPresenter.prepareFailView("Could not determine favorite category.");
            return;
        }

        int categoriesToFetch = Math.min(3, rankedCategories.size());
        List<Recipe> finalRecommendations = new ArrayList<>();

        for (int i = 0; i < categoriesToFetch; i++) {
            String category = rankedCategories.get(i);
            List<Recipe> recipes = userDataAccessObject.getRecipesByCategory(category);
            if (recipes != null) {
                finalRecommendations.addAll(recipes);
            }
        }

        if (finalRecommendations.isEmpty()) {
            userPresenter.prepareFailView("Sorry, no recommendations found.");
            return;
        }

        Collections.shuffle(finalRecommendations);

        if (finalRecommendations.size() > 20) {
            finalRecommendations = finalRecommendations.subList(0, 20);
        }

        RecommendRecipeOutputData outputData = new RecommendRecipeOutputData(finalRecommendations, "Mix of your Top Favorites");
        userPresenter.prepareSuccessView(outputData);
    }

    private List<String> getFavouriteCategoriesRanked(List<Recipe> favorites) {
        // [修复] 移除了 favorites == null || favorites.isEmpty() 的检查 (由 execute 保证)
        // [修复] 移除了 recipe == null 的检查 (由 User 类保证)

        Map<String, Integer> categoryCounts = new HashMap<>();
        for (Recipe recipe : favorites) {
            String category = recipe.getCategory();
            // 这里的判断现在很干净：只检查 category 是否有效
            if (category != null && !category.trim().isEmpty()) {
                categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
            }
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