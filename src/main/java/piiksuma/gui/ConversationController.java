package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import piiksuma.Message;
import piiksuma.User;
import piiksuma.Utilities.PiikTextLimiter;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConversationController implements Initializable {

    private User peer;
    private ObservableList<Message> messages;

    @FXML
    private JFXTextField messageField;

    @FXML
    private ScrollPane messageScrollPane;

    @FXML
    private JFXMasonryPane messageMasonryPane;

    @FXML
    private JFXButton sendButton;

    @FXML
    private Label username;
    @FXML
    private Label userId;

    public ConversationController(User peer) {
        this.peer = peer;

    }

    private Message newMessage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ContextHandler.getContext().setConversationController(this);
        messages = FXCollections.observableArrayList();
        initializeNewMessage();

        userId.setText(peer.getId());
        username.setText(peer.getName());

        ValidatorBase validator = new RequiredFieldValidator("Required Field");
        validator.setIcon(GlyphsBuilder.create(FontAwesomeIconView.class)
                .glyph(FontAwesomeIcon.WARNING)
                .build());
        messageField.getValidators().add(validator);
        PiikTextLimiter.addTextLimiter(messageField, 140);
        messageField.textProperty().addListener((observable, oldValue, newValue) -> {
            sendButton.setDisable(!messageField.validate());
            newMessage.setText(messageField.getText());
        });

        sendButton.setOnAction(this::sendMessage);

        Message testMessage = new Message();
        testMessage.setText("Hello youtube friends I'm Rupesh");
        User current = ContextHandler.getContext().getCurrentUser();
        testMessage.setSender(current);
        testMessage.setId("fsd");

        messages.add(testMessage);
        messages.addListener((ListChangeListener<? super Message>) c -> {
            messageMasonryPane.getChildren().clear();
            messages.forEach(this::insertMessage);
        });

        // TODO Propper initialization with the user messages.
        try {
            User foo = new User();
            foo.setId("usr2");
            foo.setName("User2");
            ApiFacade.getEntrypoint().getSearchFacade().readMessages(current, current).forEach(message -> {
                message.setSender(foo);
                messages.add(message);
            });
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.printStackTrace();
        }

    }

    private void sendMessage(ActionEvent event) {
        try {
            ApiFacade.getEntrypoint().getInsertionFacade().sendPrivateMessage(newMessage, peer, ContextHandler.getContext().getCurrentUser());
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            // TODO: Handle the exception
            e.printStackTrace();
        }
        initializeNewMessage();
        messageField.setText(" ");
        sendButton.setDisable(true);

    }


    private void initializeNewMessage() {
        newMessage = new Message();
        newMessage.setSender(ContextHandler.getContext().getCurrentUser());
        newMessage.setId(" ");
    }

    private void insertMessage(Message message) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/message.fxml"));
        loader.setController(new MessageController(message));
        try {
            messageMasonryPane.getChildren().add(loader.load());
            messageScrollPane.requestLayout();
            messageScrollPane.requestFocus();
        } catch (IOException e) {
            // TODO: Handle Exceptions
            e.printStackTrace();
        }
    }
}
