package use_case.post_recipe;

import java.util.ArrayList;
import java.util.List;

public class PostRecipeInputData {

    // Version of ingredient entity for data transfer
    public static class IngredientDTO {
        private final  String name;
        private final double quantity;
        private final String unit;

        public IngredientDTO(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }

        public String getName() {
            return name;
        }

        public double getQuantity() {
            return quantity;
        }

        public String getUnit() {
            return unit;
        }
    }

    private final String authorId;
    private final String title;
    private final String description;
    private final List<IngredientDTO> ingredients;
    private final String category;
    private final List<String> tags;
    private final String imagePath;

    public PostRecipeInputData(String authorId,
                               String title,
                               String description,
                               List<IngredientDTO> ingredients,
                               String category,
                               String imagePath) {
        this.authorId = authorId;
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.category = category;
        this.imagePath = imagePath;
        this.tags = new ArrayList<>();
    }


    public String getAuthorId() {
        return authorId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<IngredientDTO> getIngredients() {
        return ingredients;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getImagePath() {
        return imagePath;
    }
}
