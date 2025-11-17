package interface_adapter.reviews;

import interface_adapter.ViewModel;

/**
 * The ViewModel for the Reviews View.
 */
public class ReviewsViewModel extends ViewModel<ReviewsState> {
    //Replace all this shit later
    public static final String TITLE_LABEL = "Reviews View";
    public static final String REVIEW_LABEL = "Title";
    public static final String DESCRIPTION_LABEL = "Description";
    public static final String RATING_LABEL = "Rating";

    public static final String PUBLISH_BUTTON_LABEL = "Publish";
    public static final String TO_REVIEWS_BUTTON_LABEL = "Exit";

    public ReviewsViewModel() {
        super("reviews");
        setState(new ReviewsState());
    }

}
