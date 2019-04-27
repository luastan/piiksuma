package piiksuma.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import piiksuma.Achievement;
import piiksuma.Event;

import java.net.URL;
import java.util.ResourceBundle;

public class AchievementController implements Initializable {

    /* Associated FXML's attributes */

    @FXML
    private Label achievementName;

    @FXML
    private Label achievementDescription;

    @FXML
    private Label unlockDate;


    /* Achievement to represent */

    private Achievement achievement;


    /* Constructor */

    public AchievementController(Achievement achievement) {
        this.achievement = achievement;
    }


    /* Methods */

    /**
     * Sets achievement's data to the given data
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        achievementName.setText(achievement.getName());
        achievementDescription.setText(achievement.getDescription());
        unlockDate.setText(achievement.getUnlockDate().toString());
    }
}
