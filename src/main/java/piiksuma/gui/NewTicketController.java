package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import piiksuma.Ticket;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;


import java.net.URL;
import java.util.ResourceBundle;

public class NewTicketController implements Initializable {


    @FXML
    private JFXTextField section;

    @FXML
    private JFXTextArea description;

    @FXML
    private JFXButton sendButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sendButton.setOnAction(this::handleSendButton);
    }

    public void handleSendButton(Event event) {
        Ticket ticket = new Ticket();
        Alert alert = new Alert(ContextHandler.getContext().getStage("newTicket"));
        if (section.getText().isEmpty() || description.getText().isEmpty()) {
            alert.setHeading("Fields empty!");
            alert.addText("Fields cannot be empty");
            alert.addCloseButton();
            alert.show();
            return;
        }
        ticket.setSection(section.getText());
        ticket.setUser(ContextHandler.getContext().getCurrentUser());
        ticket.setTextProblem(description.getText());
        try {
            ApiFacade.getEntrypoint().getInsertionFacade().newTicket(ticket, ContextHandler.getContext().getCurrentUser());
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        } catch (PiikInvalidParameters piikInvalidParameters) {
            piikInvalidParameters.printStackTrace();
        }

        ticket.toString();

    }
}
