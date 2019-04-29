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
import java.sql.Timestamp;
import java.util.Date;
import java.util.ResourceBundle;

public class StartChatController implements Initializable {
    @FXML
    private JFXTextField userField;

    @FXML
    private JFXTextField messageField;

    @FXML
    private JFXButton mainButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainButton.setOnAction(this::handleNewMessage);
    }

    private void handleNewMessage(Event event){
        Message message = new Message();
        User userReceiver = new User();
        User current = ContextHandler.getContext().getCurrentUser();

        if(!checkFields()){
            Alert alert = new Alert(ContextHandler.getContext().getStage("startChat"));
            alert.setHeading("Fields empty!");
            alert.addText("Fields cannot be empty");
            alert.addCloseButton();
            alert.show();
            return;
        }

        // TODO comprobar si existe el usuario

        userReceiver.setId(userField.getText());
        message.setText(messageField.getText());
        message.setSender(current);
        Date date = new Date();
        long time = date.getTime();
        message.setDate(new Timestamp(time));
        try {
            message = ApiFacade.getEntrypoint().getInsertionFacade().createMessage(message,current);
            ApiFacade.getEntrypoint().getInsertionFacade().sendPrivateMessage(message, userReceiver, current);
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        } catch (PiikInvalidParameters piikInvalidParameters) {
            piikInvalidParameters.printStackTrace();
        }
    }

    private boolean checkFields(){
        if(userField.getText().isEmpty() || messageField.getText().isEmpty()){
            return false;
        }
        return true;
    }
}
