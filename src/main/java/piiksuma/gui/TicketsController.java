package piiksuma.gui;

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
    @FXML
    private JFXTextField searchText;
    @FXML
    private JFXButton Search;

    private ObservableList<Ticket> ticketFeed;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ticketFeed = FXCollections.observableArrayList();

        ContextHandler.getContext().setTicketsController(this);
        setUpFeedListener();

        updateTicketFeed();
        Search.setOnAction(this::handleSearch);
    }

    private void handleSearch(Event event){
        updateTicketFeed();
    }

    private void updateTicketFeed() {

        ticketFeed.clear();
        if (searchText.getText().isEmpty()){
            searchText.setText("");
        }
        ticketFeed.addAll(new QueryMapper<Ticket>(ApiFacade.getEntrypoint().getConnection()).defineClass(Ticket.class).createQuery("SELECT * FROM ticket WHERE section LIKE ?;")
                .defineParameters("%"+searchText.getText()+"%").list());

    }

    private void setUpFeedListener() {
        ticketFeed.addListener((ListChangeListener<? super Ticket>) change -> {
            ticketMasonryPane.getChildren().clear();
            ticketFeed.forEach(this::insertTicket);
        });
    }

    private void insertTicket(Ticket ticket) {
        FXMLLoader ticketLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/ticket.fxml"));
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
