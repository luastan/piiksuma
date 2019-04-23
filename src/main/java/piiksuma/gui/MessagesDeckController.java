package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class MessagesDeckController extends AbstractDeckController implements Initializable {

    @FXML
    private Label sampleLabel;


    @FXML
    private JFXButton mainButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sampleLabel.setText("Using MessagesDeckController");
        FontAwesomeIconView buttonIcon = new FontAwesomeIconView(FontAwesomeIcon.ENVELOPE_OPEN);
        buttonIcon.getStyleClass().add("deck-button-graphic");
        mainButton.setGraphic(buttonIcon);
    }
}
