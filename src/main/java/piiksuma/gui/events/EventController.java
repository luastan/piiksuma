package piiksuma.gui.events;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import piiksuma.Event;
import piiksuma.User;
import piiksuma.UserType;
import piiksuma.Utilities.PiikLogger;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;
import piiksuma.gui.profiles.ProfilePreviewController;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class EventController implements Initializable {

    @FXML
    private Label eventDate;

    @FXML
    private Label eventName;

    @FXML
    private Label eventLocation;

    @FXML
    private Label eventDescription;

    @FXML
    private ScrollPane participantScrollPane;

    @FXML
    private Label nParticipants;

    @FXML
    private HBox participantMasonryPane;

    @FXML
    private JFXButton participateButton;
    @FXML
    private JFXButton deleteButton;

    private ObservableList<User> participants;

    private Event event;

    public EventController(Event event) {
        participants = FXCollections.observableArrayList();
        this.event = event;
    }

    private void buttonInitialization() {
        User current = ContextHandler.getContext().getCurrentUser();
        deleteButton.setDisable(current.getType() != UserType.administrator && !current.equals(event.getCreator()));
        deleteButton.setOnAction(ev -> {
            try {
                ApiFacade.getEntrypoint().getDeletionFacade().removeEvent(event, current);
                ContextHandler.getContext().getStage("Event - " + event.getName()).close();
                ContextHandler.getContext().getEventsController().updateEventFeed();
            } catch (PiikDatabaseException | PiikInvalidParameters e) {
                e.showAlert(e, "Failure deleting the event");
            }
        });
        if (participants.contains(current)) {
            participateButton.setText("Do not participate");
            participateButton.setStyle("-fx-background-color: -primary-color-2; -fx-text-fill: -black-high-emphasis");
            participateButton.setOnAction(this::unParticipate);
        } else {
            participateButton.setText("participate");
            participateButton.setStyle("-fx-background-color: -primary-color-5");
            participateButton.setOnAction(this::participate);
        }
    }


    private void participate(javafx.event.Event event) {
        try {
            ApiFacade.getEntrypoint().getInsertionFacade().participateEvent(this.event, ContextHandler.getContext().getCurrentUser());
            refreshParticipants();
            buttonInitialization();
        } catch (PiikInvalidParameters | PiikDatabaseException invalidParameters) {
            invalidParameters.showAlert(invalidParameters, "Failure adding participant to the event");
        }
    }


    private void unParticipate(javafx.event.Event event) {
        User current = ContextHandler.getContext().getCurrentUser();

        try {
            ApiFacade.getEntrypoint().getDeletionFacade().deleteUserInEvent(this.event, current, current);
            refreshParticipants();
            buttonInitialization();
        } catch (PiikInvalidParameters | PiikDatabaseException invalidParameters) {
            invalidParameters.showAlert(invalidParameters, "Failure deleting participant of the event");
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HBox.setHgrow(participantMasonryPane, Priority.ALWAYS);
        setEventInfo();
        participants.addListener((ListChangeListener<? super User>) c -> {
            participantMasonryPane.getChildren().clear();
            participants.forEach(this::insertParticipantMasonry);
            nParticipants.setText("" + participants.size());
            buttonInitialization();
        });
        buttonInitialization();
    }

    private void setEventInfo() {
        refreshParticipants();
        eventName.setText(event.getName());
        eventDescription.setText(event.getDescription());
        eventDate.setText(event.getDate().toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
        eventLocation.setText(event.getLocation());
        participants.forEach(this::insertParticipantMasonry);
        nParticipants.setText("" + participants.size());
    }

    private void refreshParticipants() {
        try {
            participants.clear();
            participants.addAll(ApiFacade.getEntrypoint()
                    .getSearchFacade().usersInEvent(event, ContextHandler.getContext().getCurrentUser()).values());
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert(e, "Failure refreshing participants");
        }
    }

    private void insertParticipantMasonry(User user) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/profile/profilePreview.fxml"));
        loader.setController(new ProfilePreviewController(user));
        Node profile;
        try {
            profile = loader.load();
        } catch (IOException e) {
            PiikLogger.getInstance().log(Level.SEVERE, "EventController -> insertParticipantMasonry", e);
            return;
        }
        participantMasonryPane.getChildren().add(profile);
        participantScrollPane.requestLayout();
    }


}
