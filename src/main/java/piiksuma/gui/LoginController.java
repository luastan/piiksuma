package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import piiksuma.exceptions.PiikInvalidParameters;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private JFXTextField user;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXButton logIn;
    @FXML
    private JFXButton register;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        register.setOnAction(this::handleRegister);
    }

    private void handleRegister(Event event){

        Stage registerStage = new Stage();

        try {
            ContextHandler.getContext().register("register", registerStage);
        }catch(PiikInvalidParameters e){
            e.printStackTrace();
            return;
        }
        // Stage configuration
        registerStage.setTitle("Register");
        registerStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/register.fxml"));
        JFXDecorator decorator;

        try {
            decorator = new JFXDecorator(registerStage, loader.load(), false, false, true);
        }catch (IOException e){
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(decorator, 450, 550);


        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm(),
                getClass().getResource("/gui/css/main.css").toExternalForm()
        );
        registerStage.initModality(Modality.WINDOW_MODAL);
        registerStage.initOwner(ContextHandler.getContext().getStage("primary"));
        registerStage.setScene(scene);
        // Show and wait till it closes
        registerStage.showAndWait();
    }
}
