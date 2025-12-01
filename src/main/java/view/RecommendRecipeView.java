package view;

import entity.Recipe;
import interface_adapter.ViewManagerModel;
import interface_adapter.recommend_recipe.RecommendRecipeState;
import interface_adapter.recommend_recipe.RecommendRecipeViewModel;
import interface_adapter.view_recipe.ViewRecipeController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/*
N.
 */
public class RecommendRecipeView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "recommend recipe";

    private final RecommendRecipeViewModel recommendRecipeViewModel;
    private final ViewManagerModel viewManagerModel;
    private final ViewRecipeController viewRecipeController;

    private final JPanel recipesPanel;
    private final JButton backButton;

    public RecommendRecipeView(RecommendRecipeViewModel recommendRecipeViewModel,
                               ViewManagerModel viewManagerModel,
                               ViewRecipeController viewRecipeController) {
        this.recommendRecipeViewModel = recommendRecipeViewModel;
        this.viewManagerModel = viewManagerModel;
        this.viewRecipeController = viewRecipeController;

        this.recommendRecipeViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Recommended for You");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(title, BorderLayout.CENTER);

        backButton = new JButton("Back");
        backButton.addActionListener(this);
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButtonPanel.add(backButton);
        headerPanel.add(backButtonPanel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        recipesPanel = new JPanel();
        recipesPanel.setLayout(new BoxLayout(recipesPanel, BoxLayout.Y_AXIS));
        recipesPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(recipesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Updates the UI when the ViewModel state changes.
     * @param evt A PropertyChangeEvent object describing the event source
     * and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        RecommendRecipeState state = (RecommendRecipeState) evt.getNewValue();

        if (state.getRecommendationError() != null) {
            JOptionPane.showMessageDialog(this, state.getRecommendationError());
            state.setRecommendationError(null);
            return;
        }

        if (state.getRecommendations() != null) {
            updateRecipeList(state.getRecommendations());
        }
    }

    private void updateRecipeList(List<Recipe> recipes) {
        recipesPanel.removeAll();

        if (recipes.isEmpty()) {
            JLabel noDataLabel = new JLabel("No recommendations found.");
            noDataLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            recipesPanel.add(Box.createVerticalStrut(20));
            recipesPanel.add(noDataLabel);
        } else {
            for (Recipe recipe : recipes) {
                recipesPanel.add(createRecipeItem(recipe));
                recipesPanel.add(Box.createVerticalStrut(10));
            }
        }

        recipesPanel.revalidate();
        recipesPanel.repaint();
    }

    /**
     * Creates a display panel for a single recipe.
     * @param recipe The recipe to display.
     * @return A JPanel containing recipe information.
     */
    private JPanel createRecipeItem(Recipe recipe) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.X_AXIS));
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    viewRecipeController.execute(recipe, recipe.getRecipeId());
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                itemPanel.setBackground(new Color(240, 240, 240));
                itemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                itemPanel.setBackground(Color.WHITE);
                itemPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(80, 80));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        if (recipe.getImage() != null) {
            imageLabel.setIcon(new ImageIcon(recipe.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        } else {
            imageLabel.setText("No IMG");
        }

        String htmlText = String.format("<html><b style='font-size:14px;'>%s</b><br/><span style='color:gray;'>%s</span></html>",
                recipe.getTitle(), recipe.getCategory());
        JLabel textLabel = new JLabel(htmlText);

        itemPanel.add(imageLabel);
        itemPanel.add(Box.createHorizontalStrut(15));
        itemPanel.add(textLabel);
        itemPanel.add(Box.createHorizontalGlue());

        return itemPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            viewManagerModel.setState("recipe search");
            viewManagerModel.firePropertyChange();
        }
    }
}