package piiksuma.gui;

import javafx.fxml.Initializable;
import piiksuma.Ticket;

import java.net.URL;
import java.util.ResourceBundle;

public class TicketController implements Initializable {

    private Ticket ticket;

    public TicketController(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
