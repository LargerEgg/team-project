package interface_adapter.save_recipe;

public class SaveRecipeState {
    private boolean isSaved;
    private String message;

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
