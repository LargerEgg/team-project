package use_case.saved_recipes;

public class ShowSavedRecipesInputData {
    private final String username;

    public ShowSavedRecipesInputData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
