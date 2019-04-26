package piiksuma.gui.deckControllers;

import com.jfoenix.controls.JFXButton;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import piiksuma.gui.ContextHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class StatsController implements Initializable {
    @FXML
    private Label maxLikeIt;

    @FXML
    private Label maxLoveIt;

    @FXML
    private Label maxHateIt;

    @FXML
    private Label maxMadeMeAngry;

    @FXML
    private Label followers;

    @FXML
    private Label following;

    @FXML
    private Label followback;

    @FXML
    private JFXButton closeButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        closeButton.setOnAction(this::handleClose);
    }

    private void handleClose(Event event){
        ContextHandler.getContext().getStage("Statistics").close();
    }
}
