package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateEventController implements Initializable {
    @FXML
    private JFXButton newEvent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        newEvent.setOnAction(this::handleNew);
    }

    private void handleNew(Event event){
        ContextHandler.getContext().getStage("createEvent").close();
    }
}
