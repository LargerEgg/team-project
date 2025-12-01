package interface_adapter.saved_recipes;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SavedRecipesViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private SavedRecipesState state = new SavedRecipesState();

    public SavedRecipesState getState() {
        return state;
    }
    public void setState(SavedRecipesState state) {
        this.state = state;
    }

    public void firePropertyChanged() {
        support.firePropertyChange("savedRecipes", null, state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
