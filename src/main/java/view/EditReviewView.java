package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.edit_review.EditReviewController;
import interface_adapter.edit_review.EditReviewState;
import interface_adapter.edit_review.EditReviewViewModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    private final ViewManagerModel viewManagerModel;

    private final EditReviewViewModel EditReviewViewModel;
    private final JTextField reviewInputField = new JTextField(15);
    private final JTextArea descriptionInputField = new JTextArea(10, 15);
    private final SpinnerModel ratingModel = new SpinnerNumberModel(5, 1, 5, 1);
    private final JSpinner ratingInputField = new JSpinner(ratingModel);
    private EditReviewController EditReviewController = null;

    private final JButton publish;
    private final JButton backButton;

    public EditReviewView(EditReviewViewModel editReviewViewModel, ViewManagerModel viewManagerModel) {
        this.EditReviewViewModel = editReviewViewModel;
        this.viewManagerModel = viewManagerModel;
        editReviewViewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel(editReviewViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        final LabelTextPanel reviewInfo = new LabelTextPanel(
                new JLabel(editReviewViewModel.REVIEW_LABEL), reviewInputField);
        final LabelTextPanel descriptionInfo = new LabelTextPanel(
                new JLabel(editReviewViewModel.DESCRIPTION_LABEL), descriptionInputField);
        final LabelTextPanel ratingInfo = new LabelTextPanel(
                new JLabel(editReviewViewModel.RATING_LABEL), ratingInputField);

        final JPanel buttons = new JPanel();
        backButton = new JButton(editReviewViewModel.BACK_BUTTON_LABEL);
        buttons.add(backButton);
        publish = new JButton(editReviewViewModel.PUBLISH_BUTTON_LABEL);
        buttons.add(publish);

        publish.addActionListener(
                // This creates an anonymous subclass of ActionListener and instantiates it.
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (evt.getSource().equals(publish)) {
                            final EditReviewState currentState = editReviewViewModel.getState();

                            EditReviewController.execute(
                                    currentState.getReview(),
                                    currentState.getDescription(),
                                    currentState.getRating(),
                                    currentState.getAuthorId(),
                                    currentState.getRecipeId()
                            );
                        }
                    }
                }
        );

        backButton.addActionListener(this);

        addReviewListener();
        addDescriptionListener();
        addRatingListener();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(title);
        this.add(reviewInfo);
        this.add(descriptionInfo);
        this.add(ratingInfo);
        this.add(buttons);
    }

    private void addReviewListener() {
        reviewInputField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final EditReviewState currentState = EditReviewViewModel.getState();
                currentState.setReview(reviewInputField.getText());
                EditReviewViewModel.setState(currentState);
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
        descriptionInputField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final EditReviewState currentState = EditReviewViewModel.getState();
                currentState.setDescription(new String(descriptionInputField.getText()));
                EditReviewViewModel.setState(currentState);
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
        ratingInputField.getModel().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                changeListenerHelper();
            }

            private void changeListenerHelper() {
                final EditReviewState currentState = EditReviewViewModel.getState();
                currentState.setRating((Integer) ratingInputField.getValue());
                EditReviewViewModel.setState(currentState);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == backButton) {
            viewManagerModel.setState("view recipe");
            viewManagerModel.firePropertyChange();
        }
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
        this.EditReviewController = controller;
    }
}
