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
import piiksuma.Utilities.PiikLogger;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.deckControllers.NotificationController;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class NotificationsController implements Initializable {
    @FXML
    private ScrollPane notScrollPane;

    @FXML
    private JFXMasonryPane notificationMasonryPane;

    private ObservableList<Notification> notifications;

    /**
     * Inits the window components
     * @param location
     * @param resources
     */
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
            e.showAlert(e, "Failure updating notifications");
        }
    }

    /**
     * Updates the notification feed
     * @throws PiikDatabaseException
     */
    private void updateNotifications() throws PiikDatabaseException {

        notifications.clear();
        User user = ContextHandler.getContext().getCurrentUser();

        try {
            List<Notification> notifications1 = ApiFacade.getEntrypoint().getSearchFacade().getNotifications(user, user);

            notifications.addAll(notifications1);

        } catch (PiikInvalidParameters piikInvalidParameters) {
            piikInvalidParameters.showAlert(piikInvalidParameters, "Failure updating notifications");
        }
    }

    /**
     * Sets up the feed listener
     */
    private void setUpNotificationsListener() {

        // Change takes place
        notifications.addListener((ListChangeListener<? super Notification>) change -> {
            // Visual representation gets cleared
            notificationMasonryPane.getChildren().clear();

            notifications.forEach(this::insertNotification);
        });
    }

    /**
     * Inserts a notification in the feed
     * @param notification Notification to be inserted
     */
    private void insertNotification(Notification notification) {

        // Achievement container
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/gui/fxml/notification.fxml"));
        loader.setController(new NotificationController(notification));

        // Loads the container in the panel
        try {
            notificationMasonryPane.getChildren().add(loader.load());
        } catch (IOException e) {
            PiikLogger.getInstance().log(Level.SEVERE, "NotificationController -> insertNotification", e);
            Alert.newAlert().setHeading("Insert notification error").addText("Failure inserting the notification").addCloseButton().show();
        }

        notScrollPane.requestLayout();
        notScrollPane.requestFocus();
    }
}
