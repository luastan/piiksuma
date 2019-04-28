package piiksuma.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import piiksuma.Post;

import java.net.URL;
import java.util.ResourceBundle;

public class SearchedPostController implements Initializable {

    @FXML
    private Label search;

    private Post post;

    public SearchedPostController(Post post) {
        this.post = post;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        search.setText(post.getText());
    }
}
