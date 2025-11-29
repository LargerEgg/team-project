package entity;

import java.util.Date;
import java.util.List;

public class RecipeDraft {
    private String draftId;
    private String authorId;
    private String title;
    private String description;
    private List<IngredientDTO> ingredients;
    private String category;
    private List<String> tags;
    private String imagePath;
    private Date lastSaved;

    public static class IngredientDTO {
        private String name;
        private String quantity;

        public IngredientDTO() {}

        public IngredientDTO(String name, String quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getQuantity() { return quantity; }
        public void setQuantity(String quantity) { this.quantity = quantity; }
    }

    public RecipeDraft() {}

    public RecipeDraft(String draftId, String authorId, String title, String description,
                       List<IngredientDTO> ingredients, String category, List<String> tags,
                       String imagePath, Date lastSaved) {
        this.draftId = draftId;
        this.authorId = authorId;
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.category = category;
        this.tags = tags;
        this.imagePath = imagePath;
        this.lastSaved = lastSaved;
    }

    public String getDraftId() { return draftId; }
    public void setDraftId(String draftId) { this.draftId = draftId; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<IngredientDTO> getIngredients() { return ingredients; }
    public void setIngredients(List<IngredientDTO> ingredients) { this.ingredients = ingredients; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Date getLastSaved() { return lastSaved; }
    public void setLastSaved(Date lastSaved) { this.lastSaved = lastSaved; }
}