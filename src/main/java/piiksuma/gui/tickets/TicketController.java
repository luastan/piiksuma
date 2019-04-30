package piiksuma.gui.tickets;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import piiksuma.Ticket;

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

    public TicketController(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        id.setText(Integer.toString(ticket.getId()));
        section.setText(ticket.getSection());
        description.setText(ticket.getTextProblem());
    }
}
