package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXScrollPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import piiksuma.Message;
import piiksuma.Post;
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MessagesController implements Initializable {
    /*
    @FXML
    private ScrollPane messageScroll;

    @FXML
    private JFXMasonryPane messageMasonryPane;

    @FXML
    private JFXButton mainButton;

    private Message message;

    private ObservableList<Message> feed;

    public MessagesController(Message message) {
        this.message = message;
    }
*/
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*
        // Initialize Profile view & controller

        feed = FXCollections.observableArrayList();

        // Registers itself in the ContextHandler
        ContextHandler.getContext().setMessagesController(this);
        setUpFeedListener();

        updateFeed();
        */


    }

    /**
     * Reloads feed retrieving last posts from  the database
     */
    /*
    public void updateFeed() {
        // TODO: update the feed propperly
        feed.clear();
        feed.addAll(new QueryMapper<Message>(ApiFacade.getEntrypoint().getConnection()).defineClass(Message.class).createQuery("SELECT * FROM message;").list());
    }

    private void setUpFeedListener() {
        feed.addListener((ListChangeListener<? super Message>) change -> {
            messageMasonryPane.getChildren().clear();
            feed.forEach(this::insertMessage);
        });
    }

    private void insertMessage(Message message) {
        FXMLLoader messageLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/post.fxml"));
        messageLoader.setController(new MessagesController(message));
        try {
            messageMasonryPane.getChildren().add(messageLoader.load());
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        messageScroll.requestLayout();
        messageScroll.requestFocus();

     */
    }


