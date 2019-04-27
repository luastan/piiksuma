package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikException;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateEventController implements Initializable {
    @FXML
    private JFXButton newEvent;
    @FXML
    private JFXTextField eventName;
    @FXML
    private JFXTextField location;
    @FXML
    private JFXTextField date;
    @FXML
    private JFXTextArea description;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        newEvent.setOnAction(this::handleNew);
    }

    private void handleNew(Event event) {
        piiksuma.Event newEvent = new piiksuma.Event();

        if(!checkFields()){
            Alert alert = new Alert(ContextHandler.getContext().getStage("createEvent"));
            alert.setHeading("Fields empty!");
            alert.addText("EventName field cannot be empty");
            alert.addCloseButton();
            alert.show();
            return;
        }

        newEvent.setCreator(ContextHandler.getContext().getCurrentUser());
        newEvent.setDescription(description.getText());
        newEvent.setLocation(location.getText());
        newEvent.setName(eventName.getText());
        try {
            newEvent=ApiFacade.getEntrypoint().getInsertionFacade().createEvent(newEvent, ContextHandler.getContext().getCurrentUser());
        }catch (PiikException e){
            //TODO handle with exceptions
            System.out.println("ERROR");
            return;
        }

        ContextHandler.getContext().getStage("createEvent").close();
    }

    private boolean checkFields(){
        if(eventName.getText().isEmpty()){
            return false;
        }
        return true;
    }
}
