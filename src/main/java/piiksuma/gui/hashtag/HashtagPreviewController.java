package piiksuma.gui.hashtag;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import piiksuma.Hashtag;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class HashtagPreviewController implements Initializable {

    private Hashtag hashtag;

    @FXML
    private StackPane container;

    @FXML
    private Label hashtagText;

    public HashtagPreviewController(Hashtag hashtag) {
        this.hashtag = hashtag;
    }

    /**
     * Inits the window components
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hashtagText.setText(hashtag.getName());
        container.setOnMouseClicked(this::handleHashtagClick);
    }

    /**
     * Sets up what happen with a clicl on the window
     * @param event Event which happen in the window
     */
    private void handleHashtagClick(Event event) {
        try {
            ContextHandler.getContext().invokeStage("/gui/fxml/hashtags/hashtag.fxml", new HashtagController(hashtag), "Hashtag");
        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.showAlert(invalidParameters, "Failure loading de hashtag stage");
        }
    }
}
