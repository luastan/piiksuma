package piiksuma.gui.hashtag;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TrendingController implements Initializable {


    public JFXMasonryPane hasthags;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User current = ContextHandler.getContext().getCurrentUser();
        try {
            ApiFacade.getEntrypoint().getSearchFacade().getTrendingTopics(40, current).stream().map(HashtagPreviewController::new).forEach(controller -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/hashtags/hashtagPreview.fxml"));
                loader.setController(controller);
                try {
                    hasthags.getChildren().add(loader.load());
                } catch (IOException e) {
                    // TODO handle the exception
                    e.printStackTrace();
                }
            });
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert();
        }
    }
}
