package app;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("main running");

        FirestoreOptions options = FirestoreOptions.newBuilder()
                .setProjectId("recipe-app-29d1d")   // your real project ID
                .build();

        Firestore db = options.getService();

        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addRecipeSearchView()
                .addPostRecipeView(db)
                .addLoginView(db) //Change later when Firestore working
                .addSignupView(db) // same as above
                .addLoginUseCase()
                .addSignupUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);

        System.out.println("GUI visible");

    }
}
