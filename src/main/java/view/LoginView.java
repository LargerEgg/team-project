package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoginView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "log in";
    private final LoginViewModel loginViewModel;
    private final ViewManagerModel viewManagerModel;

    private final JTextField usernameInputField = new JTextField(15);
    private final JLabel usernameErrorField = new JLabel();

    private final JPasswordField passwordInputField = new JPasswordField(15);
    private final JLabel passwordErrorField = new JLabel();

    private final JButton logIn;
    private final JButton cancel;
    private LoginController loginController = null;

    public LoginView(LoginViewModel loginViewModel, ViewManagerModel viewManagerModel) {
        this.loginViewModel = loginViewModel;
        this.viewManagerModel = viewManagerModel;
        this.loginViewModel.addPropertyChangeListener(this);

        JLabel title = new JLabel("Login Screen");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Group username input and its error field
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.Y_AXIS));
        usernamePanel.add(new LabelTextPanel(new JLabel("Username"), usernameInputField));
        usernamePanel.add(usernameErrorField);
        usernameErrorField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameErrorField.setForeground(Color.RED); // Make error text red
        usernamePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, usernamePanel.getPreferredSize().height)); // Limit vertical expansion

        // Group password input and its error field
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.add(new LabelTextPanel(new JLabel("Password"), passwordInputField));
        passwordPanel.add(passwordErrorField);
        passwordErrorField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordErrorField.setForeground(Color.RED); // Make error text red
        passwordPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, passwordPanel.getPreferredSize().height)); // Limit vertical expansion

        JPanel buttons = new JPanel();
        logIn = new JButton("Log in");
        buttons.add(logIn);
        cancel = new JButton("Cancel");
        buttons.add(cancel);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, buttons.getPreferredSize().height)); // Limit vertical expansion


        logIn.addActionListener(e -> {
            if (e.getSource().equals(logIn)) {
                LoginState currentState = loginViewModel.getState();
                loginController.execute(currentState.getUsername(), currentState.getPassword());
            }
        });

        cancel.addActionListener(this);

        usernameInputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateState(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateState(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateState(); }
            private void updateState() {
                LoginState currentState = loginViewModel.getState();
                currentState.setUsername(usernameInputField.getText());
                loginViewModel.setState(currentState);
            }
        });

        passwordInputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateState(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateState(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateState(); }
            private void updateState() {
                LoginState currentState = loginViewModel.getState();
                currentState.setPassword(new String(passwordInputField.getPassword()));
                loginViewModel.setState(currentState);
            }
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(title);
        this.add(Box.createVerticalStrut(10)); // Add some spacing
        this.add(usernamePanel);
        this.add(Box.createVerticalStrut(5)); // Add some spacing
        this.add(passwordPanel);
        this.add(Box.createVerticalStrut(10)); // Add some spacing
        this.add(buttons);

        // Initial state for error fields
        usernameErrorField.setText("");
        passwordErrorField.setText("");
        setMinimumErrorFieldHeight(usernameErrorField);
        setMinimumErrorFieldHeight(passwordErrorField);
    }

    private void setMinimumErrorFieldHeight(JLabel errorField) {
        // Set a small fixed height for error fields when empty to reduce gap
        Dimension minSize = new Dimension(0, 15); // Adjust height as needed
        errorField.setMinimumSize(minSize);
        errorField.setPreferredSize(minSize);
        errorField.setMaximumSize(minSize);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == cancel) {
            viewManagerModel.setState("recipe search");
            viewManagerModel.firePropertyChange();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        LoginState state = (LoginState) evt.getNewValue();
        setFields(state);
        if (state.getLoginError() != null && !state.getLoginError().isEmpty()) {
            passwordErrorField.setText(state.getLoginError());
            passwordErrorField.setMaximumSize(new Dimension(Integer.MAX_VALUE, passwordErrorField.getPreferredSize().height)); // Allow it to grow
        } else {
            passwordErrorField.setText("");
            setMinimumErrorFieldHeight(passwordErrorField); // Shrink when empty
        }
    }

    private void setFields(LoginState state) {
        usernameInputField.setText(state.getUsername());
    }

    public String getViewName() {
        return viewName;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}
