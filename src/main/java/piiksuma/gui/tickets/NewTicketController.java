package piiksuma.gui.tickets;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import piiksuma.Ticket;
import piiksuma.User;
import piiksuma.Utilities.PiikLogger;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.Alert;
import piiksuma.gui.ContextHandler;


import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class NewTicketController implements Initializable {


    @FXML
    private JFXTextField section;

    @FXML
    private JFXTextArea description;

    @FXML
    private JFXButton sendButton;

    private Ticket ticket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ticket = new Ticket();
        sendButton.setOnAction(this::handleSendButton);

        RequiredFieldValidator validator = new RequiredFieldValidator("Required field");
        validator.setIcon(GlyphsBuilder.create(FontAwesomeIconView.class)
                .glyph(FontAwesomeIcon.WARNING)
                .build());
        description.setValidators(validator);
        section.setValidators(validator);
        description.textProperty().addListener((observable, oldValue, newValue) -> {
            ticket.setTextProblem(newValue);
            sendButton.setDisable(!description.validate() || !(section.getText().length() > 0));
        });
        section.textProperty().addListener((observable, oldValue, newValue) -> {
            ticket.setTextProblem(newValue);
            sendButton.setDisable(!section.validate() || !(description.getText().length() > 0));
        });
    }

    public void handleSendButton(Event event) {
        Ticket ticket = new Ticket();
        if (section.getText().isEmpty() || description.getText().isEmpty()) {
            Alert alert = new Alert(ContextHandler.getContext().getStage("newTicket"));
            alert.setHeading("Fields empty!");
            alert.addText("Section and description cannot be empty");
            alert.addCloseButton();
            alert.show();
            return;
        }
        ticket.setSection(section.getText());
        ticket.setUser(ContextHandler.getContext().getCurrentUser());
        ticket.setTextProblem(description.getText());
        try {
            ApiFacade.getEntrypoint().getInsertionFacade().newTicket(ticket, ContextHandler.getContext().getCurrentUser());
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            PiikLogger.getInstance().log(Level.SEVERE, "NewTicketController -> handleSendButton", e);
            Alert.newAlert().setHeading("Send error").addText("Failure sending the ticket to the admin").addCloseButton().show();
        }

        ContextHandler.getContext().getStage("New Ticket").close();

    }
}
