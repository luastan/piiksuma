package piiksuma.gui;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import piiksuma.Event;
import piiksuma.Post;
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EventsController implements Initializable {

    @FXML
    private Label eventDescription;

    @FXML
    private Label authorName;




    private Event event;

    public EventsController(Event event) {
        this.event = event;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        authorName.setText(event.getCreator().getName());
        eventDescription.setText(event.getDescription());
    }
}
