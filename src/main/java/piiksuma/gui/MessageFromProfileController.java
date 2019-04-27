package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import piiksuma.Message;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.net.URL;
import java.util.ResourceBundle;

public class MessageFromProfileController implements Initializable {

    @FXML
    private JFXButton mainButton;

    @FXML
    private JFXTextField messageField;

    private User user;

    public MessageFromProfileController(User user) {
        this.user = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void handleMainButton(Event event){
        Message message = new Message();
        if (messageField.getText().isEmpty()){
            return;
        }
        message.setText(messageField.getText());
        try {
            ApiFacade.getEntrypoint().getInsertionFacade().createMessage(message,ContextHandler.getContext().getCurrentUser());
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        } catch (PiikInvalidParameters piikInvalidParameters) {
            piikInvalidParameters.printStackTrace();
        }
    }
}
