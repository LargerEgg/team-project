package interface_adapter.view_recipe;

import interface_adapter.ViewModel;
import entity.Recipe;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ViewRecipeViewModel extends ViewModel {
    public static final String TITLE_LABEL = "View Recipe";
    private ViewRecipeState state = new ViewRecipeState();
    private String currentUser;

    public ViewRecipeViewModel() {
        super("view recipe");
    }

    public String getCurrentUser() {return currentUser;}
    public void setCurrentUser(String currentUser) {this.currentUser = currentUser;}

    public void setState(ViewRecipeState state) {
        this.state = state;
    }

    public ViewRecipeState getState() {
        return state;
    }

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    @Override
    public void firePropertyChange() {
        support.firePropertyChange("state", null, this.state);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
