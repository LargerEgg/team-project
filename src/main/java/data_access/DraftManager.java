package data_access;

import entity.RecipeDraft;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DraftManager {
    private static final String DRAFTS_FILE = "recipe_drafts.json";
    private static DraftManager instance;

    private DraftManager() {}

    public static DraftManager getInstance() {
        if (instance == null) {
            instance = new DraftManager();
        }
        return instance;
    }

    public void saveDraft(RecipeDraft draft) {
        try {
            List<RecipeDraft> drafts = loadAllDrafts();

            // Remove existing draft with same ID if it exists
            drafts.removeIf(d -> d.getDraftId().equals(draft.getDraftId()));

            // Add the new/updated draft
            drafts.add(draft);

            // Save to file
            saveAllDrafts(drafts);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save draft: " + e.getMessage(), e);
        }
    }

    public List<RecipeDraft> loadAllDrafts() throws IOException {
        File file = new File(DRAFTS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        String content = new String(Files.readAllBytes(Paths.get(DRAFTS_FILE)));
        if (content.trim().isEmpty()) {
            return new ArrayList<>();
        }

        JSONArray jsonArray = new JSONArray(content);
        List<RecipeDraft> drafts = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            drafts.add(jsonToRecipeDraft(obj));
        }

        return drafts;
    }

    public List<RecipeDraft> loadDraftsForUser(String authorId) {
        try {
            List<RecipeDraft> allDrafts = loadAllDrafts();
            List<RecipeDraft> userDrafts = new ArrayList<>();

            for (RecipeDraft draft : allDrafts) {
                if (draft.getAuthorId().equals(authorId)) {
                    userDrafts.add(draft);
                }
            }

            return userDrafts;
        } catch (IOException e) {
            System.err.println("Failed to load drafts: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void deleteDraft(String draftId) {
        try {
            List<RecipeDraft> drafts = loadAllDrafts();
            drafts.removeIf(d -> d.getDraftId().equals(draftId));
            saveAllDrafts(drafts);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete draft: " + e.getMessage(), e);
        }
    }

    /**
     * Save all drafts to JSON file
     */
    private void saveAllDrafts(List<RecipeDraft> drafts) throws IOException {
        JSONArray jsonArray = new JSONArray();

        for (RecipeDraft draft : drafts) {
            jsonArray.put(recipeDraftToJson(draft));
        }

        try (FileWriter file = new FileWriter(DRAFTS_FILE)) {
            file.write(jsonArray.toString(2)); // Pretty print with indent of 2
        }
    }

    private JSONObject recipeDraftToJson(RecipeDraft draft) {
        JSONObject obj = new JSONObject();
        obj.put("draftId", draft.getDraftId());
        obj.put("authorId", draft.getAuthorId());
        obj.put("title", draft.getTitle());
        obj.put("description", draft.getDescription());
        obj.put("category", draft.getCategory());
        obj.put("imagePath", draft.getImagePath());
        obj.put("lastSaved", draft.getLastSaved().getTime());

        // Convert tags
        JSONArray tagsArray = new JSONArray();
        if (draft.getTags() != null) {
            for (String tag : draft.getTags()) {
                tagsArray.put(tag);
            }
        }
        obj.put("tags", tagsArray);

        // Convert ingredients
        JSONArray ingredientsArray = new JSONArray();
        if (draft.getIngredients() != null) {
            for (RecipeDraft.IngredientDTO ingredient : draft.getIngredients()) {
                JSONObject ingObj = new JSONObject();
                ingObj.put("name", ingredient.getName());
                ingObj.put("quantity", ingredient.getQuantity());
                ingredientsArray.put(ingObj);
            }
        }
        obj.put("ingredients", ingredientsArray);

        return obj;
    }

    private RecipeDraft jsonToRecipeDraft(JSONObject obj) {
        RecipeDraft draft = new RecipeDraft();
        draft.setDraftId(obj.getString("draftId"));
        draft.setAuthorId(obj.getString("authorId"));
        draft.setTitle(obj.getString("title"));
        draft.setDescription(obj.getString("description"));
        draft.setCategory(obj.getString("category"));
        draft.setImagePath(obj.getString("imagePath"));
        draft.setLastSaved(new Date(obj.getLong("lastSaved")));

        // Parse tags
        JSONArray tagsArray = obj.getJSONArray("tags");
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < tagsArray.length(); i++) {
            tags.add(tagsArray.getString(i));
        }
        draft.setTags(tags);

        // Parse ingredients
        JSONArray ingredientsArray = obj.getJSONArray("ingredients");
        List<RecipeDraft.IngredientDTO> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientsArray.length(); i++) {
            JSONObject ingObj = ingredientsArray.getJSONObject(i);
            RecipeDraft.IngredientDTO ingredient = new RecipeDraft.IngredientDTO(
                    ingObj.getString("name"),
                    ingObj.getString("quantity")
            );
            ingredients.add(ingredient);
        }
        draft.setIngredients(ingredients);

        return draft;
    }
}