package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import piiksuma.Event;

import java.net.URL;
import java.util.ResourceBundle;

public class EventMoreInfoController implements Initializable {
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

    @FXML
    private JFXButton participate;

    @FXML
    private JFXButton moreInfo;

    @FXML
    private JFXButton share;
    private Event event;

    public EventMoreInfoController(Event event) {
        this.event = event;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
