package app;

import interface_adapter.view_recipe.ViewRecipeController; // Import ViewRecipeController
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();

        // Build the views and use cases
        appBuilder
                .addLoginView()
                .addSignupView()
                .addViewRecipeView();

        // Get the ViewRecipeController after its use case is built
        ViewRecipeController viewRecipeController = appBuilder.addViewRecipeUseCase();

        // Now add the RecipeSearchView, passing the initialized ViewRecipeController
        appBuilder.addRecipeSearchView(viewRecipeController);

        appBuilder
                .addLoginUseCase()
                .addSignupUseCase();

        JFrame application = appBuilder.build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
