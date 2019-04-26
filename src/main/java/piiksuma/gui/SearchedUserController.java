package piiksuma.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import piiksuma.User;

import java.net.URL;
import java.util.ResourceBundle;

public class SearchedUserController implements Initializable {


    private User user;

    @FXML
    private Label search;

    public SearchedUserController(User user) {
        this.user = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        search.setText(user.getName());
    }
}
