package piiksuma.gui.posts;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import piiksuma.Multimedia;
import piiksuma.Post;
import piiksuma.Utilities.PiikLogger;
import piiksuma.api.MultimediaType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class CreateAnswerController implements Initializable {


    @FXML
    private JFXTextArea postText;

    @FXML
    private JFXButton multimediaButton;

    @FXML
    private JFXButton postButton;

    @FXML
    private ImageView boxImage;

    private URI imageURI;

    private Post fatherPost;

    private Post post;

    public CreateAnswerController (Post fatherPost){
        this.fatherPost=fatherPost;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.post = new Post();
        multimediaButton.setOnAction(this::handleMultimediaButton);
        postButton.setVisible(true);

    }

    private void handleMultimediaButton(Event event){
        // Creating window to choose image/video
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add multimedia");
        File multimedia = fileChooser.showOpenDialog(null);

        if (multimedia == null) {
            return;
        }
        boxImage.setVisible(true);
        multimediaButton.setVisible(false);

// --------------------------------------------------- Testing zone ----------------------------------------------------
        try {
            // Copy img from source to resource folder
            BufferedImage img = ImageIO.read(multimedia);
            File outputFile = new File("src/main/resources/imagenes/" + multimedia.getName());
            ImageIO.write(img, multimedia.getName().split("\\.")[1], outputFile);
            // Put the new img on multimedia
            RandomAccessFile file = new RandomAccessFile(outputFile, "r");
            byte[] imgBytes = new byte[Math.toIntExact(file.length())];
            file.readFully(imgBytes);
            post.setMultimedia(new Multimedia());
            post.getMultimedia().setHash(
                    Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-512").digest(imgBytes))
            );
            this.imageURI = outputFile.toURI();
            post.getMultimedia().setUri(outputFile.toURI().toString());
            post.getMultimedia().setType(MultimediaType.image);
            boxImage.setImage(new Image(post.getMultimedia().getUri()));
            post.getMultimedia().setResolution(String.valueOf((int) boxImage.getImage().getWidth()) + 'x'
                    + (int) boxImage.getImage().getHeight());
        } catch (Exception e) {
            PiikLogger.getInstance().log(Level.SEVERE, "Can't copy the file to resources", e);
        }
    }
}
