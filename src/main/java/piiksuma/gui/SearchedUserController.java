package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import piiksuma.User;
import piiksuma.exceptions.PiikInvalidParameters;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SearchedUserController implements Initializable {


    private User user;

    @FXML
    private Label id;

    @FXML
    private Label name;

    @FXML
    private Label description;

    @FXML
    private JFXButton profileButton;


    public SearchedUserController(User user) {
        this.user = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name.setText(user.getName());
        id.setText(user.getId());
        description.setText(user.getDescription());
        profileButton.setOnAction(this::handleProfileButton);
    }

    private void handleProfileButton(Event event){
        Stage searchStage = new Stage();

        try {
            ContextHandler.getContext().register("User Profile", searchStage);
        } catch (PiikInvalidParameters e) {
            e.printStackTrace();
            return;
        }
        // Stage configuration
        searchStage.setTitle("UserProfile");
        searchStage.setResizable(false);
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/gui/fxml/otherUserProfile.fxml"));
        OtherUserProfileController controller = new OtherUserProfileController();
        controller.setUser(user);
        loader.setController(controller);

        JFXDecorator decorator;
        try {
            decorator = new JFXDecorator(searchStage, loader.load(), false, false, true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(decorator, 450, 800);


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
