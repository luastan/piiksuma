package piiksuma.gui.deckControllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXPopup;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;

import java.io.IOException;

public class AbstractDeckController {
    private JFXHamburger hamburguerButton;

    private JFXButton userProfileButton;

    private JFXButton userDataButton;

    public AbstractDeckController() {
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
        Stage searchStage = new Stage();

        try {
            ContextHandler.getContext().register("User Profile", searchStage);
        } catch (PiikInvalidParameters e) {
            e.printStackTrace();
            return;
        }
        // Stage configuration
        searchStage.setTitle("UserProfile");
        searchStage.setResizable(false);
        FXMLLoader loader;
        //This condition is to determinate if the user wants to see his own profile or another user's profile
        //It must be completed later, this version is only for testing.
        if (true) {
            loader = new FXMLLoader(getClass().getResource("/gui/fxml/userProfile.fxml"));

        } else {
            loader = new FXMLLoader(getClass().getResource("/gui/fxml/otherUserProfile.fxml"));
        }
        JFXDecorator decorator;
        try {
            decorator = new JFXDecorator(searchStage, loader.load(), false, false, true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(decorator, 450, 800);


        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm(),
                getClass().getResource("/gui/css/main.css").toExternalForm()
        );
        searchStage.initModality(Modality.WINDOW_MODAL);
        searchStage.initOwner(ContextHandler.getContext().getStage("primary"));
        searchStage.setScene(scene);
        // Show and wait till it closes
        searchStage.show();
    }

    public void setUserDataButton(JFXButton userDataButton) {
        this.userDataButton = userDataButton;
        userDataButton.setOnAction(this::handleUserDataButton);
    }

    private void handleUserDataButton(Event event){
        // Requests the feed controller to update the feed for the new Post to show up
        Stage searchStage = new Stage();

        try {
            ContextHandler.getContext().register("User Profile", searchStage);
        } catch (PiikInvalidParameters e) {
            e.printStackTrace();
            return;
        }
        // Stage configuration
        searchStage.setTitle("UserProfile");
        searchStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/userData.fxml"));

        JFXDecorator decorator;
        try {
            decorator = new JFXDecorator(searchStage, loader.load(), false, false, true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(decorator, 450, 800);


        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm(),
                getClass().getResource("/gui/css/main.css").toExternalForm()
        );
        searchStage.initModality(Modality.WINDOW_MODAL);
        searchStage.initOwner(ContextHandler.getContext().getStage("primary"));
        searchStage.setScene(scene);
        // Show and wait till it closes
        searchStage.show();
    }
}
