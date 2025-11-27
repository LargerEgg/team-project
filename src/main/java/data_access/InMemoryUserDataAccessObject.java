package data_access;

import entity.User;
import use_case.login.LoginUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUserDataAccessObject implements SignupUserDataAccessInterface, LoginUserDataAccessInterface {

    private final Map<String, User> users = new HashMap<>();
    private String currentUsername;

    public InMemoryUserDataAccessObject() {
        System.out.println("🔸 InMemoryUserDataAccessObject created - data will not persist!");
    }

    @Override
    public boolean existsByName(String username) {
        boolean exists = users.containsKey(username);
        System.out.println("Checking if user exists: " + username + " -> " + exists);
        return exists;
    }

    @Override
    public void save(User user) {
        users.put(user.getUsername(), user);
        System.out.println("✅ User saved to memory: " + user.getUsername() + " (Total users: " + users.size() + ")");
    }

    @Override
    public User get(String username) {
        User user = users.get(username);
        System.out.println("Getting user: " + username + " -> " + (user != null ? "found" : "not found"));
        return user;
    }

    @Override
    public String getCurrentUsername() {
        return currentUsername;
    }

    @Override
    public void setCurrentUsername(String name) {
        this.currentUsername = name;
        System.out.println("Current username set to: " + name);
    }
}