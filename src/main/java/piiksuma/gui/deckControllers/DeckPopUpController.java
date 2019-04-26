package piiksuma.gui.deckControllers;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DeckPopUpController implements Initializable {
    @FXML
    private JFXListView popup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void selectItem() {

        if (popup.getSelectionModel().getSelectedIndex() == 0) {
            /* Log-out */
            ContextHandler.getContext().setCurrentUser(null);
            ContextHandler.getContext().stageJuggler();
            return;
        }

        Stage stage = new Stage();
        FXMLLoader loader;
        JFXDecorator decorator;
        switch (popup.getSelectionModel().getSelectedIndex()) {
            case 1:
                /* Show achievements */
                // TODO: Define the fxml and the controller
                stage.setTitle("Title1");
                loader = new FXMLLoader(getClass().getResource("fxml1"));
                break;
            case 2:
                /* Show statistics */
                // TODO: Define the fxml and the controller
                stage.setTitle("Title2");
                loader = new FXMLLoader(getClass().getResource("fxml2"));
                break;
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
            // TODO: Handle the exception
            System.out.println("invalid fxml");
            invalidParameters.printStackTrace();
            return;
        } catch (IOException e) {
            // TODO: Handle the exception
            e.printStackTrace();
            return;
        }
        Scene scene = new Scene(decorator);
        scene.getStylesheets().addAll(ContextHandler.getContext().getCurrentStage().getScene().getStylesheets());
        stage.setScene(scene);
        stage.show();
    }
}
