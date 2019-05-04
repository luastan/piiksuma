package piiksuma.gui.tickets;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import piiksuma.Ticket;
import piiksuma.Utilities.PiikLogger;
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.Alert;
import piiksuma.gui.ContextHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class TicketsController implements Initializable {

    @FXML
    private ScrollPane ticketScrollPane;

    @FXML
    private JFXMasonryPane ticketMasonryPane;

    private ObservableList<Ticket> tickets;

    @Override

    public void initialize(URL location, ResourceBundle resources) {
        tickets = FXCollections.observableArrayList();

        setUpFeedListener();
        handleSearch(null);
    }


    private void handleSearch(Event event) {
        try {
            updateTicketFeed();
        } catch (PiikDatabaseException e) {
            PiikLogger.getInstance().log(Level.SEVERE, "TicketsController -> handleSearch", e);
            Alert.newAlert().setHeading("Feed error").addText("Failure updating the feed").addCloseButton().show();
        }
    }

    private void updateTicketFeed() throws PiikDatabaseException {
        try {
            tickets.addAll(ApiFacade.getEntrypoint().getSearchFacade().getUserTickets(
                    ContextHandler.getContext().getCurrentUser(), ContextHandler.getContext().getCurrentUser(), 20));
        } catch (PiikInvalidParameters piikInvalidParameters) {
            piikInvalidParameters.showAlert(piikInvalidParameters, "Failure updating the feed");
        }
    }

    private void setUpFeedListener() {
        tickets.addListener((ListChangeListener<? super Ticket>) change -> {
            ticketMasonryPane.getChildren().clear();
            tickets.forEach(this::insertTicket);
        });
    }

    private void insertTicket(Ticket ticket) {
        FXMLLoader ticketLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/tickets/ticket.fxml"));
        ticketLoader.setController(new TicketController(ticket));
        try {
            ticketMasonryPane.getChildren().add(ticketLoader.load());
        } catch (IOException e) {
            PiikLogger.getInstance().log(Level.SEVERE, "TicketsController -> insertTicket", e);
            Alert.newAlert().setHeading("Feed error").addText("Failure inserting the ticket").addCloseButton().show();
        }
        ticketScrollPane.requestLayout();
        ticketScrollPane.requestFocus();
    }
}
