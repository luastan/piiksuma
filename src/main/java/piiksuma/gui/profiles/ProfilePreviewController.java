package piiksuma.gui.profiles;

import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;

import javax.imageio.ImageIO;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfilePreviewController implements Initializable {

    private User profile;

    @FXML
    private ImageView profilePicture;


    public ProfilePreviewController(User profile) {
        this.profile = profile;
    }

    private void handle(MouseEvent event) {
        try {
            ContextHandler.getContext().invokeStage("/gui/fxml/profile/userProfile.fxml",
                    new UserProfileController(profile), "User profile");
        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.showAlert(invalidParameters, "Failure loading the userProfile stage");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Tooltip tooltip = new Tooltip(profile.getName());
        Node graphic = GlyphsBuilder.create(FontAwesomeIconView.class)
                .glyph(FontAwesomeIcon.INFO)
                .style("-fx-fill: -white-high-emphasis;")
                .build();
        tooltip.setGraphic(graphic);
        Tooltip.install(profilePicture, tooltip);
        profilePicture.setOnMouseClicked(this::handle);

        // Multimedia insertion
        if (profile.getMultimedia() != null && profile.getMultimedia().getHash() != null
                && !profile.getMultimedia().getHash().isEmpty()) {
            if (profile.getMultimedia().getUri() == null || profile.getMultimedia().getUri().isEmpty()) {
                try {
                    profile.setMultimedia(ApiFacade.getEntrypoint().getSearchFacade()
                            .getMultimedia(profile.getMultimedia(), ContextHandler.getContext().getCurrentUser()));
                } catch (PiikInvalidParameters | PiikDatabaseException piikInvalidParameters) {
                    piikInvalidParameters.printStackTrace();
                }
            }
            profilePicture.setImage(new Image(profile.getMultimedia().getUri()));
        } else {
            profilePicture.setImage(new Image(new File("src/main/resources/imagenes/huevo.png").toURI().toString()));
        }

        // Profile image into a circle

        Circle clip = new Circle(profilePicture.getFitWidth() / 2.1);
        clip.setCenterX(profilePicture.getFitWidth() / 2);
        clip.setCenterY(profilePicture.getFitHeight() / 2);
        profilePicture.setClip(clip);

        profilePicture.setVisible(true);
    }


}
