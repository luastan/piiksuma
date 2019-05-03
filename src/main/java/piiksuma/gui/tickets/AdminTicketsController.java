package piiksuma.gui.tickets;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import piiksuma.Message;
import piiksuma.Ticket;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;
import piiksuma.gui.ConversationController;
import piiksuma.gui.MessagePreviewController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminTicketsController implements Initializable {

    public JFXMasonryPane ticketsMasonryPane;
    public ScrollPane ticketsScrollPane;

    private Ticket selected;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addTickets();
    }


    public void updateTickets() {
        ticketsMasonryPane.getChildren().clear();
        addTickets();
    }

    private void addTickets() {
        try {
            ApiFacade.getEntrypoint()
                    .getSearchFacade().getAdminTickets(100, ContextHandler.getContext().getCurrentUser())
                    .stream().map(Message::new).forEach(this::insertTicket);
        } catch (PiikInvalidParameters | PiikDatabaseException invalidParameters) {
            invalidParameters.showAlert();
        }
    }

    private void insertTicket(Message ticket) {
        FXMLLoader messageLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/messagePreview.fxml"));
        messageLoader.setController(new MessagePreviewController(ticket, ticket.getTicket().getUser()));
        try {
            Node messagePreview = messageLoader.load();
            messagePreview.setOnMouseClicked(event -> {
                try {
                    ContextHandler.getContext()
                            .invokeStage("/gui/fxml/tickets/TicketActionsController.fxml", new TicketActionsController(ticket.getTicket()));
                } catch (PiikInvalidParameters invalidParameters) {
                    invalidParameters.showAlert();
                }
            });
            ticketsMasonryPane.getChildren().add(messagePreview);
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        ticketsScrollPane.requestLayout();
        ticketsScrollPane.requestFocus();
    }


}
