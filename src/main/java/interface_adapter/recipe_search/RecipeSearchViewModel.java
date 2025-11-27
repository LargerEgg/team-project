package interface_adapter.recipe_search;

import interface_adapter.ViewModel;

/**
 * The View Model for the Login View.
 */
public class RecipeSearchViewModel extends ViewModel<RecipeSearchState> {

    public RecipeSearchViewModel() {
        super("recipe search");
        setState(new RecipeSearchState());
    }

    public RecipeSearchState getState() {
        return super.getState();
    }

    public void firePropertyChanged() {
    }
}
