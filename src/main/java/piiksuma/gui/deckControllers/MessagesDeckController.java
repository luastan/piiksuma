package piiksuma.gui.deckControllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXHamburger;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MessagesDeckController extends AbstractDeckController implements Initializable {

    @FXML
    private JFXButton mainButton;

    @FXML
    private JFXHamburger hamburguerButton;

    @FXML
    private JFXButton messageButton;

    @FXML
    private JFXButton userDataButton;

    @FXML
    private JFXButton userButton;

    @FXML
    private JFXButton viewNotificationsButton;

    /**
     * Init the window components
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Common deck implementation
        super.setHamburguerButton(hamburguerButton);
        super.setUserProfileButton(userButton);
        super.setUserDataButton(userDataButton);
        super.setViewNotificationsButton(viewNotificationsButton);


        FontAwesomeIconView buttonIcon = new FontAwesomeIconView(FontAwesomeIcon.ENVELOPE_OPEN);
        buttonIcon.getStyleClass().add("deck-button-graphic");
        mainButton.setGraphic(buttonIcon);
        mainButton.setOnAction(this::handleMessageButton);
    }

    /**
     * Code to be executed when the window event happens
     * @param event Event which happen in the window
     */
    public void handleMessageButton (ActionEvent event){
        Stage searchStage = new Stage();

        try {
            ContextHandler.getContext().register("startChat", searchStage);
        } catch (PiikInvalidParameters e) {
            e.showAlert();
            return;
        }
        // Stage configuration
        searchStage.setTitle("Start chat");
        searchStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/startChat.fxml"));
        JFXDecorator decorator;

        try {
            decorator = new JFXDecorator(searchStage, loader.load(), false, false, true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(decorator, 400, 300);


        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm(),
                getClass().getResource("/gui/css/main.css").toExternalForm()
        );
        searchStage.initModality(Modality.WINDOW_MODAL);
        searchStage.initOwner(ContextHandler.getContext().getStage("primary"));
        searchStage.setScene(scene);
        // Show and wait till it closes
        searchStage.show();

    }

}
