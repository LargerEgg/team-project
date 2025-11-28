package interface_adapter.edit_review;

import interface_adapter.ViewModel;

/**
 * The ViewModel for the Edit Reviews View.
 */
public class EditReviewViewModel extends ViewModel<EditReviewState> {

    public static final String TITLE_LABEL = "Post Review";
    public static final String REVIEW_LABEL = "Review Title:";
    public static final String DESCRIPTION_LABEL = "Description";
    public static final String RATING_LABEL = "Rating";

    public static final String PUBLISH_BUTTON_LABEL = "Publish";
    public static final String BACK_BUTTON_LABEL = "Back";

    public EditReviewViewModel() {
        super("edit review");
        setState(new EditReviewState());
    }

}
