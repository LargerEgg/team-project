package data_access;

import entity.User;
import use_case.login.LoginUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

public class UserDataAccessObject implements SignupUserDataAccessInterface, LoginUserDataAccessInterface {
    // TODO implement all over-rided methods
    @Override
    public boolean existsByName(String username) {
        return true;
    }

    @Override
    public void save(User user) {
        return;
    }

    @Override
    public User get(String username) {
        return null;
    }

    @Override
    public String getCurrentUsername() {
        return "";
    }

    @Override
    public void setCurrentUsername(String name) {

    }
}
