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
        // This null check is a safeguard, as existsByName should prevent null here.
        if (user == null) {
            loginPresenter.prepareFailView("An unexpected error occurred: User data not found.");
            return;
        }

        if (!password.equals(user.getPassword())) {
            loginPresenter.prepareFailView("Incorrect password for " + username + ".");
        } else {
            userDataAccessObject.setCurrentUsername(username);
            LoginOutputData loginOutputData = new LoginOutputData(user.getUsername());
            loginPresenter.prepareSuccessView(loginOutputData);
        }
    }
}
