package interface_adapter.edit_review;

import interface_adapter.ViewModel;
import interface_adapter.recipe_search.RecipeSearchState;

/**
 * The ViewModel for the Edit Reviews View.
 */
public class EditReviewViewModel extends ViewModel<EditReviewState> {

    public static final String TITLE_LABEL = "Post Review";

    public EditReviewViewModel() {
        super("edit review");
        setState(new EditReviewState());
    }

    public EditReviewState getState() {
        return super.getState();
    }

}
