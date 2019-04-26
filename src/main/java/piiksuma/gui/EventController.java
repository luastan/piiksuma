package piiksuma.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import piiksuma.Event;

import java.net.URL;
import java.util.ResourceBundle;

public class EventController implements Initializable {

    @FXML
    private Label authorName;

    @FXML
    private Label authorId;

    @FXML
    private Label eventDate;

    @FXML
    private Label eventLocation;

    @FXML
    private Label eventDescription;

    private Event event;

    public EventController(Event event) {
        this.event = event;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        eventDescription.setText(event.getDescription());
        authorName.setText(event.getCreator().getName());
        authorId.setText(event.getCreator().getId());
        eventDate.setText(event.getDate().toString());
        eventLocation.setText(event.getLocation());
    }
}
