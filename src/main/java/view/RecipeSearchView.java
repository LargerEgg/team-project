package view;

import entity.Ingredient;
import entity.PopularityCalculator;
import entity.Recipe;
import interface_adapter.ViewManagerModel;
import interface_adapter.edit_review.EditReviewState;
import interface_adapter.edit_review.EditReviewViewModel;
import interface_adapter.recipe_search.RecipeSearchController;
import interface_adapter.recipe_search.RecipeSearchState;
import interface_adapter.recipe_search.RecipeSearchViewModel;
import interface_adapter.recommend_recipe.RecommendRecipeController;
import interface_adapter.saved_recipes.ShowSavedRecipesController;
import interface_adapter.view_recipe.ViewRecipeController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeSearchView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "recipe search";

    // Models & Controllers
    private final RecipeSearchViewModel recipeSearchViewModel;
    private final RecipeSearchController recipeSearchController;
    private final ViewManagerModel viewManagerModel;
    private final ViewRecipeController viewRecipeController;

    private final RecommendRecipeController recommendRecipeController;

    private final ShowSavedRecipesController showSavedRecipesController;
    private final EditReviewViewModel editReviewViewModel;

    // Header components
    private JTextField nameTextField;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> sortByComboBox;
    private JCheckBox ascendingCheckBox;
    private JButton searchButton;

    // Navigation Buttons
    private JButton signupButton;
    private JButton loginButton;
    private JButton logoutButton;
    private JButton postRecipeButton;
    private JButton recommendButton;
    private JButton savedRecipesButton;

    private JLabel currentUserLabel;

    // Results components
    private JPanel resultsPanel;
    private JProgressBar progressBar;
    private JLabel loadingLabel;

    public RecipeSearchView(RecipeSearchViewModel recipeSearchViewModel,
                            RecipeSearchController recipeSearchController,
                            ViewManagerModel viewManagerModel,
                            ViewRecipeController viewRecipeController,
                            RecommendRecipeController recommendRecipeController,
                            ShowSavedRecipesController showSavedRecipesController,
                            EditReviewViewModel editReviewViewModel) {

        this.recipeSearchViewModel = recipeSearchViewModel;
        this.recipeSearchController = recipeSearchController;
        this.viewManagerModel = viewManagerModel;
        this.viewRecipeController = viewRecipeController;
        this.recommendRecipeController = recommendRecipeController;
        this.showSavedRecipesController = showSavedRecipesController;
        this.editReviewViewModel = editReviewViewModel;
        this.recipeSearchViewModel.addPropertyChangeListener(this);

        // Listen to view changes to refresh when returning to this view
        this.viewManagerModel.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName()) && viewName.equals(evt.getNewValue())) {
                // Refresh the view when navigating back to recipe search
                List<Recipe> currentRecipes = new ArrayList<>(recipeSearchViewModel.getState().getRecipeList());
                if (!currentRecipes.isEmpty()) {
                    sortRecipes(currentRecipes);
                    updateView(currentRecipes);
                }
            }
        });

        setLayout(new BorderLayout());
        add(createNavigationBar(), BorderLayout.NORTH);

        JPanel searchAndResultsPanel = new JPanel(new BorderLayout());
        searchAndResultsPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        searchAndResultsPanel.add(createResultsScrollPane(), BorderLayout.CENTER);
        add(searchAndResultsPanel, BorderLayout.CENTER);

        updateUserRelatedUI(recipeSearchViewModel.getState());
    }

    public void updateView(List<Recipe> recipes) {
        resultsPanel.removeAll();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));

        if (recipes == null || recipes.isEmpty()) {
            resultsPanel.setLayout(new GridBagLayout());
            JLabel noResultsLabel = new JLabel("No search results found. Please modify your search query.");
            noResultsLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            noResultsLabel.setForeground(Color.GRAY);
            resultsPanel.add(noResultsLabel);
        } else {
            for (Recipe recipe : recipes) {
                resultsPanel.add(createRecipeItem(recipe));
            }
        }
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JPanel createNavigationBar() {
        JPanel navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        currentUserLabel = new JLabel("Not signed in");
        currentUserLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        navigationPanel.add(currentUserLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        signupButton = new JButton("Sign Up");
        loginButton = new JButton("Login");
        logoutButton = new JButton("Logout");
        postRecipeButton = new JButton("Post a Recipe");
        recommendButton = new JButton("Get Recommendations");

        signupButton.addActionListener(this);
        loginButton.addActionListener(this);
        logoutButton.addActionListener(this);
        postRecipeButton.addActionListener(this);
        recommendButton.addActionListener(this);

        buttonPanel.add(recommendButton);
        buttonPanel.add(postRecipeButton);
        buttonPanel.add(signupButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(logoutButton);

        navigationPanel.add(buttonPanel, BorderLayout.EAST);

        return navigationPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Recipe Search");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        headerPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        headerPanel.add(new JLabel("Name:"), gbc);

        nameTextField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(nameTextField, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        headerPanel.add(new JLabel("Category:"), gbc);

        List<String> categories = new ArrayList<>();
        categories.add("All");
        categories.addAll(recipeSearchViewModel.getState().getCategories());
        categoryComboBox = new JComboBox<>(categories.toArray(new String[0]));
        gbc.gridx = 5;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(categoryComboBox, gbc);

        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        headerPanel.add(searchButton, gbc);

        savedRecipesButton = new JButton("Show Saved Recipes");
        savedRecipesButton.addActionListener(this);
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        headerPanel.add(savedRecipesButton, gbc);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.BLACK);
        separator.setBackground(Color.BLACK);
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(separator, gbc);

        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        headerPanel.add(new JLabel("Sort By:"), gbc);

        String[] sortOptions = {"Views", "Name", "# of Ingredients"};
        sortByComboBox = new JComboBox<>(sortOptions);
        sortByComboBox.addActionListener(this);
        gbc.gridx = 1;
        headerPanel.add(sortByComboBox, gbc);

        JPanel ascendingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        ascendingPanel.add(new JLabel("Ascending?"));
        ascendingCheckBox = new JCheckBox();
        ascendingCheckBox.addActionListener(this);
        ascendingPanel.add(ascendingCheckBox);
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        headerPanel.add(ascendingPanel, gbc);

        return headerPanel;
    }

    private JScrollPane createResultsScrollPane() {
        resultsPanel = new JPanel();
        resultsPanel.setBackground(Color.WHITE);
        JScrollPane resultsScrollPane = new JScrollPane(resultsPanel);
        resultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return resultsScrollPane;
    }

    private JPanel createRecipeItem(Recipe recipe) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.X_AXIS));
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    String currentUser = recipeSearchViewModel.getState().getCurrentUser();
                    viewRecipeController.execute(recipe, currentUser);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                itemPanel.setBackground(new Color(240, 240, 240));
                itemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                itemPanel.setBackground(Color.WHITE);
                itemPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(80, 80));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        if (recipe.getImage() != null) {
            imageLabel.setIcon(new ImageIcon(recipe.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        } else {
            imageLabel.setText("[IMG]");
        }

        String hotPrefix = PopularityCalculator.isPopular(recipe)
                ? "<font color='red'><b>[Hot]</b></font> "
                : "";
        String nameAndTagsHtml = "<html>" + hotPrefix + "<b style='font-size: 150%;'>" + recipe.getTitle() + "</b>" +
                (recipe.getTags() != null && !recipe.getTags().isEmpty() ? "  <font color='gray' style='font-size: 150%;'>(" + String.join(", ", recipe.getTags()) + ")</font>" : "") +
                "</html>";
        JLabel nameLabel = new JLabel(nameAndTagsHtml);

        JTextArea ingredientsArea = new JTextArea(
                recipe.getIngredients().stream().map(Ingredient::toString).collect(Collectors.joining("\n"))
        );
        ingredientsArea.setEditable(false);
        ingredientsArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);
        JScrollPane ingredientsScrollPane = new JScrollPane(ingredientsArea);
        ingredientsScrollPane.setPreferredSize(new Dimension(200, 80));
        ingredientsScrollPane.setBorder(BorderFactory.createEmptyBorder());

        JLabel viewsLabel = new JLabel(recipe.getViews() + " views");
        viewsLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));

        itemPanel.add(imageLabel);
        itemPanel.add(Box.createHorizontalStrut(20));
        itemPanel.add(nameLabel);
        itemPanel.add(Box.createHorizontalGlue());
        itemPanel.add(ingredientsScrollPane);
        itemPanel.add(Box.createHorizontalStrut(20));
        itemPanel.add(viewsLabel);
        itemPanel.add(Box.createHorizontalStrut(10));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        return itemPanel;
    }

    private void sortRecipes(List<Recipe> recipesToSort) {
        RecipeSearchState currentState = recipeSearchViewModel.getState();
        String sortBy = (String) sortByComboBox.getSelectedItem();
        boolean ascending = ascendingCheckBox.isSelected();

        Comparator<Recipe> comparator = null;
        if ("Views".equals(sortBy)) {
            comparator = Comparator.comparingInt(Recipe::getViews);
        } else if ("Name".equals(sortBy)) {
            comparator = Comparator.comparing(Recipe::getTitle, String.CASE_INSENSITIVE_ORDER);
        } else if ("# of Ingredients".equals(sortBy)) {
            comparator = Comparator.comparingInt(r -> r.getIngredients().size());
        }

        if (comparator != null) {
            if (!ascending) {
                comparator = comparator.reversed();
            }
            recipesToSort.sort(comparator);
        }
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == searchButton) {
            String name = nameTextField.getText();
            String category = (String) categoryComboBox.getSelectedItem();

            if (name.isEmpty() && "All".equals(category)) {
                resultsPanel.removeAll();
                resultsPanel.setLayout(new GridBagLayout());
                JLabel refineLabel = new JLabel("Please refine your search query.");
                refineLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
                refineLabel.setForeground(Color.GRAY);
                resultsPanel.add(refineLabel);
                resultsPanel.revalidate();
                resultsPanel.repaint();
                return;
            }

            resultsPanel.removeAll();
            resultsPanel.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            loadingLabel = new JLabel("Loading search results...");
            loadingLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            loadingLabel.setForeground(Color.GRAY);
            resultsPanel.add(loadingLabel, gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(10, 0, 0, 0);
            progressBar = new JProgressBar(0, 100);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
            progressBar.setVisible(false);
            resultsPanel.add(progressBar, gbc);

            resultsPanel.revalidate();
            resultsPanel.repaint();

            if ("All".equals(category)) {
                category = "";
            }
            recipeSearchController.execute(name, category);

        } else if (evt.getSource() == sortByComboBox || evt.getSource() == ascendingCheckBox) {
            List<Recipe> currentRecipes = new ArrayList<>(recipeSearchViewModel.getState().getRecipeList());
            sortRecipes(currentRecipes);
            updateView(currentRecipes);

        } else if (evt.getSource() == signupButton) {
            viewManagerModel.setState("sign up");
            viewManagerModel.firePropertyChange();

        } else if (evt.getSource() == loginButton) {
            viewManagerModel.setState("log in");
            viewManagerModel.firePropertyChange();

        } else if (evt.getSource() == logoutButton) {
            RecipeSearchState currentState = recipeSearchViewModel.getState();
            currentState.setCurrentUser(null);
            EditReviewState editReviewState = editReviewViewModel.getState();
            editReviewState.setCurrentUser("Anonymous");
            recipeSearchViewModel.firePropertyChange();

        } else if (evt.getSource() == savedRecipesButton) {
            String name = recipeSearchViewModel.getState().getCurrentUser();
            showSavedRecipesController.execute(name);

        } else if (evt.getSource() == postRecipeButton) {
            viewManagerModel.setState("post recipe");
            viewManagerModel.firePropertyChange();

        } else if (evt.getSource() == recommendButton) {
            String username = recipeSearchViewModel.getState().getCurrentUser();
            if (username != null) {
                recommendRecipeController.execute(username);
            } else {
                JOptionPane.showMessageDialog(this, "Please log in first!");
            }
        }
    }

    private void updateUserRelatedUI(RecipeSearchState state) {
        if (state.getCurrentUser() != null && !state.getCurrentUser().isEmpty()) {
            currentUserLabel.setText("<html>Signed in as <font color='blue'>" + state.getCurrentUser() + "</font></html>");

            signupButton.setVisible(false);
            loginButton.setVisible(false);
            logoutButton.setVisible(true);

            postRecipeButton.setEnabled(true);
            savedRecipesButton.setEnabled(true);
            recommendButton.setVisible(true);
        } else {
            currentUserLabel.setText("Not signed in");

            signupButton.setVisible(true);
            loginButton.setVisible(true);
            logoutButton.setVisible(false);

            postRecipeButton.setEnabled(false);
            savedRecipesButton.setEnabled(false);
            recommendButton.setVisible(false);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            RecipeSearchState state = (RecipeSearchState) evt.getNewValue();
            updateUserRelatedUI(state);

            if (state.getSearchError() != null && !state.getSearchError().isEmpty()) {
                resultsPanel.removeAll();
                resultsPanel.setLayout(new GridBagLayout());
                JLabel errorLabel = new JLabel(state.getSearchError());
                errorLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
                errorLabel.setForeground(Color.RED);
                resultsPanel.add(errorLabel);
                resultsPanel.revalidate();
                resultsPanel.repaint();
                if (progressBar != null) {
                    progressBar.setVisible(false);
                }
            } else {
                List<Recipe> recipesToDisplay = new ArrayList<>(state.getRecipeList());
                sortRecipes(recipesToDisplay);
                updateView(recipesToDisplay);
                if (progressBar != null) {
                    progressBar.setVisible(false);
                }
            }
        } else if ("progress".equals(evt.getPropertyName())) {
            RecipeSearchState state = (RecipeSearchState) evt.getNewValue();
            if (progressBar != null) {
                if (state.getTotalImageCount() > 0) {
                    progressBar.setVisible(true);
                    int progress = (int) ((double) state.getCurrentImageCount() / state.getTotalImageCount() * 100);
                    progressBar.setValue(progress);
                    if (loadingLabel != null) {
                        loadingLabel.setText("Loading search results...");
                    }
                } else {
                    progressBar.setVisible(false);
                }
            }
        }
    }
}