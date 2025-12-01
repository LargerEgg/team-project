package app;

import interface_adapter.view_recipe.ViewRecipeController; // Import ViewRecipeController
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                AppBuilder appBuilder = new AppBuilder();

                // 1. Build basic views (Login, Signup, etc.)
                appBuilder
                        .addLoginView()
                        .addSignupView()
                        .addViewRecipeView()
                        .addPostRecipeView()
                        .addEditReviewView(); // Assuming this exists based on your code

                // 2. Build Use Cases and get Controllers
                // Get the ViewRecipeController first as it's needed by other views
                ViewRecipeController viewRecipeController = appBuilder.addViewRecipeUseCase();

                // 3. Add Recipe Search View (This also initializes Recommend Controller internally)
                appBuilder.addRecipeSearchView(viewRecipeController);

                // 4. ★★★ Crucial Step: Add Recommend Recipe View ★★★
                // This connects the recommend view to the app
                appBuilder.addRecommendRecipeView(viewRecipeController);

                // 5. Add Post Recipe Use Case
                appBuilder.addPostRecipeUseCase();

                // 6. Add remaining Use Cases
                appBuilder
                        .addLoginUseCase()
                        .addSignupUseCase()
                        .addEditReviewUseCase(); // Assuming this exists

                // 7. Build and show the application
                JFrame application = appBuilder.build();

                application.pack();
                application.setSize(900, 700);
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