package interface_adapter.post_recipe;

import interface_adapter.ViewModel;

public class PostRecipeViewModel extends ViewModel<PostRecipeState> {

    public PostRecipeViewModel() {
        super("post recipe");
        setState(new PostRecipeState());
    }
}
