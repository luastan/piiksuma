package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import piiksuma.Message;
import piiksuma.User;
import piiksuma.exceptions.PiikInvalidParameters;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MessageController implements Initializable {

    private Message message;

    @FXML
    private Label content;

    @FXML
    private StackPane messageHolder;


    public MessageController(Message message) {
        this.message = message;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User current = ContextHandler.getContext().getCurrentUser();
        if (message.getSender().equals(current)) {
            messageHolder.setAlignment(Pos.CENTER_RIGHT);
        }
    }
}
