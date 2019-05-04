package piiksuma.gui.deckControllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import piiksuma.Notification;

import java.net.URL;
import java.util.ResourceBundle;

public class NotificationController implements Initializable {

    @FXML
    private Label creationDate;

    @FXML
    private Label notificationContent;

    private Notification notification;

    public NotificationController(Notification notification) {
        this.notification = notification;
    }

    /**
     * Inits the window components
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        creationDate.setText(notification.getCreationDate().toString());
        notificationContent.setText(notification.getContent());

    }

}
