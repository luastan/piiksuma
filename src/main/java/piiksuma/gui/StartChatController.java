package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import piiksuma.Message;
import piiksuma.User;
import piiksuma.Utilities.PiikTextLimiter;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.ResourceBundle;

public class StartChatController implements Initializable {
    @FXML
    private JFXTextField userField;

    @FXML
    private JFXTextField messageField;

    @FXML
    private JFXButton mainButton;

    private Message message = new Message();

    private User receiver = new User();

    /**
     * Inits the window components
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Limit text fields
        PiikTextLimiter.addTextLimiter(userField, 32);
        PiikTextLimiter.addTextLimiter(messageField, 200);

        mainButton.setOnAction(this::handleNewMessage);

        ValidatorBase validator = new RequiredFieldValidator("Required field");
        validator.setIcon(GlyphsBuilder.create(FontAwesomeIconView.class)
                .glyph(FontAwesomeIcon.WARNING)
                .build());
        userField.getValidators().addAll(validator);
        messageField.getValidators().addAll(validator);

        messageField.textProperty().addListener((observable, oldValue, newValue) -> {
            mainButton.setDisable(!messageField.validate() || userField.getText().length() == 0);
            message.setText(messageField.getText());
        });

        userField.textProperty().addListener((observable, oldValue, newValue) -> {
            mainButton.setDisable(messageField.getText().length() == 0 || !userField.validate());
            receiver.setId(userField.getText());
        });
    }

    /**
     * Code to be executed when the new message button is pressed
     *
     * @param event Event on the window
     */
    private void handleNewMessage(Event event) {
        if (!messageField.validate() && !userField.validate()) {
            return;
        }
        User current = ContextHandler.getContext().getCurrentUser();
        message.setSender(current);

        try {
            message = ApiFacade.getEntrypoint().getInsertionFacade().createMessage(message, current);
            ApiFacade.getEntrypoint().getInsertionFacade().sendPrivateMessage(message, receiver, current);
        } catch (PiikDatabaseException e) {
            e.showAlert(e, "User doesn't exist");
        } catch (PiikInvalidParameters e) {
            e.showAlert(e, "Failure sending the private message");
        }

        try {
            ContextHandler.getContext().getMessagesController().updateMessageFeed();
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert(e, "Failure updating the message feed");
        }
    }
}
