package piiksuma.gui.deckControllers;

import com.jfoenix.controls.JFXButton;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import piiksuma.Statistics;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikException;
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
    private Label followBack;

    @FXML
    private JFXButton closeButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        closeButton.setOnAction(this::handleClose);

        searchStats();
    }

    private void searchStats() {
        User user = ContextHandler.getContext().getCurrentUser();

        try {
            Statistics statistics = ApiFacade.getEntrypoint().getSearchFacade().getUserStatistics(user, user);
            maxHateIt.setText(""+statistics.getMaxHateIt());
            maxLikeIt.setText(""+statistics.getMaxLikeIt());
            maxLoveIt.setText(""+statistics.getMaxLoveIt());
            maxMadeMeAngry.setText(""+statistics.getMaxMakesMeAngry());
            followers.setText(""+statistics.getFollowers());
            following.setText(""+statistics.getFollowing());
            followBack.setText(""+statistics.getFollowBack());
        } catch (PiikException e) {
            //TODO logger and alert
            System.out.println(e.getMessage());
            return;
        }
    }

    private void handleClose(Event event) {
        ContextHandler.getContext().getStage("Statistics").close();
    }
}
