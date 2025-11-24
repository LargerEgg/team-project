package use_case.login;

import entity.User;

public class LoginInteractor implements LoginInputBoundary {
    private final LoginUserDataAccessInterface userDataAccessObject;
    private final LoginOutputBoundary loginPresenter;

    public LoginInteractor(LoginUserDataAccessInterface userDataAccessInterface,
                           LoginOutputBoundary loginOutputBoundary) {
        this.userDataAccessObject = userDataAccessInterface;
        this.loginPresenter = loginOutputBoundary;
    }

    @Override
    public void execute(LoginInputData loginInputData) {
        String username = loginInputData.getUsername();
        String password = loginInputData.getPassword();

        if (!userDataAccessObject.existsByName(username)) {
            loginPresenter.prepareFailView(username + ": Account does not exist.");
            return;
        }

        User user = userDataAccessObject.get(username);
        if (user == null) {
            // This case should ideally not be reached if existsByName is accurate, but it's good practice
            loginPresenter.prepareFailView("An error occurred: User not found.");
            return;
        }

//        final LoginOutputData loginOutputData = new LoginOutputData(user.getUsername());
//        loginPresenter.prepareSuccessView(loginOutputData);
        if (!password.equals(user.getPassword())) {
            loginPresenter.prepareFailView("Incorrect password for " + username + ".");
        } else {
            userDataAccessObject.setCurrentUsername(username);
            LoginOutputData loginOutputData = new LoginOutputData(user.getUsername());
            loginPresenter.prepareSuccessView(loginOutputData);
        }
    }
}
