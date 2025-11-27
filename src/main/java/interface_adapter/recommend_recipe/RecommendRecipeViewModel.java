package interface_adapter.recommend_recipe;

import interface_adapter.ViewModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class RecommendRecipeViewModel extends ViewModel {

    public static final String TITLE_LABEL = "Recommend View";
    public static final String RECOMMEND_BUTTON_LABEL = "Get Recommendations";

    private RecommendRecipeState state = new RecommendRecipeState();

    public RecommendRecipeViewModel() {
        super("recommend recipe");
    }

    public void setState(RecommendRecipeState state) {
        this.state = state;
    }

    public RecommendRecipeState getState() {
        return state;
    }

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Alerts the View that the state has changed.
     */
    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this.state);
    }

    /**
     * Adds a listener to observe changes in this ViewModel.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}