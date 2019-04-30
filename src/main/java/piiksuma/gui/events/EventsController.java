package piiksuma.gui.events;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import piiksuma.Event;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikException;
import piiksuma.gui.ContextHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EventsController implements Initializable {

    @FXML
    private ScrollPane eventScrollPane;
    @FXML
    private JFXMasonryPane eventMasonryPane;

    private ObservableList<Event> eventFeed;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventFeed = FXCollections.observableArrayList();

        ContextHandler.getContext().setEventsController(this);
        setUpFeedListener();

        try {
            updateEventFeed();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to updates the feed on event's tab
     *
     * @throws PiikDatabaseException
     */
    private void updateEventFeed() throws PiikDatabaseException {
        User user = ContextHandler.getContext().getCurrentUser();
        eventFeed.clear();
        try {
            eventFeed.addAll(ApiFacade.getEntrypoint().getSearchFacade().getEvents(user, user));
        }catch (PiikException e){
            System.out.println(e.getMessage());
            return;
        }

    }

    private void setUpFeedListener() {
        eventFeed.addListener((ListChangeListener<? super Event>) change -> {
            eventMasonryPane.getChildren().clear();
            eventFeed.forEach(this::insertEvent);
        });
    }

    private void insertEvent(Event event) {
        FXMLLoader eventLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/events/event.fxml"));
        eventLoader.setController(new EventController(event));
        try {
            eventMasonryPane.getChildren().add(eventLoader.load());
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        eventScrollPane.requestLayout();
        eventScrollPane.requestFocus();
    }
}