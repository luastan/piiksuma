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

public class EventsPane implements Initializable {
    @FXML
    private ScrollPane eventScroll;

    @FXML
    private JFXMasonryPane eventMasonryPane;

    private ObservableList<Event> feed;

    //private Event event;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        feed = FXCollections.observableArrayList();

        // Registers itself in the ContextHandler
        ContextHandler.getContext().setEventsController(this);
        setUpFeedListener();

        updateFeed();
    }

    /**
     * Reloads feed retrieving last posts from  the database
     */
    public void updateFeed() {
        // TODO: update the feed propperly
        feed.clear();
        feed.addAll(new QueryMapper<Event>(ApiFacade.getEntrypoint().getConnection()).defineClass(Event.class).createQuery("SELECT * FROM event;").list());
    }

    /**
     * Code to be executed when the feed gets updated. Posts at the interface
     * have to be updated.
     * <p>
     * Recieves change performed to the list via {@link ListChangeListener#onChanged(ListChangeListener.Change)}
     */
    private void setUpFeedListener() {
        feed.addListener((ListChangeListener<? super Event>) change -> {
            eventMasonryPane.getChildren().clear();
            feed.forEach(this::insertPost);
        });
    }

    private void insertPost(Event post) {
        FXMLLoader postLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/eventPane.fxml"));
        postLoader.setController(new EventsController(post));
        try {
            eventMasonryPane.getChildren().add(postLoader.load());
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        eventScroll.requestLayout();
        eventScroll.requestFocus();
    }
}
