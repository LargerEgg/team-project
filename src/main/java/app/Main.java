package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("main running");

        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addRecipeSearchView()
                .addLoginView(null) //Change later when Firestore working
                .addSignupView(null) // same as above
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);

        System.out.println("GUI visible");

    }
}
