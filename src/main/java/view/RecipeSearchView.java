package view;

import entity.Ingredient;
import entity.Recipe;
import interface_adapter.recipe_search.RecipeSearchController;
import interface_adapter.recipe_search.RecipeSearchState;
import interface_adapter.recipe_search.RecipeSearchViewModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeSearchView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "recipe search";
    private final RecipeSearchViewModel recipeSearchViewModel;
    private final RecipeSearchController recipeSearchController;

    // Header components
    private JLabel titleLabel;
    private JLabel nameLabel;
    private JTextField nameTextField;
    private JLabel tagsLabel;
    private JTextField tagsTextField;
    private JLabel sortByLabel;
    private JComboBox<String> sortByComboBox;
    private JLabel ascendingLabel;
    private JCheckBox ascendingCheckBox;
    private JButton searchButton;
    private JSeparator separator;

    // Results components
    private JScrollPane resultsScrollPane;
    private JPanel resultsPanel;


    public RecipeSearchView(RecipeSearchViewModel recipeSearchViewModel, RecipeSearchController recipeSearchController) {
        this.recipeSearchViewModel = recipeSearchViewModel;
        this.recipeSearchController = recipeSearchController;
        this.recipeSearchViewModel.addPropertyChangeListener(this);

        // 1. Panel Setup
        setLayout(new BorderLayout());

        // 2. Create and add the Header Panel (North)
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 3. Create and add the Results Panel (Center with Scroll)
        add(createResultsScrollPane(), BorderLayout.CENTER);
    }

    /**
     * Updates the results panel with a new list of recipes.
     *
     * @param recipes The list of recipes to display.
     */
    public void updateView(List<Recipe> recipes) {
        resultsPanel.removeAll();
        if (recipes != null) {
            for (Recipe recipe : recipes) {
                resultsPanel.add(createRecipeItem(
                        recipe.getTitle(),
                        String.join(", ", recipe.getTags()),
                        recipe.getViews(),
                        recipe.getImagePath(),
                        recipe.getIngredients()
                ));
            }
        }
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }


    // --- Panel Creation Methods ---

    /**
     * Creates the top section containing the title, search inputs, and search button.
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new GridBagLayout());
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Title
        titleLabel = new JLabel("Recipe Search");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        headerPanel.add(titleLabel, gbc);

        // Row 1: Name and Tags
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        headerPanel.add(nameLabel, gbc);

        nameTextField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(nameTextField, gbc);

        tagsLabel = new JLabel("Tags:");
        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        headerPanel.add(tagsLabel, gbc);

        tagsTextField = new JTextField(15);
        gbc.gridx = 5;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(tagsTextField, gbc);

        // Row 2: Sort By, Ascending, and Search Button
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        sortByLabel = new JLabel("Sort By:");
        gbc.gridx = 0;
        headerPanel.add(sortByLabel, gbc);

        String[] sortOptions = {"Popularity", "Name", "Views"};
        sortByComboBox = new JComboBox<>(sortOptions);
        gbc.gridx = 1;
        headerPanel.add(sortByComboBox, gbc);

        ascendingLabel = new JLabel("Ascending?");
        gbc.gridx = 3;
        headerPanel.add(ascendingLabel, gbc);

        ascendingCheckBox = new JCheckBox();
        gbc.gridx = 4;
        headerPanel.add(ascendingCheckBox, gbc);

        // Search Button
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        headerPanel.add(searchButton, gbc);

        // Separator line
        separator = new JSeparator(SwingConstants.HORIZONTAL);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(separator, gbc);

        return headerPanel;
    }

    /**
     * Creates a JScrollPane containing the list of recipe results.
     */
    private JScrollPane createResultsScrollPane() {
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.WHITE);

        resultsScrollPane = new JScrollPane(resultsPanel);
        resultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resultsScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return resultsScrollPane;
    }

    /**
     * Creates a single panel representing a recipe result row.
     */
    private JPanel createRecipeItem(String name, String tags, int views, String imageUrl, List<Ingredient> ingredients) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.X_AXIS));
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Image
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(80, 80));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            URL url = new URL(imageUrl);
            BufferedImage image = ImageIO.read(url);
            if (image != null) {
                Image scaledImage = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                imageLabel.setText("[IMG]");
            }
        } catch (IOException e) {
            e.printStackTrace();
            imageLabel.setText("[IMG]");
        }

        // Name and Tags
        String nameAndTagsHtml;
        if (tags != null && !tags.isEmpty()) {
            nameAndTagsHtml = "<html><b>" + name + "</b> <font color='gray'>(" + tags + ")</font></html>";
        } else {
            nameAndTagsHtml = "<html><b>" + name + "</b></html>";
        }
        JLabel nameLabel = new JLabel(nameAndTagsHtml);

        // Ingredients
        JTextArea ingredientsArea = new JTextArea(
                ingredients.stream().map(Ingredient::toString).collect(Collectors.joining("\n"))
        );
        ingredientsArea.setEditable(false);
        ingredientsArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);
        JScrollPane ingredientsScrollPane = new JScrollPane(ingredientsArea);
        ingredientsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        ingredientsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        ingredientsScrollPane.setPreferredSize(new Dimension(200, 80));
        ingredientsScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Views
        JLabel viewsLabel = new JLabel(views + " views");
        viewsLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));

        // Add components to panel
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

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == searchButton) {
            recipeSearchController.execute(nameTextField.getText());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            RecipeSearchState state = (RecipeSearchState) evt.getNewValue();
            if (state.getSearchError() != null && !state.getSearchError().isEmpty()) {
                JOptionPane.showMessageDialog(this, state.getSearchError());
            } else {
                updateView(state.getRecipeList());
            }
        }
    }

    public String getViewName() {
        return viewName;
    }
}
