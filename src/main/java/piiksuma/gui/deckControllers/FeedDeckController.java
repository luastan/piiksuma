package piiksuma.gui.deckControllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXHamburger;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FeedDeckController extends AbstractDeckController implements Initializable {

    @FXML
    private JFXButton mainButton;

    @FXML
    private JFXHamburger hamburguerButton;

    @FXML
    private JFXButton userButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Common deck implementation
        super.setHamburguerButton(hamburguerButton);

        // TODO feed deck
        FontAwesomeIconView buttonIcon = new FontAwesomeIconView(FontAwesomeIcon.PLUS);
        buttonIcon.getStyleClass().add("deck-button-graphic");
        mainButton.setGraphic(buttonIcon);
        mainButton.setOnAction(this::handleNewPostEvent);
        userButton.setOnAction(this::handleUserButton);
    }


    /**
     * Defines the code to be executed when the create Post button gets pressed
     *
     * @param event Event raised by the user interaction
     */
    private void handleNewPostEvent(ActionEvent event) {
        // TODO: Create a new Post. Which should be another method that might be better placed in another class.

        // Requests the feed controller to update the feed for the new Post to show up
        Stage searchStage = new Stage();

        try {
            ContextHandler.getContext().register("Create Post", searchStage);
        } catch (PiikInvalidParameters e) {
            e.printStackTrace();
            return;
        }
        // Stage configuration
        searchStage.setTitle("Create Post");
        searchStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/createPost.fxml"));
        JFXDecorator decorator;

        try {
            decorator = new JFXDecorator(searchStage, loader.load(), false, false, true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(decorator, 450, 550);


        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm(),
                getClass().getResource("/gui/css/main.css").toExternalForm()
        );
        searchStage.initModality(Modality.WINDOW_MODAL);
        searchStage.initOwner(ContextHandler.getContext().getStage("primary"));
        searchStage.setScene(scene);
        // Show and wait till it closes
        searchStage.showAndWait();
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
        searchStage.showAndWait();
    }
}
