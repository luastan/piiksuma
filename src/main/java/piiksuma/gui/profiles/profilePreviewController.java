package piiksuma.gui.profiles;

import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import piiksuma.User;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;

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
            ContextHandler.getContext().invokeStage("/gui/fxml/profile/userProfile.fxml", new UserProfileController(profile), "Piiksuma - " + profile.getName());
        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.printStackTrace();
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
    }


}
