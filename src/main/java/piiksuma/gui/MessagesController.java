package piiksuma.gui;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXMasonryPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import piiksuma.Message;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;

public class MessagesController implements Initializable {

    public StackPane noContentLabel;
    @FXML
    private ScrollPane messageScrollPane;
    @FXML
    private JFXMasonryPane messageMasonryPane;

    private ObservableList<User> peers;

    private Map<User, List<Message>> lastMessages;

    /**
     * Inits the window components
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        peers = FXCollections.observableArrayList();

        ContextHandler.getContext().setMessagesController(this);
        setUpFeedListener();

        try {
            updateMessageFeed();
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert();
        }
    }

    /**
     * Updates the messages shown in the window
     * @throws PiikDatabaseException
     * @throws PiikInvalidParameters
     */
    public void updateMessageFeed() throws PiikDatabaseException, PiikInvalidParameters {
        User current = ContextHandler.getContext().getCurrentUser();
        lastMessages = ApiFacade.getEntrypoint().getSearchFacade().messageWithUser(current, 100, current);
        peers.clear();
        peers.addAll(lastMessages.keySet());
        noContentLabel.setVisible(peers.size() == 0);
    }

    /**
     * Sets up the feed listener
     */
    private void setUpFeedListener() {
        peers.addListener((ListChangeListener<? super User>) change -> {
            messageMasonryPane.getChildren().clear();
            peers.forEach(this::insertConversation);
        });
    }

    /**
     * Inserts a conversation in the feed
     * @param peer
     */
    private void insertConversation(User peer) {
        User current = ContextHandler.getContext().getCurrentUser();
        FXMLLoader messageLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/messagePreview.fxml"));
        messageLoader.setController(new MessagePreviewController(lastMessages.get(peer).get(0), peer));
        try {
            Node messagePreview = messageLoader.load();
            messageMasonryPane.getChildren().add(messagePreview);
            messagePreview.setOnMouseClicked(this::clickMessage);
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        messageScrollPane.requestLayout();
        messageScrollPane.requestFocus();
    }

    /**
     * Code to be executed when a mouse event happens
     * @param mouseEvent mouse event on the window
     */
    private void clickMessage(MouseEvent mouseEvent) {
        if (!(mouseEvent.getSource() instanceof StackPane)) {

            return;
        }

        // Identifies clicked User
        User target = lastMessages.keySet().stream()
                .filter(user -> user.getId().equals(((StackPane) mouseEvent.getSource()).getChildren().stream()
                        .filter(node -> node instanceof Label).map(node -> (Label) node).map(Labeled::getText)
                        .findFirst().orElse(""))).findFirst().orElse(null);
        if (target == null) {
            return;
        }
        ConversationController controller = new ConversationController(target);
        try {
            ContextHandler.getContext().invokeStage("/gui/fxml/conversation.fxml", controller, "Messages" + (target.getName() != null ? " - " + target.getName() : ""));
        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.showAlert();
        }
    }

}