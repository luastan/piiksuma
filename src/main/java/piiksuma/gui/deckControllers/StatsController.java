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
    private Label userName;

    @FXML
    private Label countLikeIt;

    @FXML
    private Label countLoveIt;

    @FXML
    private Label countHateIt;

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
            userName.setText(user.getId());
            countHateIt.setText("" + statistics.getCountHateIt());
            countLikeIt.setText("" + statistics.getCountLikeIt());
            countLoveIt.setText("" + statistics.getCountLoveIt());
            maxMadeMeAngry.setText("" + statistics.getCountMakesMeAngry());
            followers.setText("" + statistics.getFollowers());
            following.setText("" + statistics.getFollowing());
            followBack.setText("" + statistics.getFollowBack());
        } catch (PiikException e) {
            e.showAlert(e, "Failure loading the current user statistics");
        }
    }

    private void handleClose(Event event) {
        ContextHandler.getContext().getStage("Statistics").close();
    }
}
