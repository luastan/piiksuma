package piiksuma.gui.deckControllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXPopup;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;
import piiksuma.gui.profiles.UserProfileController;

import java.io.IOException;

public class AbstractDeckController {
    private JFXHamburger hamburguerButton;

    private JFXButton userProfileButton;

    private JFXButton userDataButton;

    private JFXButton viewNotificationsButton;

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
            // TODO: Handle the exception
            e.printStackTrace();
        }

    }

    public void setUserProfileButton(JFXButton userProfileButton) {
        this.userProfileButton = userProfileButton;
        userProfileButton.setOnAction(this::handleUserButton);
    }

    private void handleUserButton(ActionEvent event) {
        // Requests the feed controller to update the feed for the new Post to show up
        try {
            ContextHandler.getContext().invokeStage("/gui/fxml/profile/userProfile.fxml",
                    new UserProfileController(ContextHandler.getContext().getCurrentUser()), "User profile");
        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.showAlert();
        }

    }

    public void setUserDataButton(JFXButton userDataButton) {
        this.userDataButton = userDataButton;
        userDataButton.setOnAction(this::handleUserDataButton);
    }

    private void handleUserDataButton(Event event) {
        // Requests the feed controller to update the feed for the new Post to show up
        try {
            ContextHandler.getContext().invokeStage("/gui/fxml/profile/userData.fxml", null, "User data");
        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.showAlert();
        }
    }

    public void setViewNotificationsButton(JFXButton viewNotificationsButton) {
        this.viewNotificationsButton = viewNotificationsButton;

        viewNotificationsButton.setOnAction(this::handleNotification);
    }

    private void handleNotification(Event event){
        try {
            ContextHandler.getContext().invokeStage("/gui/fxml/notifications.fxml", null, "User notifications");
        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.showAlert();
        }
    }
}
