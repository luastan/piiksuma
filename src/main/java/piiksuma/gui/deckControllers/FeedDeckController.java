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
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;
import piiksuma.gui.posts.CreatePostController;

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

    @FXML
    private JFXButton userDataButton;

    @FXML
    private JFXButton viewNotificationsButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Common deck implementation
        super.setHamburguerButton(hamburguerButton);
        super.setUserProfileButton(userButton);
        super.setUserDataButton(userDataButton);
        super.setViewNotificationsButton(viewNotificationsButton);
        // TODO feed deck
        FontAwesomeIconView buttonIcon = new FontAwesomeIconView(FontAwesomeIcon.PLUS);
        buttonIcon.getStyleClass().add("deck-button-graphic");
        mainButton.setGraphic(buttonIcon);
        mainButton.setOnAction(this::handleNewPostEvent);
    }


    /**
     * Defines the code to be executed when the create Post button gets pressed
     *
     * @param event Event raised by the user interaction
     */
    private void handleNewPostEvent(ActionEvent event) {
        try {
            ContextHandler.getContext().invokeStage("/gui/fxml/createPost.fxml", new CreatePostController(null), "Create Post");

        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.showAlert(invalidParameters, "Failure loading createPost stage");
        }

    }

}
