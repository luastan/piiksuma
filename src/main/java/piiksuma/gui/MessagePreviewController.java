package piiksuma.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import piiksuma.Message;
import piiksuma.User;
import piiksuma.Utilities.PiikLogger;
import piiksuma.gui.profiles.ProfilePreviewController;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;


public class MessagePreviewController implements Initializable {

    private Message message;
    private User peer;

    @FXML
    private Label username;
    @FXML
    private Label userId;
    @FXML
    private Label lastMessage;
    @FXML
    private Label elapsedTime;
    @FXML
    private Label hiddenUserId;

    @FXML
    private StackPane profilePicture;

    public MessagePreviewController(Message message, User peer) {
        this.message = message;
        this.peer = peer;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (message != null) {
            lastMessage.setText(message.getText());
            elapsedTime.setText(message.getDate().toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        if (peer != null) {
            userId.setText(peer.getId());
            username.setText(peer.getName());
            hiddenUserId.setText(peer.getId());
        }
        // TODO: Bind profiles picture from the peer
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/profile/profilePreview.fxml"));
        loader.setController(new ProfilePreviewController(peer));
        try {
            profilePicture.getChildren().add(loader.load());
        } catch (IOException e) {
            PiikLogger.getInstance().log(Level.SEVERE, "MessagePreviewController -> initializeForward", e);
            Alert.newAlert().setHeading("Image error").addText("Failure loading the profile preview").addCloseButton().show();
        }
    }

    public User getPeer() {
        return peer;
    }

    public void setPeer(User peer) {
        this.peer = peer;
    }
}
