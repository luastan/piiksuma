package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CreatePostController implements Initializable {

    @FXML
    private JFXButton multimediaButton;

    @FXML
    private ImageView boxImage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize Profile view & controller
        multimediaButton.setOnAction(this::handleMultimediaButton);
    }

    private void handleMultimediaButton(Event event) {
        // Creating window to choose image/video
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add multimedia");
        File imagen = fileChooser.showOpenDialog(null);

        // If a file has been chosen
        if (imagen != null) {
            boxImage.setImage(new Image(imagen.toURI().toString()));
        }
    }
}
