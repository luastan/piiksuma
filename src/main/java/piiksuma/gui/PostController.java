package piiksuma.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import piiksuma.Post;

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

        postContent.setText(post.getText());
        authorName.setText(post.getAuthor().getName());
        authorId.setText(post.getAuthor().getId());
    }
}
