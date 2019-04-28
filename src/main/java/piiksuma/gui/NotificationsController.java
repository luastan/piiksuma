package piiksuma.gui;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import piiksuma.Notification;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.deckControllers.NotificationController;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class NotificationsController implements Initializable {
    @FXML
    private ScrollPane notScrollPane;

    @FXML
    private JFXMasonryPane notificationMasonryPane;

    private ObservableList<Notification> notifications;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        notifications = FXCollections.observableArrayList();

        // Registers itself
        ContextHandler.getContext().setNotificationsController(this);

        setUpNotificationsListener();

        try {
            // Update its contents
            updateNotifications();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }
    }


    private void updateNotifications() throws PiikDatabaseException {

        notifications.clear();
        User user = ContextHandler.getContext().getCurrentUser();

        try {
            List<Notification> notifications = ApiFacade.getEntrypoint().getSearchFacade().getNotifications(user, user);

            notifications.addAll(notifications);

        } catch (PiikInvalidParameters piikInvalidParameters) {
            piikInvalidParameters.printStackTrace();
        }
    }


    private void setUpNotificationsListener() {

        // Change takes place
        notifications.addListener((ListChangeListener<? super Notification>) change -> {
            // Visual representation gets cleared
            notificationMasonryPane.getChildren().clear();
            
            notifications.forEach(this::insertNotification);
        });
    }


    private void insertNotification(Notification notification) {

        // Achievement container
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/gui/fxml/notification.fxml"));
        loader.setController(new NotificationController(notification));

        // Loads the container in the panel
        try {
            notificationMasonryPane.getChildren().add(loader.load());
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }

        notScrollPane.requestLayout();
        notScrollPane.requestFocus();
    }
}