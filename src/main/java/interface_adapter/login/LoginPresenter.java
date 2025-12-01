package interface_adapter.login;

import interface_adapter.ViewManagerModel;
import interface_adapter.edit_review.EditReviewState;
import interface_adapter.edit_review.EditReviewViewModel;
import interface_adapter.recipe_search.RecipeSearchState;
import interface_adapter.recipe_search.RecipeSearchViewModel;
import use_case.login.LoginOutputBoundary;
import use_case.login.LoginOutputData;

import javax.swing.*; // Import JOptionPane

public class LoginPresenter implements LoginOutputBoundary {

    private final LoginViewModel loginViewModel;
    private final RecipeSearchViewModel recipeSearchViewModel;
    private final ViewManagerModel viewManagerModel;
    private final EditReviewViewModel editReviewViewModel;

    public LoginPresenter(ViewManagerModel viewManagerModel,
                          RecipeSearchViewModel recipeSearchViewModel,
                          LoginViewModel loginViewModel,
                          EditReviewViewModel editReviewViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.recipeSearchViewModel = recipeSearchViewModel;
        this.loginViewModel = loginViewModel;
        this.editReviewViewModel = editReviewViewModel;
    }

    @Override
    public void prepareSuccessView(LoginOutputData response) {
        // On success, update the loggedInViewModel's state
        final RecipeSearchState loggedInState = recipeSearchViewModel.getState();
        loggedInState.setCurrentUser(response.getUsername());
        this.recipeSearchViewModel.firePropertyChange();

        final EditReviewState loggedInReviewState = editReviewViewModel.getState();
        loggedInReviewState.setCurrentUser(response.getUsername());
        this.editReviewViewModel.firePropertyChange();

        // and clear everything from the LoginViewModel's state
        loginViewModel.setState(new LoginState());

        // switch to the logged in view
        this.viewManagerModel.setState(recipeSearchViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        final LoginState loginState = loginViewModel.getState();
        loginState.setLoginError(error);
        loginViewModel.firePropertyChange();

        // Display a popup message for the error
        JOptionPane.showMessageDialog(null, error, "Login Error", JOptionPane.ERROR_MESSAGE);
    }
}
