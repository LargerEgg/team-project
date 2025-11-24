package app;

import com.google.cloud.firestore.Firestore;
import data_access.InMemoryUserDataAccessObject;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.signup.SignupUserDataAccessInterface;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import view.SignupView;
import entity.UserFactory;
import data_access.FirebaseUserDataAccessObject;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.login.LoginUserDataAccessInterface;
import view.LoginView;
import data_access.RecipeDataAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.recipe_search.RecipeSearchController;
import interface_adapter.recipe_search.RecipeSearchPresenter;
import interface_adapter.recipe_search.RecipeSearchViewModel;
import use_case.recipe_search.RecipeSearchInputBoundary;
import use_case.recipe_search.RecipeSearchInteractor;
import use_case.recipe_search.RecipeSearchOutputBoundary;
import use_case.recipe_search.RecipeSearchRecipeDataAccessInterface;
import view.RecipeSearchView;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    private RecipeSearchViewModel recipeSearchViewModel;
    private RecipeSearchView recipeSearchView;

    private LoginViewModel loginViewModel;
    private LoginView loginView;

    private SignupViewModel signupViewModel;
    private SignupView signupView;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addRecipeSearchView() {
        recipeSearchViewModel = new RecipeSearchViewModel();
        RecipeSearchOutputBoundary recipeSearchOutputBoundary = new RecipeSearchPresenter(viewManagerModel, recipeSearchViewModel);
        RecipeSearchRecipeDataAccessInterface recipeSearchRecipeDataAccessInterface = new RecipeDataAccessObject();
        RecipeSearchInputBoundary recipeSearchInteractor = new RecipeSearchInteractor(recipeSearchRecipeDataAccessInterface, recipeSearchOutputBoundary);
        RecipeSearchController recipeSearchController = new RecipeSearchController(recipeSearchInteractor);
        recipeSearchView = new RecipeSearchView(recipeSearchViewModel, recipeSearchController);
        cardPanel.add(recipeSearchView, recipeSearchView.viewName);
        return this;
    }

    public AppBuilder addLoginView(Firestore db) {

        UserFactory userFactory = new UserFactory();
        loginViewModel = new LoginViewModel();

        LoginUserDataAccessInterface userDao =
                new InMemoryUserDataAccessObject(userFactory);

        LoginOutputBoundary loginPresenter =
                new LoginPresenter(viewManagerModel, recipeSearchViewModel, loginViewModel);

        LoginInputBoundary loginInteractor =
                new LoginInteractor(userDao, loginPresenter);

        LoginController loginController =
                new LoginController(loginInteractor);

        this.loginView = new LoginView(loginViewModel);
        this.loginView.setLoginController(loginController);

        cardPanel.add(loginView, loginView.getViewName());

        return this;
    }

    public AppBuilder addSignupView(Firestore db) {

        signupViewModel = new SignupViewModel();

        SignupUserDataAccessInterface signupUserDao =
                new InMemoryUserDataAccessObject(new UserFactory());

        SignupOutputBoundary signupPresenter =
                new SignupPresenter(viewManagerModel, signupViewModel, loginViewModel);

        SignupInputBoundary signupInteractor =
                new SignupInteractor(signupUserDao, signupPresenter, new UserFactory());

        SignupController signupController =
                new SignupController(signupInteractor);

        this.signupView = new SignupView(signupViewModel);
        this.signupView.setSignupController(signupController);

        cardPanel.add(signupView, signupView.getViewName());

        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("Recipe Application");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(cardPanel);

        viewManagerModel.setState(signupView.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }
}
