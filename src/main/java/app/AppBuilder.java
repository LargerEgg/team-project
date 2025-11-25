package app;

import data_access.*;
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.post_recipe.PostRecipeController;
import interface_adapter.post_recipe.PostRecipePresenter;
import interface_adapter.post_recipe.PostRecipeViewModel;
import interface_adapter.recipe_search.RecipeSearchController;
import interface_adapter.recipe_search.RecipeSearchPresenter;
import interface_adapter.recipe_search.RecipeSearchState;
import interface_adapter.recipe_search.RecipeSearchViewModel;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import interface_adapter.view_recipe.ViewRecipeController;
import interface_adapter.view_recipe.ViewRecipePresenter;
import interface_adapter.view_recipe.ViewRecipeViewModel;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.post_recipe.PostRecipeDataAccessInterface;
import use_case.post_recipe.PostRecipeInputBoundary;
import use_case.post_recipe.PostRecipeInteractor;
import use_case.post_recipe.PostRecipeOutputBoundary;
import use_case.recipe_search.RecipeSearchInputBoundary;
import use_case.recipe_search.RecipeSearchInteractor;
import use_case.recipe_search.RecipeSearchOutputBoundary;
import use_case.recipe_search.RecipeSearchRecipeDataAccessInterface;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.signup.SignupUserDataAccessInterface;
import use_case.view_recipe.ViewRecipeDataAccessInterface;
import use_case.view_recipe.ViewRecipeInputBoundary;
import use_case.view_recipe.ViewRecipeInteractor;
import use_case.view_recipe.ViewRecipeOutputBoundary;
import view.LoginView;
import view.PostRecipeView;
import view.RecipeSearchView;
import view.RecipeView;
import view.SignupView;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final UserFactory userFactory = new UserFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);
    private UserDataAccessObject userDataAccessObject = new UserDataAccessObject();
    private RecipeDataAccessObject recipeDataAccessObject = new RecipeDataAccessObject();

    private static final boolean USE_FIREBASE = true;
    private FirebaseUserDataAccessObject firebaseUserDataAccessObject;
    private FirebaseRecipeDataAccessObject firebaseRecipeDataAccessObject;
    private RecipeDataAccessObject apiRecipeDataAccessObject; // For MealDB API

    private SignupView signupView;
    private SignupViewModel signupViewModel;
    private LoginView loginView;
    private LoginViewModel loginViewModel;
    private RecipeSearchViewModel recipeSearchViewModel;
    private RecipeSearchView recipeSearchView;

    private ViewRecipeViewModel viewRecipeViewModel;
    private RecipeView recipeView;

    // New fields for PostRecipe
    private PostRecipeViewModel postRecipeViewModel;
    private PostRecipeView postRecipeView;
    private PostRecipeController postRecipeController; // Declare the controller here

    public AppBuilder() {

        cardPanel.setLayout(cardLayout);

        // Initialize Firebase if enabled
        if (USE_FIREBASE) {
            try {
                FirebaseInitializer.initialize();
                firebaseUserDataAccessObject = new FirebaseUserDataAccessObject(userFactory);
                firebaseRecipeDataAccessObject = new FirebaseRecipeDataAccessObject();
                System.out.println("Firebase data access objects initialized successfully!");
            } catch (IOException e) {
                System.err.println("Failed to initialize Firebase: " + e.getMessage());
                System.err.println("Falling back to in-memory data access.");
                // Fall back to non-Firebase implementation
                firebaseUserDataAccessObject = null;
                firebaseRecipeDataAccessObject = null;
            }
        }

        // Always initialize API DAO for searching external recipes
        apiRecipeDataAccessObject = new RecipeDataAccessObject();
    }

    public AppBuilder addSignupView() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel, viewManagerModel);
        cardPanel.add(signupView, signupView.viewName);
        return this;
    }

    public AppBuilder addSignupUseCase() {
        SignupUserDataAccessInterface userDAO;

        if (USE_FIREBASE && firebaseUserDataAccessObject != null) {
            System.out.println("✅ Using Firebase for user signup");
            userDAO = firebaseUserDataAccessObject;
        } else {
            System.out.println("⚠️  Using in-memory storage for user signup");
            userDAO = new InMemoryUserDataAccessObject();
        }


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
        use_case.login.LoginUserDataAccessInterface userDAO = USE_FIREBASE && firebaseUserDataAccessObject != null
                ? firebaseUserDataAccessObject
                : new data_access.UserDataAccessObject();

        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                recipeSearchViewModel, loginViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    // Modified to accept ViewRecipeController
    public AppBuilder addRecipeSearchView(ViewRecipeController viewRecipeController) {
        recipeSearchViewModel = new RecipeSearchViewModel();
        RecipeSearchRecipeDataAccessInterface recipeDAO = apiRecipeDataAccessObject;

        // Pre-fetch categories and set them in the state
        List<String> categories = recipeDAO.getAllCategories();
        RecipeSearchState initialState = recipeSearchViewModel.getState();
        initialState.setCategories(categories);
        recipeSearchViewModel.setState(initialState);

        RecipeSearchOutputBoundary recipeSearchOutputBoundary = new RecipeSearchPresenter(viewManagerModel, recipeSearchViewModel);
        RecipeSearchInputBoundary recipeSearchInteractor = new RecipeSearchInteractor(recipeDAO, recipeSearchOutputBoundary);
        RecipeSearchController recipeSearchController = new RecipeSearchController(recipeSearchInteractor);
        recipeSearchView = new RecipeSearchView(recipeSearchViewModel, recipeSearchController, viewManagerModel, viewRecipeController);
        cardPanel.add(recipeSearchView, recipeSearchView.viewName);
        return this;
    }

    public AppBuilder addViewRecipeView() {
        viewRecipeViewModel = new ViewRecipeViewModel();
        recipeView = new RecipeView(viewRecipeViewModel, viewManagerModel);
        cardPanel.add(recipeView, recipeView.viewName);
        return this;
    }

    // Modified to return ViewRecipeController
    public ViewRecipeController addViewRecipeUseCase() {
        ViewRecipeOutputBoundary viewRecipeOutputBoundary = new ViewRecipePresenter(viewRecipeViewModel, viewManagerModel);

        ViewRecipeDataAccessInterface viewRecipeDataAccessObject;
        if (USE_FIREBASE && firebaseRecipeDataAccessObject != null) {
            viewRecipeDataAccessObject = new CompositeRecipeDataAccess(
                    firebaseRecipeDataAccessObject,
                    apiRecipeDataAccessObject
            );
        } else {
            viewRecipeDataAccessObject = apiRecipeDataAccessObject;
        }

        ViewRecipeInputBoundary viewRecipeInteractor = new ViewRecipeInteractor(viewRecipeDataAccessObject, viewRecipeOutputBoundary);
        return new ViewRecipeController(viewRecipeInteractor);
    }

    // New method to add PostRecipeView
    public AppBuilder addPostRecipeView() {
        postRecipeViewModel = new PostRecipeViewModel();
        postRecipeView = new PostRecipeView(postRecipeViewModel, viewManagerModel);
        cardPanel.add(postRecipeView, postRecipeView.viewName);
        return this;
    }

    // New method to add PostRecipeUseCase
    public AppBuilder addPostRecipeUseCase() {
        PostRecipeOutputBoundary postRecipeOutputBoundary = new PostRecipePresenter(viewManagerModel, postRecipeViewModel);

        // Use Firebase for posting recipes if available, otherwise use in-memory
        PostRecipeDataAccessInterface postRecipeDataAccess = USE_FIREBASE && firebaseRecipeDataAccessObject != null
                ? firebaseRecipeDataAccessObject
                : new PostRecipeDataAccessObject();

        PostRecipeInputBoundary postRecipeInteractor = new PostRecipeInteractor(postRecipeDataAccess, postRecipeOutputBoundary);
        postRecipeController = new PostRecipeController(postRecipeInteractor);
        postRecipeView.setPostRecipeController(postRecipeController);
        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("Recipe Application");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (USE_FIREBASE) {
                FirebaseInitializer.shutdown();
            }
        }));

        application.add(cardPanel);

        // Set the initial view to the recipe search view
        viewManagerModel.setState(recipeSearchView.viewName);
        viewManagerModel.firePropertyChange();

        return application;
    }

    private static class CompositeRecipeDataAccess implements ViewRecipeDataAccessInterface {
        private final FirebaseRecipeDataAccessObject firebaseDAO;
        private final RecipeDataAccessObject apiDAO;

        public CompositeRecipeDataAccess(FirebaseRecipeDataAccessObject firebaseDAO, RecipeDataAccessObject apiDAO) {
            this.firebaseDAO = firebaseDAO;
            this.apiDAO = apiDAO;
        }

        @Override
        public entity.Recipe findById(String recipeId) {
            // Try Firebase first (user-created recipes)
            entity.Recipe recipe = firebaseDAO.findById(recipeId);
            if (recipe != null) {
                return recipe;
            }
            // Fall back to API (external recipes)
            return apiDAO.findById(recipeId);
        }

        @Override
        public void save(entity.Recipe recipe) {
            // Always save to Firebase
            firebaseDAO.save(recipe);
        }
    }
}
