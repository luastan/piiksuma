package piiksuma.gui.hashtag;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import piiksuma.Hashtag;
import piiksuma.Post;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;
import piiksuma.gui.posts.PostController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HashtagController implements Initializable {

    private Hashtag hashtag;

    @FXML
    private Label name;
    @FXML
    private JFXButton follow;
    @FXML
    private ScrollPane postScroll;
    @FXML
    private JFXMasonryPane postMasonry;


    public HashtagController(Hashtag hashtag) {
        this.hashtag = hashtag;
    }

    /**
     * Init the window components
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name.setText(hashtag.getName());
        update();
    }

    /**
     * Requests the post feed to be updated
     */
    public void update() {
        try {
            ApiFacade.getEntrypoint().getSearchFacade()
                    .getPost(hashtag, ContextHandler.getContext().getCurrentUser()).forEach(this::addPost);
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert();
        }
        updateButtonState();
    }

    // Adds a post to the pane
    private void addPost(Post post) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/post.fxml"));
        loader.setController(new PostController(post));
        try {
            postMasonry.getChildren().add(loader.load());
        } catch (IOException e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    /**
     * Update the function of the button depending on the window state
     */
    private void updateButtonState() {
        try {
            if (ApiFacade.getEntrypoint().getSearchFacade().userFollowsHashtag(hashtag, ContextHandler.getContext().getCurrentUser())) {
                follow.setText("UnFollow");
                follow.setStyle("-fx-background-color: -primary-color-2; -fx-text-fill: -black-high-emphasis");
                follow.setOnAction(this::unFollowHashtag);
            } else {
                follow.setText("Follow");
                follow.setStyle("-fx-background-color: -primary-color-5");
                follow.setOnAction(this::followHashtag);
            }
        } catch (PiikDatabaseException e) {
            e.showAlert();
        }
    }

    /**
     * Function to follow a Hashtag
     * @param event Event on the window
     */
    private void followHashtag(Event event) {
        try {
            ApiFacade.getEntrypoint().getInsertionFacade().followHastag(hashtag, ContextHandler.getContext().getCurrentUser());
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert();
        }
    }

    /**
     * Function to unfollow a Hashtag
     * @param event Event on the window
     */
    private void unFollowHashtag(Event event) {
        try {
            ApiFacade.getEntrypoint().getDeletionFacade().unfollowHastag(hashtag, ContextHandler.getContext().getCurrentUser());
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert();
        }
    }
}
