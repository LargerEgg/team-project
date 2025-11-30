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

        // 使用 getSavedRecipes() 获取收藏
        List<Recipe> favorites = user.getSavedRecipes();

        if (favorites == null || favorites.isEmpty()) {
            userPresenter.prepareFailView("No favorites found. Please save some recipes first!");
            return;
        }

        // 1. 获取分类排名列表
        List<String> rankedCategories = getFavouriteCategoriesRanked(favorites);

        if (rankedCategories.isEmpty()) {
            userPresenter.prepareFailView("Could not determine favorite category.");
            return;
        }

        // 2. 改进逻辑：获取前 3 名分类（如果不足 3 个则全取）
        int categoriesToFetch = Math.min(3, rankedCategories.size());
        List<Recipe> finalRecommendations = new ArrayList<>();

        // 3. 循环抓取每个分类的食谱
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

        // 4. 随机打乱列表，实现“混合”效果
        Collections.shuffle(finalRecommendations);

        // 可选：限制推荐总数（比如只展示随机后的前 20 个，防止列表过长）
        if (finalRecommendations.size() > 20) {
            finalRecommendations = finalRecommendations.subList(0, 20);
        }

        // 5. 返回结果
        // 这里的 categoryName 可以改成一个通用的标题，比如 "Mix of your favorites"
        RecommendRecipeOutputData outputData = new RecommendRecipeOutputData(finalRecommendations, "Mix of your Top Favorites");
        userPresenter.prepareSuccessView(outputData);
    }

    private List<String> getFavouriteCategoriesRanked(List<Recipe> favorites) {
        if (favorites == null || favorites.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Integer> categoryCounts = new HashMap<>();
        for (Recipe recipe : favorites) {
            if (recipe == null) continue;
            String category = recipe.getCategory();
            if (category != null && !category.trim().isEmpty()) {
                categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(categoryCounts.entrySet());

        // 按收藏数量降序排列
        entryList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        List<String> rankedCategories = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : entryList) {
            rankedCategories.add(entry.getKey());
        }

        return rankedCategories;
    }
}