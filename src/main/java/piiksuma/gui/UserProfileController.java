package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.junit.FixMethodOrder;
import javafx.scene.control.Label;
import piiksuma.User;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {

    @FXML
    private JFXButton backButton;

    @FXML
    private Label Name;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = new User("OswaldOswin","user1","@yahoo.es");
        ContextHandler.getContext().setCurrentUser(user);
        Name.setText(ContextHandler.getContext().getCurrentUser().getName());
    }

    /**
     * Restores the original layout
     *
     * @param event Click Event
     */
    private void backButton(javafx.event.Event event) {
        // Just by selecting another tab will renew the contents
        JFXTabPane tabPane = (JFXTabPane) ContextHandler.getContext().getElement("mainTabPane");
        tabPane.getSelectionModel().select(0);
        event.consume();  // Consumes it just in case another residual handler was listening to it

    }

}
