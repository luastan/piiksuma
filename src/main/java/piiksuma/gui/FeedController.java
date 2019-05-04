package piiksuma.gui;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import piiksuma.Post;
import piiksuma.User;
import piiksuma.Utilities.PiikLogger;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.posts.PostController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class FeedController implements Initializable {

    public StackPane noPostLabel;
    @FXML
    private JFXMasonryPane postMasonryPane;

    @FXML
    private ScrollPane postScrollPane;

    @FXML
    private Label superCoolLabel;

    private ObservableList<Post> feed;


    /**
     * Inits the window components
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize Feed view & controller
        feed = FXCollections.observableArrayList();

        // Registers itself in the ContextHandler
        ContextHandler.getContext().setFeedController(this);
        setUpFeedListener();

        try {
            updateFeed();
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            PiikLogger.getInstance().log(Level.SEVERE, "FeedControler -> initialize", e);
            Alert.newAlert().setHeading("Feed error").addText("Failure initializing the feed").addCloseButton().show();
        }
    }


    /**
     * Reloads feed retrieving last posts from  the database
     */
    public void updateFeed() throws PiikDatabaseException, PiikInvalidParameters {
        User currentUser = ContextHandler.getContext().getCurrentUser();
        feed.clear();
        feed.addAll(ApiFacade.getEntrypoint().getSearchFacade().getFeed(currentUser, 10, currentUser));
        noPostLabel.setVisible(feed.size() == 0);
    }


    /**
     * Code to be executed when the feed gets updated. Posts at the interface
     * have to be updated.
     * <p>
     * Recieves change performed to the list via {@link ListChangeListener#onChanged(ListChangeListener.Change)}
     */
    private void setUpFeedListener() {
        feed.addListener((ListChangeListener<? super Post>) change -> {
            postMasonryPane.getChildren().clear();
            feed.forEach(this::insertPost);
        });
    }

    /**
     * Inserts a post in the window
     * @param post
     */
    private void insertPost(Post post) {
        FXMLLoader postLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/post.fxml"));
        postLoader.setController(new PostController(post));
        try {
            postMasonryPane.getChildren().add(postLoader.load());
        } catch (IOException e) {
            PiikLogger.getInstance().log(Level.SEVERE, "FeedControler -> insertPost", e);
            Alert.newAlert().setHeading("Insert post error").addText("Failure inserting the post").addCloseButton().show();
        }
        postScrollPane.requestLayout();
        postScrollPane.requestFocus();
    }
}
