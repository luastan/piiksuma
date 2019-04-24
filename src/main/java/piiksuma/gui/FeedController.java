package piiksuma.gui;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import piiksuma.Post;

import java.net.URL;
import java.util.ResourceBundle;

public class FeedController implements Initializable {

    @FXML
    private JFXMasonryPane postMasonryPane;

    @FXML
    private ScrollPane postScrollPane;

    @FXML
    private Label superCoolLabel;

    private ObservableList<Post> feed;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize Feed view & controller

        // Registers itself in the ContextHandler
        ContextHandler.getContext().setFeedController(this);
    }


    /**
     * Reloads feed retrieving last posts from  the database
     */
    public void updateFeed() {
        // TODO: update the feed
    }

    /**
     * Code to be executed when the feed gets updated. Posts at the interface
     * have to be updated.
     * <p>
     * Recieves change performed to the list via {@link ListChangeListener#onChanged(ListChangeListener.Change)}
     */
    void setUpFeedListener() {
        feed.addListener((ListChangeListener<? super Post>) change -> {
            // TODO: Define actions to be taken when the feed gets updated
        });
    }
}
