package data_access;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import entity.User;
import entity.UserFactory;
import use_case.login.LoginUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirebaseUserDataAccessObject implements LoginUserDataAccessInterface, SignupUserDataAccessInterface {

    private final UserFactory userFactory;
    private final Firestore db;
    private final CollectionReference usersCollection;

    private String currentUsername;

    public FirebaseUserDataAccessObject(UserFactory userFactory) {
        this.db = db;
        this.userFactory = userFactory;
        this.usersCollection = db.collection("users"); //IMPORTANT: Change name to whatever is chosen for collections
    }

    @Override
    public boolean existsByName(String username) {
        try {
            DocumentReference docref = usersCollection.document(username);

            ApiFuture<DocumentSnapshot> future =docref.get();

            DocumentSnapshot document = future.get();

            return document.exists();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error checking if username exists", e);
        }
    }

    @Override
    public User get(String name) {
        try {
            DocumentReference docref = usersCollection.document(name);

            ApiFuture<DocumentSnapshot> future = docref.get();
            DocumentSnapshot document = future.get();

            if (!document.exists()) {
                return null;
            }
            String username = document.getString("username");
            String password = document.getString("password");

            return userFactory.create(username, password);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error retrieving user", e);
        }
    }

    @Override
    public void setCurrentUsername(String name) {
        currentUsername = name;
    }

    @Override
    public String getCurrentUsername() {
        return currentUsername;
    }

    @Override
    public void save(User user) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("username", user.getName());
            data.put("password", user.getPassword());

            usersCollection.document(user.getName()).set(data).get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving user", e);
        }
    }
}
