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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private PostRecipeViewModel postRecipeViewModel;
    private PostRecipeView postRecipeView;
    private PostRecipeController postRecipeController;

    private RecommendRecipeViewModel recommendRecipeViewModel;
    private RecommendRecipeView recommendRecipeView;

    private EditReviewViewModel editReviewViewModel;
    private EditReviewController editReviewController;
    private ViewRecipeController viewRecipeController; // Corrected member variable

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);

        apiRecipeDataAccessObject = new RecipeDataAccessObject();
        recipeDataAccessObject = apiRecipeDataAccessObject;

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
                firebaseUserDataAccessObject = null;
                firebaseRecipeDataAccessObject = null;
                firebaseReviewDataAccessObject = null;
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
            userDAO = firebaseUserDataAccessObject;
        } else {
            userDAO = new InMemoryUserDataAccessObject();
        }

        final SignupOutputBoundary signupOutputBoundary = new SignupPresenter(viewManagerModel, signupViewModel, loginViewModel);
        final SignupInputBoundary userSignupInteractor = new SignupInteractor(userDAO, signupOutputBoundary, userFactory);
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
            userDAO = firebaseUserDataAccessObject;
        } else {
            userDAO = new InMemoryUserDataAccessObject();
        }

        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel, recipeSearchViewModel, loginViewModel, editReviewViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(userDAO, loginOutputBoundary);
        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    public AppBuilder addRecipeSearchView(ViewRecipeController viewRecipeController) {
        recipeSearchViewModel = new RecipeSearchViewModel();

        RecipeSearchRecipeDataAccessInterface recipeDAO;
        if (USE_FIREBASE && firebaseRecipeDataAccessObject != null) {
            recipeDAO = new AppBuilderCompositeRecipeSearchDAO(apiRecipeDataAccessObject, firebaseRecipeDataAccessObject);
        } else {
            recipeDAO = apiRecipeDataAccessObject;
        }

        List<String> categories = recipeDAO.getAllCategories();
        RecipeSearchState initialState = recipeSearchViewModel.getState();
        initialState.setCategories(categories);
        recipeSearchViewModel.setState(initialState);

        ShowSavedRecipesOutputBoundary savedRecipesPresenter = new SavedRecipesPresenter(recipeSearchViewModel);
        ShowSavedRecipesDataAccessInterface savedRecipesDAO = new FirebaseSaveRecipeDataAccessObject(apiRecipeDataAccessObject);
        ShowSavedRecipesInputBoundary savedRecipesInteractor = new ShowSavedRecipesInteractor(savedRecipesDAO, savedRecipesPresenter);
        ShowSavedRecipesController showSavedRecipesController = new ShowSavedRecipesController(savedRecipesInteractor);

        RecipeSearchOutputBoundary recipeSearchOutputBoundary = new RecipeSearchPresenter(viewManagerModel, recipeSearchViewModel);
        RecipeSearchInputBoundary recipeSearchInteractor = new RecipeSearchInteractor(recipeDAO, recipeSearchOutputBoundary);
        RecipeSearchController recipeSearchController = new RecipeSearchController(recipeSearchInteractor);

        recommendRecipeViewModel = new RecommendRecipeViewModel();
        RecommendRecipeOutputBoundary recommendPresenter = new RecommendRecipePresenter(recommendRecipeViewModel, viewManagerModel);
        RecommendRecipeDataAccessInterface recommendDAO = new CompositeRecommendRecipeDAO(savedRecipesDAO, new RecommendRecipeDataAccessObject());
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
        SaveRecipeOutputBoundary saveRecipePresenter = new SaveRecipePresenter(saveRecipeViewModel);
        SaveRecipeDataAccessInterface saveRecipeDAO = new FirebaseSaveRecipeDataAccessObject(apiRecipeDataAccessObject);
        SaveRecipeInputBoundary saveRecipeInteractor = new SaveRecipeInteractor(saveRecipeDAO, saveRecipePresenter);
        SaveRecipeController saveRecipeController = new SaveRecipeController(saveRecipeInteractor);

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
        EditReviewDataAccessInterface editReviewDAO = USE_FIREBASE && firebaseReviewDataAccessObject != null
                ? firebaseReviewDataAccessObject
                : new ReviewDataAccessObject();
        ViewRecipeInputBoundary viewRecipeInteractor = new ViewRecipeInteractor(viewRecipeDataAccessObject, viewRecipeOutputBoundary, saveRecipeDAO, editReviewDAO);
        this.viewRecipeController = new ViewRecipeController(viewRecipeInteractor); // Assign to member variable
        return this.viewRecipeController; // Return the assigned member variable
    }

    public AppBuilder addPostRecipeView() {
        postRecipeViewModel = new PostRecipeViewModel();
        postRecipeView = new PostRecipeView(postRecipeViewModel, recipeSearchViewModel, viewManagerModel);
        cardPanel.add(postRecipeView, postRecipeView.viewName);
        return this;
    }

    public AppBuilder addPostRecipeUseCase() {
        PostRecipeOutputBoundary postRecipeOutputBoundary = new PostRecipePresenter(viewManagerModel, postRecipeViewModel);
        PostRecipeDataAccessInterface postRecipeDataAccess = USE_FIREBASE && firebaseRecipeDataAccessObject != null
                ? firebaseRecipeDataAccessObject
                : new PostRecipeDataAccessObject();

        PostRecipeInputBoundary postRecipeInteractor = new PostRecipeInteractor(postRecipeDataAccess, postRecipeOutputBoundary);
        postRecipeController = new PostRecipeController(postRecipeInteractor);
        postRecipeView.setPostRecipeController(postRecipeController);
        return this;
    }

    public AppBuilder addEditReviewUseCase() {
        EditReviewOutputBoundary editReviewOutputBoundary = new EditReviewPresenter(
                viewManagerModel,
                editReviewViewModel,
                viewRecipeViewModel,
                this.viewRecipeController // Use the member variable here
        );
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
        viewManagerModel.setState(recipeSearchView.viewName);
        viewManagerModel.firePropertyChange();
        return application;
    }

    // -------------------------------------------------------------------------
    // CompositeRecipeDataAccess
    // Responsible for View Recipe. Ensures that after fetching from API,
    // reviews and view counts from Firebase are merged.
    // -------------------------------------------------------------------------
    private static class CompositeRecipeDataAccess implements ViewRecipeDataAccessInterface {
        private final FirebaseRecipeDataAccessObject firebaseDAO;
        private final RecipeDataAccessObject apiDAO;

        public CompositeRecipeDataAccess(FirebaseRecipeDataAccessObject firebaseDAO, RecipeDataAccessObject apiDAO) {
            this.firebaseDAO = firebaseDAO;
            this.apiDAO = apiDAO;
        }

        @Override
        public entity.Recipe findById(String recipeId) {
            // 1. Try to get from API first
            entity.Recipe recipe = apiDAO.findById(recipeId);

            // 2. Check Firebase for additional data (reviews, views)
            if (firebaseDAO != null) {
                try {
                    // Use existing findById which parses reviews automatically
                    entity.Recipe fbRecipe = firebaseDAO.findById(recipeId);

                    if (fbRecipe != null) {
                        // If API returned null, it might be a user-created recipe in Firebase
                        if (recipe == null) {
                            return fbRecipe;
                        }

                        // If API returned a recipe, merge Firebase data (Reviews, Views) into it
                        if (fbRecipe.getReviews() != null && !fbRecipe.getReviews().isEmpty()) {
                            recipe.setReviews(fbRecipe.getReviews());
                        }
                        if (fbRecipe.getViews() > 0) {
                            recipe.setViews(fbRecipe.getViews());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Failed to merge data from Firebase: " + e.getMessage());
                }
            }
            return recipe;
        }

        @Override
        public void recordView(String recipeId) {
            // Record view in both places
            if (firebaseDAO != null) firebaseDAO.recordView(recipeId);
            apiDAO.recordView(recipeId);
        }
    }

    // -------------------------------------------------------------------------
    // AppBuilderCompositeRecipeSearchDAO
    // Responsible for Search. Combines results from API and Firebase.
    // -------------------------------------------------------------------------
    private static class AppBuilderCompositeRecipeSearchDAO implements RecipeSearchRecipeDataAccessInterface {
        private final RecipeDataAccessObject apiDAO;
        private final FirebaseRecipeDataAccessObject firebaseDAO;

        public AppBuilderCompositeRecipeSearchDAO(RecipeDataAccessObject apiDAO, FirebaseRecipeDataAccessObject firebaseDAO) {
            this.apiDAO = apiDAO;
            this.firebaseDAO = firebaseDAO;
        }

        @Override
        public List<Recipe> search(String name, String category) {
            // 1. Get recipes from the API
            List<Recipe> apiRecipes = apiDAO.search(name, category);

            // 2. Get recipes from Firebase
            List<Recipe> firebaseRecipes;
            if (firebaseDAO != null) {
                if (category != null && !category.isEmpty()) {
                    firebaseRecipes = firebaseDAO.findByCategory(category, name);
                } else {
                    firebaseRecipes = firebaseDAO.search(name, null);
                }
            } else {
                firebaseRecipes = new ArrayList<>();
            }


            // 3. Combine and remove duplicates
            List<Recipe> combinedRecipes = Stream.concat(apiRecipes.stream(), firebaseRecipes.stream())
                    .distinct()
                    .collect(Collectors.toList());

            // 4. Asynchronously fetch popularity data (views, saves, averageRating) from Firebase
            if (firebaseDAO != null && combinedRecipes != null && !combinedRecipes.isEmpty()) {
                List<CompletableFuture<Void>> futures = combinedRecipes.stream()
                        .flatMap(recipe -> {
                            String recipeId = recipe.getRecipeId();
                            // Create futures for views, saves, and averageRating
                            CompletableFuture<Void> viewsFuture = firebaseDAO.getViewCount(recipeId)
                                    .thenAccept(recipe::setViews);
                            CompletableFuture<Void> savesFuture = firebaseDAO.getSaveCount(recipeId)
                                    .thenAccept(recipe::setSaves);
                            CompletableFuture<Void> ratingFuture = firebaseDAO.getAverageRating(recipeId)
                                    .thenAccept(recipe::setAverageRating);
                            return java.util.stream.Stream.of(viewsFuture, savesFuture, ratingFuture);
                        })
                        .collect(Collectors.toList());

                // 5. Wait for all the fetches to complete
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            }

            return combinedRecipes;
        }

        @Override
        public List<String> getAllCategories() {
            List<String> apiCategories = apiDAO.getAllCategories();
            List<String> firebaseCategories = new ArrayList<>();
            if (firebaseDAO != null) {
                firebaseCategories = firebaseDAO.getAllCategories();
            }


            return Stream.concat(apiCategories.stream(), firebaseCategories.stream())
                    .distinct()
                    .collect(Collectors.toList());
        }
    }
}
