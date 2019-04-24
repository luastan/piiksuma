package piiksuma.gui.deckControllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import piiksuma.gui.ContextHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class FeedDeckController extends AbstractDeckController implements Initializable {

    @FXML
    private JFXButton mainButton;

    @FXML
    private JFXHamburger hamburguerButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Common deck implementation
        super.setHamburguerButton(hamburguerButton);

        // TODO feed deck
        FontAwesomeIconView buttonIcon = new FontAwesomeIconView(FontAwesomeIcon.PLUS);
        buttonIcon.getStyleClass().add("deck-button-graphic");
        mainButton.setGraphic(buttonIcon);
        mainButton.setOnAction(FeedDeckController::handleNewPostEvent);
    }


    /**
     * Defines the code to be executed when the create Post button gets pressed
     *
     * @param event Event raised by the user interaction
     */
    private static void handleNewPostEvent(ActionEvent event) {
        // TODO: Create a new Post. Which should be another method that might be better placed in another class.

        // Requests the feed controller to update the feed for the new Post to show up
        ContextHandler.getContext().getFeedController().updateFeed();

        // Event gets consumed sincee it's no longer needed
        event.consume();
    }
}
