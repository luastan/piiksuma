package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import piiksuma.*;
import piiksuma.Utilities.PiikLogger;
import piiksuma.Utilities.PiikTextLimiter;
import piiksuma.api.ApiFacade;
import piiksuma.api.MultimediaType;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import javax.activation.MimeTypeParseException;
import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreatePostController implements Initializable {

    @FXML
    private JFXButton postButton;

    @FXML
    private JFXTextArea postText;

    @FXML
    private JFXButton multimediaButton;

    @FXML
    private ImageView boxImage;

    private URI imageURI;

    private Post post;

    private Post postFather;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        postButton.setOnAction(this::publishPost);
        multimediaButton.setOnAction(this::handleMultimediaButton);
        post = new Post();
        post.setAuthor(ContextHandler.getContext().getCurrentUser());
        postText.textProperty().addListener((observable, oldValue, newValue) -> {
            post.setText(newValue);
            postButton.setDisable(!postText.validate());
        });
        PiikTextLimiter.addTextLimiter(postText, 20);
        // Checks if the input is empty
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Field required");
        validator.setIcon(GlyphsBuilder.create(FontAwesomeIconView.class)
                .glyph(FontAwesomeIcon.WARNING)
                .build());
        postText.getValidators().add(validator);
    }

    public Post getPostFather() {
        return postFather;
    }

    public void setPostFather(Post postFather) {
        this.postFather = postFather;
    }

    private void handleMultimediaButton(Event event) {
        // Creating window to choose image/video
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add multimedia");
        File multimedia = fileChooser.showOpenDialog(null);

        if (multimedia == null) {
            return;
        }

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
//----------------------------------------------------------------------------------------------------------------------
/*
        try {
            RandomAccessFile file = new RandomAccessFile(multimedia, "r");
            byte[] imgBytes = new byte[Math.toIntExact(file.length())];
            file.readFully(imgBytes);
            post.setMultimedia(new Multimedia());
            post.getMultimedia().setHash(
                    Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-512").digest(imgBytes))
            );
            this.imageURI = multimedia.toURI();
            post.getMultimedia().setUri(multimedia.toURI().toString());
            post.getMultimedia().setType(MultimediaType.image);
            boxImage.setImage(new Image(post.getMultimedia().getUri()));
            post.getMultimedia().setResolution(String.valueOf((int) boxImage.getImage().getWidth()) + 'x'
                    + (int) boxImage.getImage().getHeight());
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
*/
    }

    private void publishPost(Event event) {
        ArrayList<Hashtag> hashtags = new ArrayList<>();
        Pattern pattern = Pattern.compile("#{1}\\w+");
        Matcher matcher = pattern.matcher(post.getText());

        while (matcher.find()) {
            hashtags.add(new Hashtag(matcher.group()));
        }
        post.setHashtags(hashtags);

        if (postFather != null) {
            post.setFatherPost(postFather);
        }
        // Post gets inserted in the database
        try {
            ApiFacade.getEntrypoint().getInsertionFacade().createPost(post, ContextHandler.getContext().getCurrentUser());
            ContextHandler.getContext().getFeedController().updateFeed();
            // And the window closes
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert();
        }

    }
}
