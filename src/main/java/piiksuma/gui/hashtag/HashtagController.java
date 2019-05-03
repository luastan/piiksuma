package piiksuma.gui.hashtag;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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

    private void updateButtonState() {
//        if (ApiFacade.getEntrypoint().getInsertionFacade().followHastag();)
    }
}
