package view;

import entity.Ingredient;
import entity.Recipe;
import interface_adapter.recipe_search.RecipeSearchController;
import interface_adapter.recipe_search.RecipeSearchState;
import interface_adapter.recipe_search.RecipeSearchViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeSearchView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "recipe search";
    private final RecipeSearchViewModel recipeSearchViewModel;
    private final RecipeSearchController recipeSearchController;

    // Header components
    private JTextField nameTextField;
    private JTextField categoryTextField;
    private JComboBox<String> sortByComboBox;
    private JCheckBox ascendingCheckBox;
    private JButton searchButton;

    // Results components
    private JPanel resultsPanel;

    public RecipeSearchView(RecipeSearchViewModel recipeSearchViewModel, RecipeSearchController recipeSearchController) {
        this.recipeSearchViewModel = recipeSearchViewModel;
        this.recipeSearchController = recipeSearchController;
        this.recipeSearchViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createResultsScrollPane(), BorderLayout.CENTER);
    }

    public void updateView(List<Recipe> recipes) {
        resultsPanel.removeAll();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS)); // Reset layout

        if (recipes == null || recipes.isEmpty()) {
            resultsPanel.setLayout(new GridBagLayout()); // Center the message
            JLabel noResultsLabel = new JLabel("No search results found. Please modify your search query.");
            noResultsLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            noResultsLabel.setForeground(Color.GRAY);
            resultsPanel.add(noResultsLabel);
        } else {
            for (Recipe recipe : recipes) {
                resultsPanel.add(createRecipeItem(
                        recipe.getTitle(),
                        String.join(", ", recipe.getTags()),
                        recipe.getViews(),
                        recipe.getImage(),
                        recipe.getIngredients()
                ));
            }
        }
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel titleLabel = new JLabel("Recipe Search");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        headerPanel.add(titleLabel, gbc);

        // Name
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

        // Category
        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        headerPanel.add(new JLabel("Category:"), gbc);

        categoryTextField = new JTextField(15);
        gbc.gridx = 5;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(categoryTextField, gbc);

        // Sorting
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        headerPanel.add(new JLabel("Sort By:"), gbc);

        String[] sortOptions = {"Views", "Name", "# of Ingredients"};
        sortByComboBox = new JComboBox<>(sortOptions);
        sortByComboBox.addActionListener(this);
        gbc.gridx = 1;
        headerPanel.add(sortByComboBox, gbc);

        gbc.gridx = 3;
        headerPanel.add(new JLabel("Ascending?"), gbc);

        ascendingCheckBox = new JCheckBox();
        ascendingCheckBox.addActionListener(this);
        gbc.gridx = 4;
        headerPanel.add(ascendingCheckBox, gbc);

        // Search Button
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        headerPanel.add(searchButton, gbc);

        // Separator
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

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

    private JPanel createRecipeItem(String name, String tags, int views, BufferedImage image, List<Ingredient> ingredients) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.X_AXIS));
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(80, 80));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        if (image != null) {
            imageLabel.setIcon(new ImageIcon(image.getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        } else {
            imageLabel.setText("[IMG]");
        }

        String nameAndTagsHtml = "<html><b>" + name + "</b>" +
                (tags != null && !tags.isEmpty() ? " <font color='gray'>(" + tags + ")</font>" : "") +
                "</html>";
        JLabel nameLabel = new JLabel(nameAndTagsHtml);

        JTextArea ingredientsArea = new JTextArea(
                ingredients.stream().map(Ingredient::toString).collect(Collectors.joining("\n"))
        );
        ingredientsArea.setEditable(false);
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);
        JScrollPane ingredientsScrollPane = new JScrollPane(ingredientsArea);
        ingredientsScrollPane.setPreferredSize(new Dimension(200, 80));
        ingredientsScrollPane.setBorder(BorderFactory.createEmptyBorder());

        JLabel viewsLabel = new JLabel(views + " views");

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

    private void sortRecipes() {
        RecipeSearchState currentState = recipeSearchViewModel.getState();
        List<Recipe> recipeList = new ArrayList<>(currentState.getRecipeList());
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
            recipeList.sort(comparator);
            updateView(recipeList);
        }
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == searchButton) {
            // Show loading message
            resultsPanel.removeAll();
            resultsPanel.setLayout(new GridBagLayout());
            JLabel loadingLabel = new JLabel("Loading search results...");
            loadingLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            resultsPanel.add(loadingLabel);
            resultsPanel.revalidate();
            resultsPanel.repaint();

            // Execute search in background
            recipeSearchController.execute(nameTextField.getText(), categoryTextField.getText());
        } else if (evt.getSource() == sortByComboBox || evt.getSource() == ascendingCheckBox) {
            sortRecipes();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            RecipeSearchState state = (RecipeSearchState) evt.getNewValue();
            if (state.getSearchError() != null && !state.getSearchError().isEmpty()) {
                resultsPanel.removeAll();
                resultsPanel.setLayout(new GridBagLayout());
                JLabel errorLabel = new JLabel(state.getSearchError());
                errorLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
                errorLabel.setForeground(Color.RED);
                resultsPanel.add(errorLabel);
                resultsPanel.revalidate();
                resultsPanel.repaint();
            } else {
                updateView(state.getRecipeList());
            }
        }
    }
}
