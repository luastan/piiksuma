package piiksuma.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import piiksuma.Post;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.net.URL;
import java.util.ResourceBundle;

public class PostController implements Initializable {

    @FXML
    private Label authorId;

    @FXML
    private Label authorName;

    @FXML
    private Label postContent;


    private Post post;

    public PostController(Post post) {
        this.post = post;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        User author = post.getAuthor();

        try {
            author = ApiFacade.getEntrypoint().getSearchFacade().getUser(
                    author, ContextHandler.getContext().getCurrentUser());
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.printStackTrace();
        }

        post.setAuthor(author);

        postContent.setText(post.getText());
        authorName.setText(post.getAuthor().getName());
        authorId.setText(post.getAuthor().getId());
    }
}
