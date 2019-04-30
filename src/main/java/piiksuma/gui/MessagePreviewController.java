package piiksuma.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import piiksuma.Message;
import piiksuma.User;

import java.net.URL;
import java.util.ResourceBundle;


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
    private Label hiddenUserId;

    @FXML
    private ImageView profilePicture;

    public MessagePreviewController(Message message, User peer) {
        this.message = message;
        this.peer = peer;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (message != null) {
            lastMessage.setText(message.getText());
        }
        if (peer != null) {
            userId.setText(peer.getId());
            username.setText(peer.getName());
            hiddenUserId.setText(peer.getId());
        }
        // TODO: Bind profiles picture from the peer
    }

    public User getPeer() {
        return peer;
    }

    public void setPeer(User peer) {
        this.peer = peer;
    }
}
