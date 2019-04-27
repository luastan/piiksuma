package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXMasonryPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import piiksuma.Post;
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

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

    @FXML
    private JFXButton messageButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        ContextHandler.getContext().getCurrentUser().setName("OswaldOswin");
        // userName.setText(ContextHandler.getContext().getCurrentUser().getName());
        feed = FXCollections.observableArrayList();

        ContextHandler.getContext().setOtherUserProfileController(this);
        setUpFeedListener();

        try {
            updateFeed();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }
        messageButton.setOnAction(this::handleMessageButton);
    }

    public void updateFeed() throws PiikDatabaseException {
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
    private void handleMessageButton(Event event) {
        Stage searchStage = new Stage();

        try {
            ContextHandler.getContext().register("Start chat", searchStage);
        } catch (
                PiikInvalidParameters e) {
            e.printStackTrace();
            return;
        }
        // Stage configuration
        searchStage.setTitle("Start chat");
        searchStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/startChatFromProfile.fxml"));
        JFXDecorator decorator;

        try {
            decorator = new JFXDecorator(searchStage, loader.load(), false, false, true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(decorator, 400, 300);


        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm(),
                getClass().getResource("/gui/css/main.css").toExternalForm()
        );
        searchStage.initModality(Modality.WINDOW_MODAL);
        searchStage.initOwner(ContextHandler.getContext().getStage("primary"));
        searchStage.setScene(scene);
        // Show and wait till it closes
        searchStage.showAndWait();
    }
}
