package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikException;

import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class CreateEventController implements Initializable {
    @FXML
    private JFXButton newEvent;
    @FXML
    private JFXTextField eventName;
    @FXML
    private JFXTextField location;
    @FXML
    private JFXDatePicker date;
    @FXML
    private JFXTextArea description;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        newEvent.setOnAction(this::handleNew);
        date.setValidators(new RequiredFieldValidator());
        RequiredFieldValidator eventNameValidator = new RequiredFieldValidator();
        eventNameValidator.setMessage("Field required");
        eventNameValidator.setIcon(GlyphsBuilder.create(FontAwesomeIconView.class)
                .glyph(FontAwesomeIcon.WARNING)
                .build());
        eventName.setValidators(eventNameValidator);
        eventName.textProperty().addListener((observable, oldValue, newValue) -> {
            newEvent.setDisable(!eventName.validate());
        });
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

        if (date.validate()) {
            newEvent.setDate(Timestamp.valueOf(date.getValue().atStartOfDay()));
        }

//        if(!date.getText().isEmpty()) {
//            try {
//                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//                Date d = dateFormat.parse(date.getText());
//                newEvent.setDate(new java.sql.Timestamp(d.getTime()));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }

        try {
            newEvent=ApiFacade.getEntrypoint().getInsertionFacade().createEvent(newEvent, ContextHandler.getContext().getCurrentUser());
        }catch (PiikException e){
            System.out.println(e.getMessage());
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
