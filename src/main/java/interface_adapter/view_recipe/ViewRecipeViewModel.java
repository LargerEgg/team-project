package interface_adapter.view_recipe;

import interface_adapter.ViewModel;

public class ViewRecipeViewModel extends ViewModel<ViewRecipeState> {

    public ViewRecipeViewModel() {
        super("view recipe");
        setState(new ViewRecipeState());
    }

    @Override
    public ViewRecipeState getState() {
        return super.getState();
    }
}

