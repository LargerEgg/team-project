package view;

import entity.Ingredient;
import entity.Recipe;
import entity.Review;
import interface_adapter.ViewManagerModel;
import entity.PopularityCalculator;
import interface_adapter.save_recipe.SaveRecipeController;
import interface_adapter.save_recipe.SaveRecipeState;
import interface_adapter.save_recipe.SaveRecipeViewModel;
import interface_adapter.unsave_recipe.UnsaveRecipeController;
import interface_adapter.view_recipe.ViewRecipeState;
import interface_adapter.view_recipe.ViewRecipeViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/*
N.
 */
public class RecipeView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "view recipe";
    private final ViewRecipeViewModel viewRecipeViewModel;
    private final ViewManagerModel viewManagerModel;
    private final SaveRecipeController saveRecipeController;
    private final UnsaveRecipeController unsaveRecipeController;
    private final SaveRecipeViewModel saveRecipeViewModel;
    private String currentUser;

    private JButton saveButton;
    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel categoryLabel;
    private JLabel tagsLabel;
    private JLabel viewsSavesRatingLabel;
    private JLabel imageLabel;
    private JLabel savedStarLabel;
    private JLabel popularityLabel;
    private JTextArea descriptionArea; // Changed from JLabel to JTextArea
    private JScrollPane descriptionScrollPane; // New JScrollPane for description
    private JTextArea ingredientsArea;
    private JScrollPane ingredientsScrollPane;
    private JTextArea reviewsArea;
    private JScrollPane reviewsScrollPane;
    private JButton backButton;

    private JButton reviewButton;

    public RecipeView(ViewRecipeViewModel viewRecipeViewModel, ViewManagerModel viewManagerModel, SaveRecipeController saveRecipeController, SaveRecipeViewModel saveRecipeViewModel, UnsaveRecipeController unsaveRecipeController) {
        this.viewRecipeViewModel = viewRecipeViewModel;
        this.viewManagerModel = viewManagerModel;
        this.saveRecipeController = saveRecipeController;
        this.saveRecipeViewModel = saveRecipeViewModel;
        this.unsaveRecipeController = unsaveRecipeController;

        this.viewRecipeViewModel.addPropertyChangeListener(this);
        this.saveRecipeViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header Panel (Title and Back Button)
        JPanel headerPanel = new JPanel(new BorderLayout());
        titleLabel = new JLabel("Recipe Title", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        backButton = new JButton("Back");
        backButton.addActionListener(this);

        reviewButton = new JButton("Review");
        reviewButton.addActionListener(this);

        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButtonPanel.add(backButton);
        backButtonPanel.add(reviewButton);
        headerPanel.add(backButtonPanel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel using GridBagLayout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        // Image (Top-Left)
        JPanel imageContainer = new JPanel(null);
        imageContainer.setPreferredSize(new Dimension(400, 300));
        imageContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        imageLabel = new JLabel();
        imageLabel.setBounds(0, 0, 400, 300);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        savedStarLabel = new JLabel("⭐");
        savedStarLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        savedStarLabel.setForeground(Color.YELLOW);
        savedStarLabel.setBounds(360, 10, 40, 40);
        savedStarLabel.setVisible(false);

        imageContainer.add(imageLabel);
        imageContainer.add(savedStarLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(imageContainer, gbc);

        //imageLabel = new JLabel();
        //imageLabel.setPreferredSize(new Dimension(400, 300)); // Fixed size for image
        //imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        //gbc.gridx = 0;
        //gbc.gridy = 0;
        //gbc.gridwidth = 1;
        //gbc.gridheight = 1;
        //gbc.fill = GridBagConstraints.BOTH;
        //gbc.weightx = 0.5; // Give half width to image
        //gbc.weighty = 0.5; // Give some height to image area
        //contentPanel.add(imageLabel, gbc);

        // Top-Right Details Panel (Tags, Description, Ingredients)
        JPanel topRightDetailsPanel = new JPanel();
        topRightDetailsPanel.setLayout(new BoxLayout(topRightDetailsPanel, BoxLayout.Y_AXIS));
        topRightDetailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        tagsLabel = new JLabel("Tags: ");
        tagsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topRightDetailsPanel.add(tagsLabel);
        topRightDetailsPanel.add(Box.createVerticalStrut(10));

        // Description (JTextArea in JScrollPane)
        topRightDetailsPanel.add(new JLabel("Description:")); // Label for description
        descriptionArea = new JTextArea(7, 30); // Increased rows from 5 to 7
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionScrollPane.setPreferredSize(new Dimension(300, 140)); // Increased height from 100 to 140
        topRightDetailsPanel.add(descriptionScrollPane);
        topRightDetailsPanel.add(Box.createVerticalStrut(10));

        topRightDetailsPanel.add(new JLabel("Ingredients:"));
        ingredientsArea = new JTextArea(5, 30);
        ingredientsArea.setEditable(false);
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);
        ingredientsScrollPane = new JScrollPane(ingredientsArea);
        ingredientsScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        ingredientsScrollPane.setPreferredSize(new Dimension(300, 150)); // Set preferred size
        topRightDetailsPanel.add(ingredientsScrollPane);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5; // Give half width to details
        gbc.weighty = 0.5; // Give some height to details area
        contentPanel.add(topRightDetailsPanel, gbc);

        // Other Details (Author, Category, Views/Saves/Rating) - Below image and top-right details
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Span two columns
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0; // Don't grow vertically

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        saveButton = new JButton("Save Recipe");
        saveButton.addActionListener(this);
        infoPanel.add(saveButton);

        authorLabel = new JLabel("Author: ");
        authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(authorLabel);

        categoryLabel = new JLabel("Category: ");
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(categoryLabel);

        viewsSavesRatingLabel = new JLabel("Views: 0 | Saves: 0 | Rating: 0.0");
        viewsSavesRatingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(viewsSavesRatingLabel);
        infoPanel.add(Box.createVerticalStrut(10));

        contentPanel.add(infoPanel, gbc);

        // Reviews (Bottom, spanning columns)
        reviewsArea = new JTextArea(10, 30); // Increased rows for reviews
        reviewsArea.setEditable(false);
        reviewsArea.setLineWrap(true);
        reviewsArea.setWrapStyleWord(true);
        reviewsScrollPane = new JScrollPane(reviewsArea);
        reviewsScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        reviewsScrollPane.setPreferredSize(new Dimension(700, 200)); // Set preferred size
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span two columns
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5; // Allow reviews to take up remaining vertical space
        contentPanel.add(new JLabel("Reviews:"), gbc); // Add label for reviews
        gbc.gridy = 3; // Move to next row for the scroll pane
        contentPanel.add(reviewsScrollPane, gbc);


        JScrollPane mainScrollPane = new JScrollPane(contentPanel);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(mainScrollPane, BorderLayout.CENTER);
    }

    private void updateView(Recipe recipe) {

        boolean isSaved = viewRecipeViewModel.getState().getIsSaved();
        saveButton.setEnabled(true);

        if (isSaved) {
            saveButton.setText("Unsave Recipe");
            savedStarLabel.setVisible(true);
        } else {
            saveButton.setText("Save Recipe");
            savedStarLabel.setVisible(false);
        }

        if (recipe != null) {
            // Use PopularityCalculator to add hot label for popular recipes
            String hotPrefix = PopularityCalculator.isPopular(recipe) 
                    ? "<font color='red'><b>[Hot]</b></font> " 
                    : "";
            titleLabel.setText("<html>" + hotPrefix + recipe.getTitle() + "</html>");
            authorLabel.setText("Author: " + recipe.getAuthorId());
            categoryLabel.setText("Category: " + recipe.getCategory());
            
            // Only display tags if they exist
            if (recipe.getTags() != null && !recipe.getTags().isEmpty()) {
                tagsLabel.setText("Tags: " + String.join(", ", recipe.getTags()));
            } else {
                tagsLabel.setText("Tags: None");
            }

            viewsSavesRatingLabel.setText(String.format("Views: %d | Saves: %d | Rating: %.1f",
                    recipe.getViews(), recipe.getSaves(), recipe.getAverageRating()));

            descriptionArea.setText(recipe.getDescription()); // Set text directly, no HTML
            descriptionArea.setCaretPosition(0); // Scroll to top

            if (recipe.getImage() != null) {
                Image scaledImage = recipe.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                imageLabel.setIcon(null);
                imageLabel.setText("[No Image Available]");
            }

            ingredientsArea.setText(recipe.getIngredients().stream()
                    .map(Ingredient::toString)
                    .collect(Collectors.joining("\n")));
            ingredientsArea.setCaretPosition(0); // Scroll to top

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (recipe.getReviews() != null && !recipe.getReviews().isEmpty()) {
                reviewsArea.setText(recipe.getReviews().stream()
                        .map(review -> String.format("%s - Rating: %d/5 by %s on %s\nComment: %s",
                                review.getTitle(), review.getRating(), review.getAuthorId(), dateFormat.format(review.getDateCreated()), review.getDescription()))
                        .collect(Collectors.joining("\n\n")));
            } else {
                reviewsArea.setText("No reviews yet.");
            }
            reviewsArea.setCaretPosition(0); // Scroll to top

        } else {
            // Clear all fields if no recipe is set
            titleLabel.setText("No Recipe Selected");
            authorLabel.setText("Author: ");
            categoryLabel.setText("Category: ");
            tagsLabel.setText("Tags: ");
            viewsSavesRatingLabel.setText("Views: 0 | Saves: 0 | Rating: 0.0");
            descriptionArea.setText(""); // Clear description
            imageLabel.setIcon(null);
            imageLabel.setText("[No Image Available]");
            ingredientsArea.setText("");
            reviewsArea.setText("");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            if (currentUser == null || currentUser.isEmpty()) {
                JOptionPane.showMessageDialog(this, "You must be logged in to save recipes.");
                return;
            }

            Recipe recipe = viewRecipeViewModel.getState().getRecipe();
            boolean isSaved = viewRecipeViewModel.getState().getIsSaved();

            if (isSaved) {
                unsaveRecipeController.execute(currentUser, recipe);
            } else {
                saveRecipeController.execute(currentUser, recipe);
            }
        }



        if (e.getSource() == backButton) {
            viewManagerModel.setState("recipe search");
            viewManagerModel.firePropertyChange();
        }
        if (e.getSource() == reviewButton) {
            viewManagerModel.setState("edit review");
            viewManagerModel.firePropertyChange();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == viewRecipeViewModel && "state".equals(evt.getPropertyName())) {
            ViewRecipeState state = (ViewRecipeState) evt.getNewValue();
            if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
                JOptionPane.showMessageDialog(this, state.getErrorMessage());
            } else {
                currentUser = state.getCurrentUser();
                updateView(state.getRecipe());
            }
        }

        if (evt.getSource() == saveRecipeViewModel && "state".equals(evt.getPropertyName())) {
            SaveRecipeState saveState = (SaveRecipeState) evt.getNewValue();
            viewRecipeViewModel.getState().setIsSaved(saveState.isSaved());

            if (saveState.isSaved()) {
                saveButton.setText("Unsave Recipe");
                savedStarLabel.setVisible(true);
            } else {
                saveButton.setText("Save Recipe");
                savedStarLabel.setVisible(false);
            }
            saveButton.setEnabled(true);

            if (saveState.getMessage() != null) {
                JOptionPane.showMessageDialog(this, saveState.getMessage());
            }
        }
    }
}
