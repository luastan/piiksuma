package piiksuma.gui.deckControllers;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import piiksuma.Achievement;
import piiksuma.User;
import piiksuma.UserType;
import piiksuma.Utilities.PiikLogger;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.AchievementController;
import piiksuma.gui.ContextHandler;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class AchievementsController implements Initializable {

    // Up and down scroll
    @FXML
    private ScrollPane eventScrollPane;

    // Achievement auto-arranging
    @FXML
    private JFXMasonryPane eventMasonryPane;

    // Shown achievements
    private ObservableList<Achievement> achievements;


    /* Methods */

    /**
     * Initializes a window that shows a group of achievements
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        achievements = FXCollections.observableArrayList();

        // Registers itself
        ContextHandler.getContext().setAchievementsController(this);

        // Achievements container configuration
        setUpAchievementsListener();

        try {
            // Update its contents
            updateAchievements();
        } catch (PiikDatabaseException e) {
            e.showAlert(e, "Failure updating achievements");
        }
    }

    /**
     * Function to update the shown achievements
     *
     * @throws PiikDatabaseException
     */
    private void updateAchievements() throws PiikDatabaseException {

        // Erase all contained achievements
        achievements.clear();

        // All the achievements that the current user has unlocked
        User user = ContextHandler.getContext().getCurrentUser();

        // Retrieving unlock achievements from the database and their unlock dates
        try {
            List<Achievement> unlockedAchievements = ApiFacade.getEntrypoint().getSearchFacade().getAchievements(user,
                    user);

            Map<String, Timestamp> unlockDates = ApiFacade.getEntrypoint().getSearchFacade().getUnlockDates(user, user);

            for (Achievement achievement : unlockedAchievements) {
                achievement.setUnlockDate(unlockDates.get(achievement.getId()));
            }

            // Storing retrieved achievements to be shown
            achievements.addAll(unlockedAchievements);

        } catch (PiikInvalidParameters piikInvalidParameters) {
            piikInvalidParameters.showAlert(piikInvalidParameters, "Failure retrieving unlocked achievements");
        }
    }

    /**
     * Defines which actions to perform when the achievements container changes its state
     */
    private void setUpAchievementsListener() {

        // Change takes place
        achievements.addListener((ListChangeListener<? super Achievement>) change -> {
            // Visual representation gets cleared
            eventMasonryPane.getChildren().clear();
            // Each achievement is represented
            achievements.forEach(this::insertAchievement);
        });
    }

    /**
     * Adds an achievement representation
     *
     * @param achievement achievement whose data will be represented
     */
    private void insertAchievement(Achievement achievement) {

        // Achievement container
        FXMLLoader achievementLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/achievement.fxml"));
        achievementLoader.setController(new AchievementController(achievement));

        // Loads the container in the panel
        try {
            eventMasonryPane.getChildren().add(achievementLoader.load());
        } catch (IOException e) {
            PiikLogger.getInstance().log(Level.SEVERE, "Achievements Controller -> insertAchievement", e);
        }

        eventScrollPane.requestLayout();
        eventScrollPane.requestFocus();
    }
}
