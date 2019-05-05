package piiksuma.gui.tickets;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import piiksuma.Ticket;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class TicketController implements Initializable {

    private Ticket ticket;

    @FXML
    private Label id;

    @FXML
    private Label section;

    @FXML
    private Label description;

    @FXML
    private StackPane ticketContainer;

    public TicketController(Ticket ticket) {
        this.ticket = ticket;
    }

    /**
     * Inits the window components
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        id.setText(Integer.toString(ticket.getId()));
        section.setText(ticket.getSection());
        description.setText(ticket.getTextProblem());
        ticketContainer.setOnMouseClicked(event -> {
            try {
                ContextHandler.getContext().invokeStage("/gui/fxml/conversation.fxml", new TicketConvController(ticket));
            } catch (PiikInvalidParameters invalidParameters) {
                invalidParameters.showAlert(invalidParameters, "Failure loading the conversation window");
            }
        });
    }
}
