package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.edit_review.EditReviewController;
import interface_adapter.edit_review.EditReviewState;
import interface_adapter.edit_review.EditReviewViewModel;
import use_case.edit_review.EditReviewInputData;
import use_case.post_recipe.PostRecipeInputData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * The View for the Edit Review Use Case.
 */
public class EditReviewView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "edit review";
    private final EditReviewViewModel editReviewViewModel;
    private EditReviewController editReviewController;
    private final ViewManagerModel viewManagerModel;

    private JTextField titleField;
    private JTextArea descriptionField = new JTextArea(10, 15);
    private final SpinnerModel ratingModel = new SpinnerNumberModel(5, 1, 5, 1);
    private JSpinner ratingField;

    private JButton publishButton;
    private JButton backButton;

    private JLabel messageLabel;

    public EditReviewView(EditReviewViewModel editReviewViewModel, ViewManagerModel viewManagerModel) {
        this.editReviewViewModel = editReviewViewModel;
        editReviewViewModel.addPropertyChangeListener(this);
        this.viewManagerModel = viewManagerModel;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel(interface_adapter.edit_review.EditReviewViewModel.TITLE_LABEL);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

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
        panel.add(new JLabel("Review Title:"), gbc);

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
        descriptionField = new JTextArea(5, 30);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionField);
        panel.add(descScrollPane, gbc);

        row++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Rating:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.CENTER;
        ratingField = new JSpinner(ratingModel);
        ratingField.setMinimumSize(new Dimension(100, 20));
        ( (JSpinner.DefaultEditor) ratingField.getEditor()).getTextField().setEnabled(false);
        panel.add(ratingField, gbc);

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
        backButton = new JButton("Back");

        publishButton.addActionListener(this);
        backButton.addActionListener(this);

        panel.add(publishButton);
        panel.add(backButton);

        return panel;
    }

    private void addReviewListener() {
        titleField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final EditReviewState currentState = editReviewViewModel.getState();
                currentState.setReview(titleField.getText());
                editReviewViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentListenerHelper();
            }
        });
    }

    private void addDescriptionListener() {
        descriptionField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final EditReviewState currentState = editReviewViewModel.getState();
                currentState.setDescription(new String(descriptionField.getText()));
                editReviewViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentListenerHelper();
            }
        });
    }

    private void addRatingListener() {
        ratingField.getModel().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                changeListenerHelper();
            }

            private void changeListenerHelper() {
                final EditReviewState currentState = editReviewViewModel.getState();
                currentState.setRating((Integer) ratingField.getValue());
                editReviewViewModel.setState(currentState);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == backButton) {
            clearForm();
            viewManagerModel.setState("view recipe");
            viewManagerModel.firePropertyChange();
        }
        else if (evt.getSource() == publishButton) {
            EditReviewInputData inputData = createInputDataFromForm();
            if (editReviewController != null) {
                editReviewController.publish(inputData);
            }
        }
    }

    private EditReviewInputData createInputDataFromForm() {
        String authorId = "current-user-id"; // TODO: Get from logged-in user
        String recipeId = "current-recipe-id"; // TODO: get from somewhere
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        int rating = (int) ratingField.getValue();


        return new EditReviewInputData(title, description, rating, authorId, recipeId);
    }

    private void clearForm() {
        titleField.setText("");
        descriptionField.setText("");
        ratingField.setValue(5);
        messageLabel.setText("");
        messageLabel.setForeground(Color.BLACK);
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final EditReviewState state = (EditReviewState) evt.getNewValue();
        if (state.getDescriptionError() != null && !state.getDescriptionError().isEmpty()) {
            JOptionPane.showMessageDialog(this, state.getDescriptionError());
        }
        else if (state.getReviewError() != null && !state.getReviewError().isEmpty()) {
            JOptionPane.showMessageDialog(this, state.getReviewError());
        }
        else if (state.getRatingError() != null && !state.getRatingError().isEmpty()) {
            JOptionPane.showMessageDialog(this, state.getRatingError());
        }

    }

    public String getViewName() {
        return viewName;
    }

    public void setEditReviewController(EditReviewController controller) {
        this.editReviewController = controller;
    }
}
