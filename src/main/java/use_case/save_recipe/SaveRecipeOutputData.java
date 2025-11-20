package use_case.save_recipe;

public class SaveRecipeOutputData {
    private final boolean success;
    private final String message;

    public SaveRecipeOutputData(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
