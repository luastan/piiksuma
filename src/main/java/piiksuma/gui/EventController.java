package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import piiksuma.Event;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikException;

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

    @FXML
    private JFXButton participate;

    @FXML
    private JFXButton share;

    private Event eventP;

    public EventController(Event event) {
        this.eventP = event;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setEventInfo();

        participate.setOnAction(this::handleParticipate);
    }

    private void setEventInfo(){
        eventDescription.setText(eventP.getDescription());
        authorName.setText(eventP.getCreator().getName());
        authorId.setText(eventP.getCreator().getId());
        eventDate.setText(eventP.getDate().toString());
        eventLocation.setText(eventP.getLocation());
    }

    private void handleParticipate(javafx.event.Event event){
        try{
            ApiFacade.getEntrypoint().getInsertionFacade().participateEvent(eventP, ContextHandler.getContext().getCurrentUser());
        }catch (PiikException e){
            System.out.println(e.getMessage());
            return;
        }
    }
}
