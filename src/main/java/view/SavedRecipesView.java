package view;

import entity.Recipe;
import interface_adapter.ViewManagerModel;
import interface_adapter.saved_recipes.SavedRecipesViewModel;
import interface_adapter.saved_recipes.SavedRecipesState;
import interface_adapter.saved_recipes.ShowSavedRecipesController;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class SavedRecipesView extends JPanel implements PropertyChangeListener {
    public final String viewName = "Saved Recipes";

    private final SavedRecipesViewModel viewModel;
    private final ShowSavedRecipesController controller;
    private final ViewManagerModel viewManagerModel;

    private JLabel titleLable;
    private JButton backButton;

    private JPanel resultsPanel;
    private JScrollPane scrollPane;

    public SavedRecipesView(SavedRecipesViewModel viewModel, ShowSavedRecipesController controller, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.controller = controller;
        this.viewManagerModel = viewManagerModel;

        viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        add(createResultsScrollPane(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new GridLayout());
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        titleLable = new JLabel("Saved Recipes");
        titleLable.setFont(new Font("SansSerif", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor =  GridBagConstraints.CENTER;
        headerPanel.add(titleLable, gbc);

        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            viewManagerModel.setState("recipe search");
            viewManagerModel.firePropertyChange();
        });

        gbc.gridy = 1;
        headerPanel.add(backButton, gbc);

        return headerPanel;
    }

    private JScrollPane createResultsScrollPane() {
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.white);

        scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    private JPanel createRecipeItem(Recipe recipe) {

        JPanel item = new JPanel(new BorderLayout());
        item.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        item.setBackground(Color.WHITE);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(10, 10, 10, 10));
        center.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(recipe.getTitle());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel tagsLabel = new JLabel(recipe.getTags().toString());
        tagsLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        tagsLabel.setForeground(Color.GRAY);

        center.add(nameLabel);
        center.add(Box.createVerticalStrut(5));
        center.add(tagsLabel);

        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(80, 80));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        try {
            URL url = new URL(recipe.getImagePath());
            BufferedImage img = ImageIO.read(url);
            if (img != null) {
                Image scaled = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
            } else {
                imageLabel.setText("[IMG]");
            }
        } catch (IOException e) {
            imageLabel.setIcon(new ImageIcon("[IMG]"));
        }

        item.add(imageLabel, BorderLayout.WEST);
        item.add(center, BorderLayout.CENTER);

        JLabel viewsLabel = new JLabel(recipe.getViews() + " views");
        viewsLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        viewsLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        item.add(viewsLabel, BorderLayout.EAST);

        return item;
    }

    private void updateView(List<Recipe> recipes) {
        resultsPanel.removeAll();

        if (recipes != null) {
            for (Recipe recipe : recipes) {
                resultsPanel.add(createRecipeItem(recipe));
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SavedRecipesState state = (SavedRecipesState) evt.getNewValue();

        if (state.getError() != null) {
            JOptionPane.showMessageDialog(this, state.getError());
            return;
        }

        updateView(state.getRecipes());
    }

    public String getViewName() {
        return viewName;
    }

}
