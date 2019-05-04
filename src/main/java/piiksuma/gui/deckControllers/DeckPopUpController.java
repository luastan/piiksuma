package piiksuma.gui.deckControllers;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import piiksuma.UserType;
import piiksuma.Utilities.PiikLogger;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.Alert;
import piiksuma.gui.ContextHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class DeckPopUpController implements Initializable {
    @FXML
    private JFXListView popup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Select the items to be shown on the deck
     */
    public void selectItem() {
        Stage stage = new Stage();
        FXMLLoader loader;
        JFXDecorator decorator;

        if (popup.getSelectionModel().getSelectedIndex() == 0) {
            /* Log-out */
            ContextHandler.getContext().setCurrentUser(null);
            ContextHandler.getContext().getCurrentStage().close();
            // Now we show the login window
            stage.setTitle("Login");
            loader = new FXMLLoader(getClass().getResource("/gui/fxml/login.fxml"));
            try {
                decorator = new JFXDecorator(stage, loader.load(), false, false, true);
            } catch (IOException e) {
                return;
            }

            // Scene definition & binding to the Primary Stage
            Scene scene = new Scene(decorator, 450, 550);
            stage.setScene(scene);
            scene.getStylesheets().addAll(
                    getClass().getResource("/gui/css/global.css").toExternalForm(),
                    getClass().getResource("/gui/css/main.css").toExternalForm()
            );

            try {
                ContextHandler.getContext().register("login", stage);
            } catch (PiikInvalidParameters piikInvalidParameters) {
                piikInvalidParameters.showAlert(piikInvalidParameters, "Failure loading the login stage");
            }
            // Show
            stage.show();
            return;
        }

        switch (popup.getSelectionModel().getSelectedIndex()) {
            case 1:
                /* Show achievements */
                stage.setTitle("Achievements");
                loader = new FXMLLoader(getClass().getResource("/gui/fxml/achievements.fxml"));
                break;
            case 2:
                /* Show statistics */
                stage.setTitle("Statistics");
                loader = new FXMLLoader(getClass().getResource("/gui/fxml/stats.fxml"));
                break;
            case 3:
                if (ContextHandler.getContext().getCurrentUser().getType().equals(UserType.user)) {
                    Alert.newAlert().setHeading("Forbidden").addText("Only adiminstrators are allowed to view all the tickets").addCloseButton().show();
                    return;
                }

                try {
                    ContextHandler.getContext().invokeStage("/gui/fxml/tickets/adminTickets.fxml", null, "Tickets");
                } catch (PiikInvalidParameters invalidParameters) {
                    invalidParameters.showAlert(invalidParameters, "Failure loading the adminTickets stage");
                }
                return;
            default:
                return;
        }

        try {
            ContextHandler.getContext().register(stage.getTitle(), stage);
            decorator = new JFXDecorator(
                    stage,
                    loader.load(),
                    false,
                    false,
                    true
            );
        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.showAlert(invalidParameters, "Failure loading the fxml");
            return;
        } catch (IOException e) {
            PiikLogger.getInstance().log(Level.SEVERE, "DeckPopUpController -> selectItem", e);
            return;
        }
        Scene scene = new Scene(decorator);
        scene.getStylesheets().addAll(ContextHandler.getContext().getCurrentStage().getScene().getStylesheets());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(ContextHandler.getContext().getStage("primary"));
        stage.setScene(scene);

        // Show and wait
        stage.show();
    }
}
