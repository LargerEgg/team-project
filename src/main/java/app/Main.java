package app;

import interface_adapter.view_recipe.ViewRecipeController; // Import ViewRecipeController
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                AppBuilder appBuilder = new AppBuilder();

                // Build the views and use cases
                appBuilder
                        .addLoginView()
                        .addSignupView()
                        .addViewRecipeView()
                        .addPostRecipeView()
                        .addEditReviewView();

                // Get the ViewRecipeController after its use case is built
                ViewRecipeController viewRecipeController = appBuilder.addViewRecipeUseCase();

                // Now add the RecipeSearchView, passing the initialized ViewRecipeController
                appBuilder.addRecipeSearchView(viewRecipeController);

                // Add the PostRecipeUseCase
                appBuilder.addPostRecipeUseCase();

                appBuilder
                        .addLoginUseCase()
                        .addSignupUseCase();

                JFrame application = appBuilder.build();

                application.pack();
                application.setSize(900, 700); // Set a reasonable default size
                application.setLocationRelativeTo(null);
                application.setVisible(true);

                System.out.println("Application started successfully!");

            } catch (Exception e) {
                System.err.println("Failed to start application: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to start application: " + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
