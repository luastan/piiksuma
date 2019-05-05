package piiksuma.gui.deckControllers;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import piiksuma.Notification;
import piiksuma.User;
import piiksuma.Utilities.PiikLogger;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;
import piiksuma.gui.profiles.UserProfileController;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class AbstractDeckController {
    private JFXHamburger hamburguerButton;

    private JFXButton userProfileButton;

    private JFXButton userDataButton;

    private JFXButton viewNotificationsButton;

    @FXML
    public StackPane deck;

    public AbstractDeckController() {
    }

    public JFXButton getViewNotificationsButton() {
        return viewNotificationsButton;
    }

    public JFXHamburger getHamburguerButton() {
        return hamburguerButton;
    }

    public JFXButton getUserProfileButton() {
        return userProfileButton;
    }

    public JFXButton getUserDataButton() {
        return userDataButton;
    }


    /**
     * Applies Common behaviour to the Hamburguer Button on the deck
     *
     * @param hamburguerButton the button which recieves the funcionality
     */
    public void setHamburguerButton(JFXHamburger hamburguerButton) {
        this.hamburguerButton = hamburguerButton;
        // TODO: Behavioural code
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/deckPopUp.fxml"));
        try {
            JFXPopup popup = new JFXPopup(loader.load());
            hamburguerButton.setOnMouseClicked(event -> {
                popup.show(
                        hamburguerButton,
                        JFXPopup.PopupVPosition.BOTTOM,
                        JFXPopup.PopupHPosition.LEFT,
                        0,
                        0
                );
                popup.setAutoHide(true);
            });
        } catch (IOException e) {
            PiikLogger.getInstance().log(Level.SEVERE, "AbstractDeckController -> setHamburguerButton", e);
        }

    }

    /**
     * Add the function to the button to be executed when the it's pressed
     * @param userProfileButton Button of the userProfile
     */
    public void setUserProfileButton(JFXButton userProfileButton) {
        this.userProfileButton = userProfileButton;
        userProfileButton.setOnAction(this::handleUserButton);
    }


    /**
     * Code to be executed when the window event happened
     * @param event Window event
     */
    private void handleUserButton(ActionEvent event) {
        // Requests the feed controller to update the feed for the new Post to show up
        try {
            ContextHandler.getContext().invokeStage("/gui/fxml/profile/userProfile.fxml",
                    new UserProfileController(ContextHandler.getContext().getCurrentUser()), "User profile");
        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.showAlert(invalidParameters, "Failure updating the feed for the new post");
        }

    }

    /**
     * Add the function to the button to be executed when the it's pressed
     * @param userDataButton Button of the userProfile
     */
    public void setUserDataButton(JFXButton userDataButton) {
        this.userDataButton = userDataButton;
        userDataButton.setOnAction(this::handleUserDataButton);
    }
    /**
     * Code to be executed when the window event happened
     * @param event Window event
     */
    private void handleUserDataButton(Event event) {
        // Requests the feed controller to update the feed for the new Post to show up
        try {
            ContextHandler.getContext().invokeStage("/gui/fxml/profile/userData.fxml", null, "User data");
        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.showAlert(invalidParameters, "Failure updating the feed for the new post");
        }
    }
    /**
     * Add the function to the button to be executed when the it's pressed
     * @param viewNotificationsButton  Button of the userProfile
     */
    public void setViewNotificationsButton(JFXButton viewNotificationsButton) {
        this.viewNotificationsButton = viewNotificationsButton;
        viewNotificationsButton.setOnAction(this::handleNotification);
    }
    /**
     * Code to be executed when the window event happened
     * @param event Window event
     */
    private void handleNotification(Event event){
        User current = ContextHandler.getContext().getCurrentUser();
        try {
            List<Notification> notifications = ApiFacade.getEntrypoint().getSearchFacade().getNotifications(current, current);
            if (notifications.size() > 0) {
                notifications.forEach(this::sendNotification);
            } else {
                JFXSnackbarLayout layout = new JFXSnackbarLayout("There's not notifications for you at the moment", "Piiksuma", Event::consume);
                new JFXSnackbar(deck).enqueue(new JFXSnackbar.SnackbarEvent(layout));
            }
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert(e, "Could not show the notifications");
        }

    }
    /**
     * Sends the notification to the window
     * @param notification Notificacion to be send
     */
    private void sendNotification(Notification notification) {
        JFXSnackbarLayout layout = new JFXSnackbarLayout(notification.getContent(), "!", Event::consume);
        new JFXSnackbar(deck).enqueue(new JFXSnackbar.SnackbarEvent(layout));
    }
}
