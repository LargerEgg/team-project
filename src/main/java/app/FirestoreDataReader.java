package app;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.CollectionReference;
import com.google.firebase.FirebaseApp;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirestoreDataReader {

    public static void main(String[] args) {
        try {
            // Step 1: Initialize Firebase Admin SDK
            // Replace "path/to/your/serviceAccountKey.json" with the actual path
            // You can generate this JSON file from your Firebase project settings
            // (Project settings -> Service accounts -> Generate new private key)
            FileInputStream serviceAccount =
                    new FileInputStream("src/main/resources/recipe-app-29d1d-firebase-adminsdk-fbsvc-ad98bb22f1.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);

            // Step 2: Get a Firestore instance
            Firestore db = FirestoreClient.getFirestore();
            System.out.println("Firebase Admin SDK initialized and Firestore instance obtained!");

            // Step 3: Specify the collection and document you want to read
            // Let's assume you have a 'recipes' collection and a document named 'SpicyChickenCurry'
            DocumentReference docRef = db.collection("recipes").document("SpicyChickenCurry");

            // Step 4: Asynchronously retrieve the document
            // .get() returns an ApiFuture, which you can block on with .get() for simple examples,
            // or handle asynchronously using callbacks in a real application.
            DocumentSnapshot document = docRef.get().get(); // Blocks until data is retrieved

            if (document.exists()) {
                System.out.println("\n--- Document Data Found ---");
                System.out.println("Document ID: " + document.getId());

                // You can get the data as a Map
                Map<String, Object> data = document.getData();
                System.out.println("Data as Map: " + data);

                // Or map it to a custom Java object (POJO - Plain Old Java Object)
                // For this to work, your POJO (e.g., Recipe.class) needs
                // a no-argument constructor and getters/setters for its fields.
                // Example: Recipe recipe = document.toObject(Recipe.class);
                // System.out.println("Recipe Title: " + recipe.getTitle());

            } else {
                System.out.println("No such document!");
            }

            // Example of reading a whole collection (first 5 documents)
            System.out.println("\n--- Reading from 'recipes' Collection (first 5 documents) ---");
            CollectionReference recipesCollection = db.collection("recipes");
            recipesCollection.limit(5).get().get().getDocuments().forEach(d -> {
                System.out.println("  Recipe ID: " + d.getId() + ", Data: " + d.getData().get("title")); // Assuming a 'title' field
            });


        } catch (IOException e) {
            System.err.println("Error loading service account key: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error during Firestore operation: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // It's good practice to ensure the FirebaseApp is shut down if this is a short-lived process
            // For long-running servers, you typically wouldn't shut it down here.
            if (FirebaseApp.getApps() != null && !FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.getInstance().delete();
            }
        }
    }

}

// Example POJO for mapping Firestore documents (if you choose to use it)
class Recipe {
    private String title;
    private String description;
    // ... other fields

    public Recipe() {
        // No-argument constructor required for Firestore object mapping
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
// ... getters and setters for other fields
}
