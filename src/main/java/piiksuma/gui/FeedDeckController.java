package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class FeedDeckController extends AbstractDeckController implements Initializable {

    @FXML
    private Label sampleLabel;

    @FXML
    private JFXButton mainButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO feed deck
        sampleLabel.setText("Using feed deck Controller");
        FontAwesomeIconView buttonIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL);
        buttonIcon.getStyleClass().add("deck-button-graphic");
        mainButton.setGraphic(buttonIcon);

    }
}
