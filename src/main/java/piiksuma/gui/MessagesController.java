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
import java.util.Comparator;
import java.util.Objects;
import java.util.ResourceBundle;

public class MessagesController implements Initializable {

    @FXML
    private ScrollPane messageScrollPane;
    @FXML
    private JFXMasonryPane messageMasonryPane;

    private ObservableList<User> peers;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        peers = FXCollections.observableArrayList();

        ContextHandler.getContext().setMessagesController(this);
        setUpFeedListener();

        try {
            updateMessageFeed();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }
    }

    public void updateMessageFeed() throws PiikDatabaseException {
        peers.clear();
        // Gets all the users who interacted with the current one via messages
        // TODO: Get users who interacted with current one
        peers.addAll(new QueryMapper<User>(ApiFacade.getEntrypoint().getConnection()).defineClass(User.class).createQuery("Select * from piiuser").list());
    }

    private void setUpFeedListener() {
        peers.addListener((ListChangeListener<? super User>) change -> {
            messageMasonryPane.getChildren().clear();
            peers.forEach(this::insertConversation);
        });
    }

    private void insertConversation(User peer) {
        User current = ContextHandler.getContext().getCurrentUser();
        FXMLLoader messageLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/messagePreview.fxml"));
        /* Gets last message exchanged with the peer */
        // TODO: Get last message sent by user
        Message lastMessage = new Message();
        lastMessage.setText("Sample Text Message");
        messageLoader.setController(new MessagePreviewController(lastMessage, peer));
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

    private void clickMessage(MouseEvent mouseEvent) {
        if (!(mouseEvent.getSource() instanceof StackPane)) {

            return;
        }

        User target = new User();
        ((StackPane) mouseEvent.getSource()).getChildren().stream().filter(node -> node instanceof Label)
                .map(node -> (Label) node).map(Labeled::getText).forEach(target::setId);

        try {
            target = ApiFacade.getEntrypoint().getSearchFacade().getUser(target, ContextHandler.getContext().getCurrentUser());
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.printStackTrace();
        }
        ConversationController controller = new ConversationController(target);

        Stage conversationStage = new Stage();
        conversationStage.setResizable(false);
        conversationStage.setTitle("Piiksuma - Conversation");
        conversationStage.initModality(Modality.WINDOW_MODAL);
        try {
            ContextHandler.getContext().register("conversation", conversationStage);
        } catch (PiikInvalidParameters e) {
            e.printStackTrace();
            return;
        }
        // Stage configuration

        JFXDecorator decorator;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/conversation.fxml"));
        loader.setController(controller);
        try {
            decorator = new JFXDecorator(conversationStage, loader.load(), false, false, true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Scene scene = new Scene(decorator, 450, 900);
        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/main.css").toExternalForm(),
                getClass().getResource("/gui/css/global.css").toExternalForm()
        );

        conversationStage.setScene(scene);
        conversationStage.initOwner(ContextHandler.getContext().getStage("primary"));
        conversationStage.show();
    }

}