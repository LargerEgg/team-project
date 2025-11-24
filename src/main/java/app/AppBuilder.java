package app;

import data_access.RecipeDataAccessObject;
import data_access.UserDataAccessObject;
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.post_recipe.PostRecipeViewModel;
import interface_adapter.recipe_search.RecipeSearchController;
import interface_adapter.recipe_search.RecipeSearchPresenter;
import interface_adapter.recipe_search.RecipeSearchState;
import interface_adapter.recipe_search.RecipeSearchViewModel;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.post_recipe.PostRecipeDataAccessInterface;
import use_case.recipe_search.RecipeSearchInputBoundary;
import use_case.recipe_search.RecipeSearchInteractor;
import use_case.recipe_search.RecipeSearchOutputBoundary;
import use_case.recipe_search.RecipeSearchRecipeDataAccessInterface;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.signup.SignupUserDataAccessInterface;

import interface_adapter.post_recipe.PostRecipeController;
import interface_adapter.post_recipe.PostRecipePresenter;
import interface_adapter.post_recipe.PostRecipeViewModel;
import use_case.post_recipe.PostRecipeInputBoundary;
import use_case.post_recipe.PostRecipeInteractor;
import use_case.post_recipe.PostRecipeOutputBoundary;
import data_access.PostRecipeDataAccessObject;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final UserFactory userFactory = new UserFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);
    private UserDataAccessObject userDataAccessObject = new UserDataAccessObject();

    private SignupView signupView;
    private SignupViewModel signupViewModel;
    private LoginView loginView;
    private LoginViewModel loginViewModel;
    private RecipeSearchViewModel recipeSearchViewModel;
    private RecipeSearchView recipeSearchView;

    private PostRecipeView postRecipeView;
    private PostRecipeViewModel postRecipeViewModel;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addSignupView() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel, viewManagerModel);
        cardPanel.add(signupView, signupView.viewName);
        return this;
    }

    public AppBuilder addSignupUseCase() {
        final SignupOutputBoundary signupOutputBoundary = new SignupPresenter(viewManagerModel,
                signupViewModel, loginViewModel);
        final SignupInputBoundary userSignupInteractor = new SignupInteractor(
                userDataAccessObject, signupOutputBoundary, userFactory);

        SignupController controller = new SignupController(userSignupInteractor);
        signupView.setSignupController(controller);
        return this;
    }

    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel, viewManagerModel);
        cardPanel.add(loginView, loginView.viewName);
        return this;
    }

    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                recipeSearchViewModel, loginViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    public AppBuilder addRecipeSearchView() {
        recipeSearchViewModel = new RecipeSearchViewModel();
        RecipeSearchRecipeDataAccessInterface recipeDAO = new RecipeDataAccessObject();

        // Pre-fetch categories and set them in the state
        List<String> categories = recipeDAO.getAllCategories();
        RecipeSearchState initialState = recipeSearchViewModel.getState();
        initialState.setCategories(categories);
        recipeSearchViewModel.setState(initialState);

        RecipeSearchOutputBoundary recipeSearchOutputBoundary = new RecipeSearchPresenter(viewManagerModel, recipeSearchViewModel);
        RecipeSearchInputBoundary recipeSearchInteractor = new RecipeSearchInteractor(recipeDAO, recipeSearchOutputBoundary);
        RecipeSearchController recipeSearchController = new RecipeSearchController(recipeSearchInteractor);
        recipeSearchView = new RecipeSearchView(recipeSearchViewModel, recipeSearchController, viewManagerModel);
        cardPanel.add(recipeSearchView, recipeSearchView.viewName);
        return this;
    }

    public AppBuilder addPostRecipeView() {
        // 1. ViewModel
        postRecipeViewModel = new PostRecipeViewModel();

        // 2. Presenter
        // Adjust args to match your actual PostRecipePresenter constructor.
        PostRecipeOutputBoundary postRecipeOutputBoundary =
                new PostRecipePresenter(viewManagerModel, postRecipeViewModel);

        // 3. Interactor
        PostRecipeDataAccessInterface postRecipeDataAccess = new PostRecipeDataAccessObject();
        PostRecipeInputBoundary postRecipeInteractor =
                new PostRecipeInteractor(postRecipeDataAccess, postRecipeOutputBoundary);

        // 4. Controller
//        postRecipeController = new PostRecipeController(postRecipeInteractor);

        // 5. View
        postRecipeView = new PostRecipeView(
                postRecipeViewModel, viewManagerModel);

        // 6. Register in CardLayout
        cardPanel.add(postRecipeView, postRecipeView.viewName);

        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("Recipe Application");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(cardPanel);

        viewManagerModel.setState(recipeSearchView.viewName);
        viewManagerModel.firePropertyChange();

        return application;
    }
}
