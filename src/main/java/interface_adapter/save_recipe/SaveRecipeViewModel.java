package interface_adapter.save_recipe;

import interface_adapter.ViewModel;

public class SaveRecipeViewModel extends ViewModel<SaveRecipeState> {

    public SaveRecipeViewModel() {
        super("save recipe");
        setState(new SaveRecipeState());
    }

    public SaveRecipeState getState() {
        return super.getState();
    }
}

