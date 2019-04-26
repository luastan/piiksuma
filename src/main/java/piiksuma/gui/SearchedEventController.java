package piiksuma.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import piiksuma.Event;

import java.net.URL;
import java.util.ResourceBundle;

public class SearchedEventController implements Initializable {

    @FXML
    private Label search;

    private Event event;

    public SearchedEventController(Event event) {
        this.event = event;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        search.setText(event.getName());

    }
}
