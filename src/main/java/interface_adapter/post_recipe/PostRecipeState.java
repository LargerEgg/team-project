package interface_adapter.post_recipe;

import use_case.post_recipe.PostRecipeInputData;

import java.util.ArrayList;
import java.util.List;

public class PostRecipeState {
    private String authorId = "";
    private String title = "";
    private String description = "";
    private List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
    private String category = "";
    private List<String> tags = new ArrayList<>();
    private String imagePath = "";

    private String errorMessage = "";
    private String successMessage = "";

    // Getters and Setters

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PostRecipeInputData.IngredientDTO> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<PostRecipeInputData.IngredientDTO> ingredients) {
        this.ingredients = ingredients;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
}