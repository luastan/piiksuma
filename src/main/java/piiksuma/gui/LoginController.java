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
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private JFXTextField userId;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXButton logIn;
    @FXML
    private JFXButton register;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        register.setOnAction(this::handleRegister);
        logIn.setOnAction(this::handleLogIn);
    }

    private void handleLogIn(Event event) {

        if (!checkFields()) {
            //TODO alert
            System.out.println("Fields not filled");
            return;
        }
        User user = new User(userId.getText(), password.getText());
        User userLoggin = null;
        try {
            userLoggin = ApiFacade.getEntrypoint().getSearchFacade().login(user);
        } catch (PiikException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (userLoggin == null) {
            //TODO alert with invalid credentials
            System.out.println("User == null");
            return;
        }

        ContextHandler.getContext().setCurrentUser(userLoggin);
    }

    private boolean checkFields() {
        if (userId.getText().isEmpty() || password.getText().isEmpty()) {
            return false;
        }

        return true;
    }

    private void enterMainWindow() {
        Stage mainStage = new Stage();

        try {
            ContextHandler.getContext().register("primary", mainStage);
        }catch (PiikException e){
            //TODO
            return;
        }
        // Stage configuration
        mainStage.setTitle("Piiksuma");
        mainStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/main.fxml"));

        JFXDecorator decorator;

        try {
            decorator = new JFXDecorator(mainStage, loader.load(), false, false, true);
        }catch (IOException e){
            return;
        }

        Scene scene = new Scene(decorator, 450, 800);
        mainStage.setScene(scene);
        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm(),
                getClass().getResource("/gui/css/main.css").toExternalForm()
        );

        // Show
        mainStage.show();
    }

    private void handleRegister(Event event) {

        Stage registerStage = new Stage();

        try {
            ContextHandler.getContext().register("register", registerStage);
        } catch (PiikInvalidParameters e) {
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
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(decorator, 450, 550);


        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm()
        );
        registerStage.initModality(Modality.WINDOW_MODAL);
        registerStage.initOwner(ContextHandler.getContext().getStage("login"));
        registerStage.setScene(scene);
        // Show and wait till it closes
        registerStage.showAndWait();
    }
}
