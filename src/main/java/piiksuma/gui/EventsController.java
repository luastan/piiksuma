package piiksuma.gui;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import piiksuma.Event;
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;

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

        updateEventFeed();
    }

    private void updateEventFeed(){

        eventFeed.clear();
        eventFeed.addAll(new QueryMapper<Event>(ApiFacade.getEntrypoint().getConnection()).defineClass(Event.class).createQuery("SELECT * FROM event;").list());

    }

    private void setUpFeedListener() {
        eventFeed.addListener((ListChangeListener<? super Event>) change -> {
            eventMasonryPane.getChildren().clear();
            eventFeed.forEach(this::insertEvent);
        });
    }

    private void insertEvent(Event event){
        FXMLLoader eventLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/event.fxml"));
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
