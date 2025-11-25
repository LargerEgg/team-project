package data_access;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FirebaseInitializer {
    private static boolean initialized = false;
    private static Firestore firestore;

    public static synchronized void initialize() throws IOException {
        if (initialized) {
            return;
        }

        try {
            // Try to load from resources first (for packaged application)
            InputStream serviceAccount = FirebaseInitializer.class
                    .getClassLoader()
                    .getResourceAsStream("recipe-app-29d1d-firebase-adminsdk-fbsvc-ad98bb22f1.json");

            // Fallback to file system (for development)
            if (serviceAccount == null) {
                serviceAccount = new FileInputStream(
                        "src/main/resources/recipe-app-29d1d-firebase-adminsdk-fbsvc-ad98bb22f1.json"
                );
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            firestore = FirestoreClient.getFirestore();
            initialized = true;

            System.out.println("Firebase initialized successfully!");

        } catch (IOException e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
            throw e;
        }
    }

    public static Firestore getFirestore() {
        if (!initialized) {
            throw new IllegalStateException(
                    "Firebase has not been initialized. Call initialize() first."
            );
        }
        return firestore;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void shutdown() {
        if (initialized && FirebaseApp.getApps() != null && !FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.getInstance().delete();
            initialized = false;
            firestore = null;
            System.out.println("Firebase shut down successfully.");
        }
    }
}
