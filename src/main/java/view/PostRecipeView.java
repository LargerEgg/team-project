package view;

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

public class PostRecipeView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "postRecipeView";
    private final PostRecipeViewModel postRecipeViewModel;

    public PostRecipeView(PostRecipeViewModel postRecipeViewModel) {

    }
}
