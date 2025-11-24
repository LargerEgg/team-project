package use_case.recommend_recipe;

public class RecommendRecipeInputData {
    final private String username;

    public RecommendRecipeInputData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}