package piiksuma.gui.events;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import piiksuma.Event;


import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EventPreviewController implements Initializable {

    private Event event;

    @FXML
    private Label hiddenEventId;
    @FXML
    private Label name;
    @FXML
    private Label description;
    @FXML
    private Label date;


    public EventPreviewController(Event event) {
        this.event = event;
    }

    /**
     * Inits the window components
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hiddenEventId.setText(event.getId());
        name.setText(event.getName());
        description.setText(event.getDescription());
        date.setText(event.getDate().toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
