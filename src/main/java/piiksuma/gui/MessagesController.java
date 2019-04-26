package piiksuma.gui;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import piiksuma.Event;
import piiksuma.Message;
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MessagesController implements Initializable {

    @FXML
    private ScrollPane messageScrollPane;
    @FXML
    private JFXMasonryPane messageMasonryPane;

    private ObservableList<Message> messagesFeed;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messagesFeed = FXCollections.observableArrayList();

        ContextHandler.getContext().setMessagesController(this);
        setUpFeedListener();

        updateMessageFeed();
    }

    private void updateMessageFeed() {

        messagesFeed.clear();
        messagesFeed.addAll(new QueryMapper<Message>(ApiFacade.getEntrypoint().getConnection()).defineClass(Message.class).createQuery("SELECT * FROM message;").list());

    }

    private void setUpFeedListener() {
        messagesFeed.addListener((ListChangeListener<? super Message>) change -> {
            messageMasonryPane.getChildren().clear();
            messagesFeed.forEach(this::insertMessage);
        });
    }

    private void insertMessage(Message message) {
        FXMLLoader messageLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/message.fxml"));
        messageLoader.setController(new MessageController(message));
        try {
            messageMasonryPane.getChildren().add(messageLoader.load());
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        messageScrollPane.requestLayout();
        messageScrollPane.requestFocus();
    }
}