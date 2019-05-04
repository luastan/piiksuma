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
import piiksuma.Utilities.PiikLogger;
import piiksuma.Utilities.PiikTextLimiter;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.logging.Level;

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
        messages.addListener((ListChangeListener<? super Message>) c -> {
            messageMasonryPane.getChildren().clear();
            messages.stream().sorted(Comparator.comparing(Message::getDate)).forEach(this::insertMessage);
            messageScrollPane.requestLayout();
            messageScrollPane.requestFocus();
        });

        updateMessages();

    }

    private void sendMessage(ActionEvent event) {
        try {
            User current = ContextHandler.getContext().getCurrentUser();
            ApiFacade.getEntrypoint().getInsertionFacade().createMessage(newMessage, current);
            ApiFacade.getEntrypoint().getInsertionFacade().sendPrivateMessage(newMessage, peer, current);
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            PiikLogger.getInstance().log(Level.SEVERE, "ConversationController -> sendMessage", e);
            Alert.newAlert().setHeading("Send message error").addText("Failure sending the message").addCloseButton().show();
        }
        initializeNewMessage();
        messageField.setText("");
        messageField.resetValidation();
        sendButton.setDisable(true);
        updateMessages();
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
        } catch (IOException e) {
            PiikLogger.getInstance().log(Level.SEVERE, "ConversationController -> insertMessage", e);
            Alert.newAlert().setHeading("Insert message error").addText("Failure inserting the message").addCloseButton().show();
        }
    }

    private void updateMessages() {
        try {
            User current = ContextHandler.getContext().getCurrentUser();
            messages.clear();
            messages.addAll(ApiFacade.getEntrypoint().getSearchFacade()
                    .messageWithUser(current, 250, current)
                    .get(peer));
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert(e, "Failure updating the messages");
        }
    }
}
