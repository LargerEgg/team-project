package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.edit_review.EditReviewController;
import interface_adapter.edit_review.EditReviewState;
import interface_adapter.edit_review.EditReviewViewModel;
import use_case.edit_review.EditReviewInputData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
        this.editReviewViewModel.addPropertyChangeListener(this);
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
        gbc.fill = GridBagConstraints.NONE; // Changed from BOTH to NONE to prevent vertical stretching
        gbc.weighty = 0; // Changed from 0.1 to 0 to prevent vertical expansion
        gbc.anchor = GridBagConstraints.WEST;
        ratingField = new JSpinner(ratingModel);
        ratingField.setPreferredSize(new Dimension(60, 25)); // Set a preferred size for a compact look
        ((JSpinner.DefaultEditor) ratingField.getEditor()).getTextField().setEnabled(false);
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
        String authorId = editReviewViewModel.getState().getCurrentUser();
        String recipeId = editReviewViewModel.getState().getCurrentRecipe();
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
        final EditReviewState state = editReviewViewModel.getState();

        if ("success".equals(evt.getPropertyName())) {
            messageLabel.setText(state.getSuccessMessage());
            messageLabel.setForeground(new Color(0, 128, 0));
            JOptionPane.showMessageDialog(this, state.getSuccessMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        }
        else if ("error".equals(evt.getPropertyName())) {
            messageLabel.setText(state.getErrorMessage());
            messageLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, state.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setEditReviewController(EditReviewController controller) {
        this.editReviewController = controller;
    }
}