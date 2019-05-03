package piiksuma.gui.tickets;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import piiksuma.Message;
import piiksuma.Ticket;
import piiksuma.User;
import piiksuma.UserType;
import piiksuma.Utilities.PiikTextLimiter;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;
import piiksuma.gui.MessageController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TicketConvController implements Initializable {

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
    private HBox bottomPanel;

    @FXML
    private Label userId;

    private Ticket ticket;

    private Message newMessage;

    public TicketConvController(Ticket ticket) {
        this.ticket = ticket;
        initializeNewMessage();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ValidatorBase validator = new RequiredFieldValidator("Required Field");
        validator.setIcon(GlyphsBuilder.create(FontAwesomeIconView.class)
                .glyph(FontAwesomeIcon.WARNING)
                .build());
        messageField.getValidators().add(validator);
        messageField.textProperty().addListener((observable, oldValue, newValue) -> {
            newMessage.setText(messageField.getText());
            sendButton.setDisable(!messageField.validate());
        });
        sendButton.setOnAction(this::sendMessage);
        userId.setText(ticket.getTextProblem());
        username.setText("Ticket " + ticket.getId());
        insertMessages();

        // Ability to close the ticket if the current user is an admin
        if (ContextHandler.getContext().getCurrentUser().getType().equals(UserType.administrator)) {
            JFXButton closeButton = new JFXButton("Close ticket");
            closeButton.getStyleClass().add("button-colored");
            closeButton.setOnAction(this::closeTicket);
            bottomPanel.getChildren().add(closeButton);
        }
    }

    public void updateMessages() {
        messageMasonryPane.getChildren().clear();
        this.insertMessages();
    }

    private void insertMessages() {
        User current = ContextHandler.getContext().getCurrentUser();
        // TODO: grab all the messages in a ticket conversation and call insertMessage
        // ApiFacade.getEntrypoint().getSearchFacade().getMessages(ticket, current).forEach(this::insertMessage);
    }

    private void insertMessage(Message message) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/message.fxml"));
        loader.setController(new MessageController(message));
        try {
            messageMasonryPane.getChildren().add(loader.load());
        } catch (IOException e) {
            // TODO: Handle Exceptions
            e.printStackTrace();
        }
    }

    private void initializeNewMessage() {
        newMessage = new Message();
        newMessage.setSender(ContextHandler.getContext().getCurrentUser());
        newMessage.setId(" ");
        newMessage.setTicket(this.ticket);
    }

    private void sendMessage(ActionEvent event) {
        User current = ContextHandler.getContext().getCurrentUser();
        try {
            ApiFacade.getEntrypoint().getInsertionFacade().createMessage(newMessage, current);
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert();
        }
        initializeNewMessage();
        messageField.setText("");
        sendButton.setDisable(true);
        updateMessages();
    }


    private void closeTicket(Event event) {
        Stage stage = ContextHandler.getContext().getStage("Ticket");
        if (stage != null) {
            stage.close();
        }
        User current = ContextHandler.getContext().getCurrentUser();
        try {
            ApiFacade.getEntrypoint().getInsertionFacade().closeTicket(ticket, current);
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert();
        }
    }


}
