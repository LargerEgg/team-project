package app;

import data_access.*;
import entity.Recipe;
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
import interface_adapter.save_recipe.SaveRecipeController;
import interface_adapter.save_recipe.SaveRecipePresenter;
import interface_adapter.save_recipe.SaveRecipeViewModel;
import interface_adapter.saved_recipes.SavedRecipesPresenter;
import interface_adapter.saved_recipes.SavedRecipesViewModel;
import interface_adapter.saved_recipes.ShowSavedRecipesController;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import interface_adapter.unsave_recipe.UnsaveRecipeController;
import interface_adapter.unsave_recipe.UnsaveRecipePresenter;
import interface_adapter.view_recipe.ViewRecipeController;
import interface_adapter.view_recipe.ViewRecipePresenter;
import interface_adapter.view_recipe.ViewRecipeViewModel;
import interface_adapter.recommend_recipe.RecommendRecipeController;
import interface_adapter.recommend_recipe.RecommendRecipePresenter;
import interface_adapter.recommend_recipe.RecommendRecipeViewModel;
import interface_adapter.edit_review.EditReviewViewModel;
import interface_adapter.edit_review.EditReviewController;
import interface_adapter.edit_review.EditReviewPresenter;
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
import use_case.recommend_recipe.RecommendRecipeDataAccessInterface;
import use_case.recommend_recipe.RecommendRecipeInputBoundary;
import use_case.recommend_recipe.RecommendRecipeInteractor;
import use_case.recommend_recipe.RecommendRecipeOutputBoundary;
import use_case.save_recipe.SaveRecipeDataAccessInterface;
import use_case.save_recipe.SaveRecipeInputBoundary;
import use_case.save_recipe.SaveRecipeInteractor;
import use_case.save_recipe.SaveRecipeOutputBoundary;
import use_case.saved_recipes.ShowSavedRecipesDataAccessInterface;
import use_case.saved_recipes.ShowSavedRecipesInputBoundary;
import use_case.saved_recipes.ShowSavedRecipesInteractor;
import use_case.saved_recipes.ShowSavedRecipesOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.signup.SignupUserDataAccessInterface;
import use_case.unsave_recipe.UnsaveRecipeDataAccessInterface;
import use_case.unsave_recipe.UnsaveRecipeInputBoundary;
import use_case.unsave_recipe.UnsaveRecipeInteractor;
import use_case.unsave_recipe.UnsaveRecipeOutputBoundary;
import use_case.view_recipe.ViewRecipeDataAccessInterface;
import use_case.view_recipe.ViewRecipeInputBoundary;
import use_case.view_recipe.ViewRecipeInteractor;
import use_case.view_recipe.ViewRecipeOutputBoundary;
import use_case.edit_review.EditReviewOutputBoundary;
import use_case.edit_review.EditReviewDataAccessInterface;
import use_case.edit_review.EditReviewInputBoundary;
import use_case.edit_review.EditReviewInteractor;
import view.*;

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
    private RecipeDataAccessObject recipeDataAccessObject;
    private ReviewDataAccessObject reviewDataAccessObject = new ReviewDataAccessObject();

    private static final boolean USE_FIREBASE = true;
    private FirebaseUserDataAccessObject firebaseUserDataAccessObject;
    private FirebaseRecipeDataAccessObject firebaseRecipeDataAccessObject;
    private RecipeDataAccessObject apiRecipeDataAccessObject; // For MealDB API
    private FirebaseReviewDataAccessObject firebaseReviewDataAccessObject;

    private SignupView signupView;
    private SignupViewModel signupViewModel;
    private LoginView loginView;
    private LoginViewModel loginViewModel;
    private RecipeSearchViewModel recipeSearchViewModel;
    private RecipeSearchView recipeSearchView;
    private EditReviewView editReviewView;

    private ViewRecipeViewModel viewRecipeViewModel;
    private RecipeView recipeView;

    // New fields for PostRecipe
    private PostRecipeViewModel postRecipeViewModel;
    private PostRecipeView postRecipeView;
    private PostRecipeController postRecipeController;

    private RecommendRecipeViewModel recommendRecipeViewModel;
    private RecommendRecipeView recommendRecipeView;

    private EditReviewViewModel editReviewViewModel;
    private EditReviewController editReviewController;

    public AppBuilder() {

        cardPanel.setLayout(cardLayout);

        apiRecipeDataAccessObject = new RecipeDataAccessObject();
        recipeDataAccessObject = apiRecipeDataAccessObject;

        // Initialize Firebase if enabled
        if (USE_FIREBASE) {
            try {
                FirebaseInitializer.initialize();
                firebaseUserDataAccessObject = new FirebaseUserDataAccessObject(userFactory);
                firebaseRecipeDataAccessObject = new FirebaseRecipeDataAccessObject();
                firebaseReviewDataAccessObject = new FirebaseReviewDataAccessObject();
                System.out.println("Firebase data access objects initialized successfully!");
            } catch (IOException | IllegalStateException e) {
                System.err.println("Failed to initialize Firebase: " + e.getMessage());
                System.err.println("Falling back to in-memory data access.");
                // Fall back to non-Firebase implementation
                firebaseUserDataAccessObject = null;
                firebaseRecipeDataAccessObject = null;
            }
        }
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
                userDAO, signupOutputBoundary, userFactory);

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
        use_case.login.LoginUserDataAccessInterface userDAO;

        if (USE_FIREBASE && firebaseUserDataAccessObject != null) {
            System.out.println("✅ Using Firebase for user login");
            userDAO = firebaseUserDataAccessObject;
        } else {
            System.out.println("⚠️  Using in-memory storage for user login");
            userDAO = new InMemoryUserDataAccessObject();
        }

        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                recipeSearchViewModel, loginViewModel, editReviewViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDAO, loginOutputBoundary);

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    // Modified to accept ViewRecipeController
    public AppBuilder addRecipeSearchView(ViewRecipeController viewRecipeController) {
        recipeSearchViewModel = new RecipeSearchViewModel();

        RecipeSearchRecipeDataAccessInterface recipeDAO;
        if (USE_FIREBASE && firebaseRecipeDataAccessObject != null) {
            recipeDAO = new CompositeRecipeSearchDAO(apiRecipeDataAccessObject, firebaseRecipeDataAccessObject);
        } else {
            recipeDAO = apiRecipeDataAccessObject;
        }

        // Pre-fetch categories and set them in the state
        List<String> categories = recipeDAO.getAllCategories();
        RecipeSearchState initialState = recipeSearchViewModel.getState();
        initialState.setCategories(categories);
        recipeSearchViewModel.setState(initialState);

        ShowSavedRecipesOutputBoundary savedRecipesPresenter =
                new SavedRecipesPresenter(recipeSearchViewModel);

        ShowSavedRecipesDataAccessInterface savedRecipesDAO =
                new FirebaseSaveRecipeDataAccessObject(apiRecipeDataAccessObject);

        ShowSavedRecipesInputBoundary savedRecipesInteractor =
                new ShowSavedRecipesInteractor(savedRecipesDAO, savedRecipesPresenter);

        ShowSavedRecipesController showSavedRecipesController =
                new ShowSavedRecipesController(savedRecipesInteractor);

        RecipeSearchOutputBoundary recipeSearchOutputBoundary = new RecipeSearchPresenter(viewManagerModel, recipeSearchViewModel);
        RecipeSearchInputBoundary recipeSearchInteractor = new RecipeSearchInteractor(recipeDAO, recipeSearchOutputBoundary);
        RecipeSearchController recipeSearchController = new RecipeSearchController(recipeSearchInteractor);

        recommendRecipeViewModel = new RecommendRecipeViewModel();
        RecommendRecipeOutputBoundary recommendPresenter = new RecommendRecipePresenter(recommendRecipeViewModel, viewManagerModel);
        RecommendRecipeDataAccessInterface recommendDAO = new CompositeRecommendRecipeDAO(
                savedRecipesDAO,
                new RecommendRecipeDataAccessObject()
        );
        RecommendRecipeInputBoundary recommendInteractor = new RecommendRecipeInteractor(recommendDAO, recommendPresenter);
        RecommendRecipeController recommendController = new RecommendRecipeController(recommendInteractor);

        recipeSearchView = new RecipeSearchView(
                recipeSearchViewModel,
                recipeSearchController,
                viewManagerModel,
                viewRecipeController,
                recommendController,
                showSavedRecipesController,
                editReviewViewModel
        );

        cardPanel.add(recipeSearchView, recipeSearchView.viewName);
        return this;
    }

    public AppBuilder addRecommendRecipeView(ViewRecipeController viewRecipeController) {
        if (recommendRecipeViewModel == null) {
            recommendRecipeViewModel = new RecommendRecipeViewModel();
        }
        recommendRecipeView = new RecommendRecipeView(recommendRecipeViewModel, viewManagerModel, viewRecipeController);
        cardPanel.add(recommendRecipeView, recommendRecipeView.viewName);
        return this;
    }

    public AppBuilder addViewRecipeView() {
        viewRecipeViewModel = new ViewRecipeViewModel();

        SaveRecipeViewModel saveRecipeViewModel = new SaveRecipeViewModel();

        SaveRecipeOutputBoundary saveRecipePresenter =
                new SaveRecipePresenter(saveRecipeViewModel);

        SaveRecipeDataAccessInterface saveRecipeDAO =
                new FirebaseSaveRecipeDataAccessObject(apiRecipeDataAccessObject);

        SaveRecipeInputBoundary saveRecipeInteractor =
                new SaveRecipeInteractor(saveRecipeDAO, saveRecipePresenter);

        SaveRecipeController saveRecipeController =
                new SaveRecipeController(saveRecipeInteractor);

        UnsaveRecipeOutputBoundary unsaveRecipeOutputBoundary = new UnsaveRecipePresenter(saveRecipeViewModel);
        UnsaveRecipeInputBoundary unsaveRecipeInteractor = new UnsaveRecipeInteractor((UnsaveRecipeDataAccessInterface) saveRecipeDAO, unsaveRecipeOutputBoundary);
        UnsaveRecipeController unsaveRecipeController = new UnsaveRecipeController(unsaveRecipeInteractor);

        recipeView = new RecipeView(viewRecipeViewModel, viewManagerModel, saveRecipeController, saveRecipeViewModel, unsaveRecipeController);
        cardPanel.add(recipeView, recipeView.viewName);
        return this;
    }

    public AppBuilder addEditReviewView() {
        editReviewViewModel = new EditReviewViewModel();
        editReviewView = new EditReviewView(editReviewViewModel, viewManagerModel);
        cardPanel.add(editReviewView, editReviewView.getViewName());
        return this;
    }

    // Modified to return ViewRecipeController
    public ViewRecipeController addViewRecipeUseCase() {
        ViewRecipeOutputBoundary viewRecipeOutputBoundary = new ViewRecipePresenter(viewRecipeViewModel, viewManagerModel, editReviewViewModel);

        ViewRecipeDataAccessInterface viewRecipeDataAccessObject;
        if (USE_FIREBASE && firebaseRecipeDataAccessObject != null) {
            viewRecipeDataAccessObject = new CompositeRecipeDataAccess(
                    firebaseRecipeDataAccessObject,
                    apiRecipeDataAccessObject
            );
        } else {
            viewRecipeDataAccessObject = apiRecipeDataAccessObject;
        }

        SaveRecipeDataAccessInterface saveRecipeDAO = new FirebaseSaveRecipeDataAccessObject(apiRecipeDataAccessObject);
        ViewRecipeInputBoundary viewRecipeInteractor = new ViewRecipeInteractor(viewRecipeDataAccessObject, viewRecipeOutputBoundary, saveRecipeDAO);
        return new ViewRecipeController(viewRecipeInteractor);
    }

    // New method to add PostRecipeView
    public AppBuilder addPostRecipeView() {
        postRecipeViewModel = new PostRecipeViewModel();
        postRecipeView = new PostRecipeView(postRecipeViewModel, recipeSearchViewModel, viewManagerModel);
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

    public AppBuilder addEditReviewUseCase() {
        EditReviewOutputBoundary editReviewOutputBoundary = new EditReviewPresenter(viewManagerModel, editReviewViewModel);

        // Use Firebase for posting reviews if available, otherwise use in-memory
        EditReviewDataAccessInterface editReviewDataAccess = USE_FIREBASE && firebaseReviewDataAccessObject != null
                ? firebaseReviewDataAccessObject
                : new ReviewDataAccessObject();


        EditReviewInputBoundary editReviewInteractor = new EditReviewInteractor(editReviewDataAccess, editReviewOutputBoundary);
        editReviewController = new EditReviewController(editReviewInteractor);
        editReviewView.setEditReviewController(editReviewController);
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
        public void recordView(String recipeId) {
            firebaseDAO.recordView(recipeId);
            apiDAO.recordView(recipeId);
        }
    }

    private static class CompositeRecipeSearchDAO implements RecipeSearchRecipeDataAccessInterface {
        private final RecipeDataAccessObject apiDAO;
        private final FirebaseRecipeDataAccessObject firebaseDAO;

        public CompositeRecipeSearchDAO(RecipeDataAccessObject apiDAO, FirebaseRecipeDataAccessObject firebaseDAO) {
            this.apiDAO = apiDAO;
            this.firebaseDAO = firebaseDAO;
        }

        @Override
        public List<Recipe> search(String name, String category) {
            return List.of();
        }

        @Override
        public List<String> getAllCategories() {
            return apiDAO.getAllCategories();
        }

        public List<entity.Recipe> search(String name, String category, RecipeSearchOutputBoundary presenter) {
            return apiDAO.search(name, category);
        }
    }
}