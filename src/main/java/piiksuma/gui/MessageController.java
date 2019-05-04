package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import piiksuma.Message;
import piiksuma.User;
import piiksuma.Utilities.PiikLogger;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class MessageController implements Initializable {

    private Message message;

    @FXML
    private Label content;

    @FXML
    private StackPane messageHolder;

    @FXML
    private StackPane messageHitbox;

    private Alert forwardAlert;


    public MessageController(Message message) {
        this.message = message;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User current = ContextHandler.getContext().getCurrentUser();
        if (message.getSender().equals(current)) {
            messageHolder.setAlignment(Pos.CENTER_RIGHT);
        }
        content.setText(message.getText());
        initializeForward();
        messageHitbox.setOnMouseClicked(event -> {
            forwardAlert.show();
            event.consume();
        });
    }


    private void initializeForward() {
        forwardAlert = Alert.newAlert().setHeading("Forward message");
        JFXButton forwardButton = new JFXButton("Send");
        forwardButton.setDisable(true);
        JFXTextField textField = new JFXTextField();
        textField.getStyleClass().add("piik-text-field");
        textField.setPromptText("User ID");
        RequiredFieldValidator validator = new RequiredFieldValidator("Required Field");
        validator.setIcon(GlyphsBuilder.create(FontAwesomeIconView.class)
                .glyph(FontAwesomeIcon.WARNING)
                .build());

        textField.getValidators().add(validator);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            forwardButton.setDisable(!textField.validate());
        });

        forwardAlert.addInBody(textField);

        forwardButton.setOnAction(event -> {
            Message forwarded = new Message(message);
            forwarded.setTicket(null);
            forwarded.setSender(ContextHandler.getContext().getCurrentUser());
            try {
                ApiFacade.getEntrypoint().getInsertionFacade().sendPrivateMessage(
                        forwarded, new User(textField.getText()), ContextHandler.getContext().getCurrentUser()
                );
                forwardAlert.getAlert().close();
                ContextHandler.getContext().getMessagesController().updateMessageFeed();
            } catch (PiikDatabaseException | PiikInvalidParameters e) {
                PiikLogger.getInstance().log(Level.SEVERE, "MessageController -> initializeForward", e);
                Alert.newAlert().setHeading("Send message error").addText("Failure sending the private message").addCloseButton().show();
            }
        });

        forwardAlert.addCloseButton().addButtons(forwardButton);

    }
}
