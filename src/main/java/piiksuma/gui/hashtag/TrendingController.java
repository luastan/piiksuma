package piiksuma.gui.hashtag;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class TrendingController implements Initializable {

    // The -fx-background-color complains always if the string gets constructed instead of being entirely hardcoded
    // So "-fx-background-color: " + randomColor + ";" doesnt even compile =)
    private static String[] styles = {
            "-fx-background-color: #119DA4;",
            "-fx-background-color: #FFC857;",
            "-fx-background-color: #FFFD82;",
            "-fx-background-color: #EA526F;",
            "-fx-background-color: #7DDF64;",
            "-fx-background-color: #D7B7FD;",
            "-fx-background-color: #86FFEA;"
    };

    public JFXMasonryPane hasthags;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        User current = ContextHandler.getContext().getCurrentUser();
        try {
            ApiFacade.getEntrypoint().getSearchFacade().getTrendingTopics(40, current).stream().map(HashtagPreviewController::new).forEach(controller -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/hashtags/hashtagPreview.fxml"));
                loader.setController(controller);
                try {
                    Node newHashtag = loader.load();
                    newHashtag.setStyle(styles[new Random().nextInt(styles.length)]);
                    hasthags.getChildren().add(newHashtag);
                } catch (IOException e) {
                    // TODO handle the exception
                    e.printStackTrace();
                }
            });
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert(e, "Faliure loadiang the trendig stage");
        }
    }
}
