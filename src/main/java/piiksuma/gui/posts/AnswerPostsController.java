package piiksuma.gui.posts;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import piiksuma.Post;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AnswerPostsController implements Initializable {

    @FXML
    private JFXMasonryPane postMasonryPane;

    private Post post;

    public AnswerPostsController(Post post) {
        this.post = post;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User current = ContextHandler.getContext().getCurrentUser();
        try {
            ApiFacade.getEntrypoint().getSearchFacade().getAnswers(post).stream().map(PostController::new).forEach(postController -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/post.fxml"));
                    loader.setController(postController);
                    postMasonryPane.getChildren().add(loader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.printStackTrace();
            e.showAlert();
        }
    }
}