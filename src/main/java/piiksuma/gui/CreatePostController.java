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
        /*Se cambia la imagen de fases de la representación de la casilla de la celda asociada
         *
         *@param event click que activa el handler
         */
        // Se crea una ventana en la que escoger un archivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Escoja una imagen de fondo (150x65)");
        File imagen = fileChooser.showOpenDialog(null);

        // Si el usuario ha escogido una imagen
        if (imagen != null) {
            boxImage.setImage(new Image(imagen.toURI().toString()));
        }

        // Si el usuario quiere provocar un SEGFAULT
        else {
            System.out.println("Operación cancelada");

        }
    }


}
