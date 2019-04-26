package piiksuma.gui;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import piiksuma.Event;
import piiksuma.Message;
import piiksuma.Ticket;
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TicketsController implements Initializable {

    @FXML
    private ScrollPane ticketScrollPane;
    @FXML
    private JFXMasonryPane ticketMasonryPane;

    private ObservableList<Ticket> ticketFeed;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void updateTicketFeed() {

        ticketFeed.clear();
        ticketFeed.addAll(new QueryMapper<Ticket>(ApiFacade.getEntrypoint().getConnection()).defineClass(Ticket.class).createQuery("SELECT * FROM ticket;").list());

    }

    private void setUpFeedListener() {
        ticketFeed.addListener((ListChangeListener<? super Ticket>) change -> {
            ticketMasonryPane.getChildren().clear();
            ticketFeed.forEach(this::insertTicket);
        });
    }

    private void insertTicket(Ticket ticket) {
        FXMLLoader ticketLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/message.fxml"));
        ticketLoader.setController(new TicketController(ticket));
        try {
            ticketMasonryPane.getChildren().add(ticketLoader.load());
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        ticketScrollPane.requestLayout();
        ticketScrollPane.requestFocus();
    }
}
