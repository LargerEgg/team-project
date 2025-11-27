package app;

import data_access.FirebaseInitializer;

/**
 * Quick debug utility to check Firebase initialization status
 */
public class CheckFirebaseStatus {
    public static void main(String[] args) {
        System.out.println("=== Firebase Status Check ===");
        System.out.println("Is Firebase initialized? " + FirebaseInitializer.isInitialized());

        if (!FirebaseInitializer.isInitialized()) {
            System.out.println("\n❌ Firebase is NOT initialized!");
            System.out.println("Attempting to initialize...\n");

            try {
                FirebaseInitializer.initialize();
                System.out.println("✅ Firebase initialized successfully!");
            } catch (Exception e) {
                System.err.println("❌ Failed to initialize Firebase:");
                System.err.println("   " + e.getMessage());
                System.err.println("\nPossible causes:");
                System.err.println("1. Missing credentials file");
                System.err.println("2. Incorrect file path");
                System.err.println("3. Invalid JSON in credentials file");
            }
        } else {
            System.out.println("✅ Firebase is already initialized!");
        }

        System.out.println("\n=== End Status Check ===");
    }
}