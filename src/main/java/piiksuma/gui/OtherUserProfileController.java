package piiksuma.gui;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import piiksuma.Post;
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OtherUserProfileController implements Initializable {

    @FXML
    private Text userName;
    @FXML
    private ScrollPane postScrollPane;

    @FXML
    private JFXMasonryPane postMasonryPane;
    private ObservableList<Post> feed;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        ContextHandler.getContext().getCurrentUser().setName("OswaldOswin");
        // userName.setText(ContextHandler.getContext().getCurrentUser().getName());
        feed = FXCollections.observableArrayList();

        ContextHandler.getContext().setOtherUserProfileController(this);
        setUpFeedListener();

        updateFeed();
    }

    public void updateFeed() {
        // TODO: update the feed propperly
        feed.clear();
        feed.addAll(new QueryMapper<Post>(ApiFacade.getEntrypoint().getConnection()).defineClass(Post.class).createQuery("SELECT * FROM post;").list());
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

    private void insertPost(Post post) {
        FXMLLoader postLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/post.fxml"));
        postLoader.setController(new PostController(post));
        try {
            postMasonryPane.getChildren().add(postLoader.load());
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        postScrollPane.requestLayout();
        postScrollPane.requestFocus();
    }
}
