package interface_adapter.save_recipe;

import interface_adapter.ViewModel;

public class SaveRecipeViewModel extends ViewModel<SaveRecipeState> {

    public SaveRecipeViewModel() {
        super("Save Recipe");
        setState(new SaveRecipeState());
    }

}
