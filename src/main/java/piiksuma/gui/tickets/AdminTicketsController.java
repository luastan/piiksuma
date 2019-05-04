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
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminTicketsController implements Initializable {

    @FXML
    private ScrollPane ticketScrollPane;

    @FXML
    private JFXMasonryPane ticketMasonryPane;

    private ObservableList<Ticket> tickets;

    @Override

    /**
     * Init the window components
     */
    public void initialize(URL location, ResourceBundle resources) {
        tickets = FXCollections.observableArrayList();

        setUpFeedListener();
        handleSearch(null);
    }

    /**
     * Code the the search button
     * @param event Event on the window
     */
    private void handleSearch(Event event) {
        try {
            updateTicketFeed();
        } catch (PiikDatabaseException e) {
            e.showAlert();
        }
    }

    /**
     * Updates the tickets feed
     * @throws PiikDatabaseException
     */
    private void updateTicketFeed() throws PiikDatabaseException {
        try {
            tickets.addAll(ApiFacade.getEntrypoint().getSearchFacade().getAdminTickets(10, ContextHandler.getContext().getCurrentUser()));

        } catch (PiikInvalidParameters piikInvalidParameters) {
            // TODO handle
            piikInvalidParameters.printStackTrace();
        }
    }

    /**
     * Sets up the feed listener
     */
    private void setUpFeedListener() {
        tickets.addListener((ListChangeListener<? super Ticket>) change -> {
            ticketMasonryPane.getChildren().clear();
            tickets.forEach(this::insertTicket);
        });
    }

    /**
     * Inserts a ticket in the window
     * @param ticket Ticket to be inserted
     */
    private void insertTicket(Ticket ticket) {
        FXMLLoader ticketLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/tickets/ticket.fxml"));
        try {
            ticketLoader.setController(new TicketController(ticket));
            ticketMasonryPane.getChildren().add(ticketLoader.load());
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        ticketScrollPane.requestLayout();
        ticketScrollPane.requestFocus();
    }
}
