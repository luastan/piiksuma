package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import piiksuma.Event;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.io.IOException;
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
    private JFXButton moreInfo;

    @FXML
    private JFXButton share;

    private Event eventP;

    private boolean moreInfoClicked;

    public EventController(Event event) {
        this.eventP = event;
        moreInfoClicked = false;
    }

    public EventController(Event event, boolean moreInfoClicked) {
        this.eventP = event;
        this.moreInfoClicked = moreInfoClicked;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setEventInfo();

        if (moreInfoClicked == true) {
            moreInfo.setVisible(false);
            return;
        }

        participate.setOnAction(this::handleParticipate);
        moreInfo.setOnAction(this::handleMoreInfo);
    }

    private void setEventInfo() {
        eventDescription.setText(eventP.getDescription());
        authorName.setText(eventP.getCreator().getName());
        authorId.setText(eventP.getCreator().getId());
        eventDate.setText(eventP.getDate().toString());
        eventLocation.setText(eventP.getLocation());
    }

    private void handleParticipate(javafx.event.Event event) {

        try {
            ApiFacade.getEntrypoint().getInsertionFacade().participateEvent(eventP, ContextHandler.getContext().getCurrentUser());
        } catch (PiikException e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    private void handleMoreInfo(javafx.event.Event event) {
        Stage registerStage = new Stage();

        try {
            ContextHandler.getContext().register("moreInfo", registerStage);
        } catch (PiikInvalidParameters e) {
            e.printStackTrace();
            return;
        }
        // Stage configuration
        registerStage.setTitle("Event");
        registerStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/event.fxml"));
        loader.setController(new EventController(eventP, true));

        JFXDecorator decorator;

        try {
            decorator = new JFXDecorator(registerStage, loader.load(), false, false, true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(decorator, 450, 450);


        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm()
        );
        registerStage.initModality(Modality.WINDOW_MODAL);
        registerStage.initOwner(ContextHandler.getContext().getStage("primary"));
        registerStage.setScene(scene);
        registerStage.show();
    }
}
