package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.post_recipe.PostRecipeController;
import interface_adapter.post_recipe.PostRecipeState;
import interface_adapter.post_recipe.PostRecipeViewModel;
import use_case.post_recipe.PostRecipeInputData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostRecipeView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "post recipe";
    private final PostRecipeViewModel postRecipeViewModel;
    private PostRecipeController postRecipeController;
    private final ViewManagerModel viewManagerModel;

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextArea ingredientsArea;
    private JTextField categoryField;
    private JTextField tagsField;
    private JTextField imagePathField;
    private JButton publishButton;
    private JButton saveDraftButton;
    private JButton cancelButton;
    private JLabel messageLabel;

    public PostRecipeView(PostRecipeViewModel postRecipeViewModel, ViewManagerModel viewManagerModel) {
        this.postRecipeViewModel = postRecipeViewModel;
        this.postRecipeViewModel.addPropertyChangeListener(this);
        this.viewManagerModel = viewManagerModel;


        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Post Recipe");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel("Recipe Title:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        titleField = new JTextField(30);
        panel.add(titleField, gbc);

        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        panel.add(descScrollPane, gbc);

        row++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Ingredients:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        ingredientsArea = new JTextArea(5, 30);
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);
        JScrollPane ingScrollPane = new JScrollPane(ingredientsArea);
        panel.add(ingScrollPane, gbc);

        JLabel ingredientsHint = new JLabel("(Format: name,quantity per line. e.g., 'Chicken,500g')");
        ingredientsHint.setFont(new Font("SansSerif", Font.ITALIC, 10));
        gbc.gridy = row + 1;
        panel.add(ingredientsHint, gbc);

        row += 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        categoryField = new JTextField(30);
        panel.add(categoryField, gbc);

        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel("Tags:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        tagsField = new JTextField(30);
        panel.add(tagsField, gbc);

        JLabel tagsHint = new JLabel("(Comma-separated, e.g., 'spicy,asian,dinner')");
        tagsHint.setFont(new Font("SansSerif", Font.ITALIC, 10));
        gbc.gridy = row + 1;
        panel.add(tagsHint, gbc);

        row += 2;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel("Image URL:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        imagePathField = new JTextField(30);
        panel.add(imagePathField, gbc);

        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        messageLabel = new JLabel("");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(messageLabel, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        publishButton = new JButton("Publish");
        saveDraftButton = new JButton("Save Draft");
        cancelButton = new JButton("Cancel");

        publishButton.addActionListener(this);
        saveDraftButton.addActionListener(this);
        cancelButton.addActionListener(this);

        panel.add(publishButton);
        panel.add(saveDraftButton);
        panel.add(cancelButton);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == publishButton) {
            PostRecipeInputData inputData = createInputDataFromForm();
            if (postRecipeController != null) {
                postRecipeController.publish(inputData);
            }
        } else if (e.getSource() == saveDraftButton) {
            PostRecipeInputData inputData = createInputDataFromForm();
            if (postRecipeController != null) {
                postRecipeController.saveDraft(inputData);
            }
        } else if (e.getSource() == cancelButton) {
            clearForm();
            viewManagerModel.setState("recipe search");
            viewManagerModel.firePropertyChange();
        }
    }

    private PostRecipeInputData createInputDataFromForm() {
        String authorId = "current-user-id"; // TODO: Get from logged-in user
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String category = categoryField.getText().trim();
        String imagePath = imagePathField.getText().trim();

        List<PostRecipeInputData.IngredientDTO> ingredients = parseIngredients(ingredientsArea.getText());

        return new PostRecipeInputData(authorId, title, description, ingredients, category, imagePath);
    }

    // Function to allow comma parsing of ingredients
    private List<PostRecipeInputData.IngredientDTO> parseIngredients(String text) {
        List<PostRecipeInputData.IngredientDTO> ingredients = new ArrayList<>();
        String[] lines = text.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    String quantity = parts[1].trim();
                    ingredients.add(new PostRecipeInputData.IngredientDTO(name, quantity));
                } else if (parts.length == 1) {
                    ingredients.add(new PostRecipeInputData.IngredientDTO(parts[0].trim(), ""));
                }
            }
        }

        return ingredients;
    }

    private void clearForm() {
        titleField.setText("");
        descriptionArea.setText("");
        ingredientsArea.setText("");
        categoryField.setText("");
        tagsField.setText("");
        imagePathField.setText("");
        messageLabel.setText("");
        messageLabel.setForeground(Color.BLACK);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        PostRecipeState state = postRecipeViewModel.getState();

        if ("success".equals(evt.getPropertyName())) {
            messageLabel.setText(state.getSuccessMessage());
            messageLabel.setForeground(new Color(0, 128, 0));
            JOptionPane.showMessageDialog(this, state.getSuccessMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } else if ("draft_saved".equals(evt.getPropertyName())) {
            messageLabel.setText(state.getSuccessMessage());
            messageLabel.setForeground(new Color(0, 100, 200));
            JOptionPane.showMessageDialog(this, state.getSuccessMessage(), "Draft Saved", JOptionPane.INFORMATION_MESSAGE);
        } else if ("error".equals(evt.getPropertyName())) {
            messageLabel.setText(state.getErrorMessage());
            messageLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, state.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setPostRecipeController(PostRecipeController controller) {
        this.postRecipeController = controller;
    }

}
