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
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;

import javax.imageio.ImageIO;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class profilePreviewController implements Initializable {

    private User profile;

    @FXML
    private ImageView profilePicture;


    public profilePreviewController(User profile) {
        this.profile = profile;
    }

    private void handle(MouseEvent event) {
        try {
            ContextHandler.getContext().invokeStage("/gui/fxml/profile/userProfile.fxml",
                    new UserProfileController(profile), "User profile");
        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.showAlert();
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
        // TODO: Profile image initialization

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

            profilePicture.setImage(new Image(profile.getMultimedia().getUri(), 450, 800,
                    true, true));
            profilePicture.setViewport(new Rectangle2D((profilePicture.getImage().getWidth() - 450) / 2,
                    (profilePicture.getImage().getHeight() - 170) / 2, 450, 170));
        } else {
            profilePicture.setImage(new Image(new File("src/main/resources/imagenes/huevo.png").toURI().toString()));
        }
        profilePicture.setVisible(true);
    }


}
