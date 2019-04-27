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
    private Label id;

    @FXML
    private Label name;

    @FXML
    private Label description;


    public SearchedUserController(User user) {
        this.user = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name.setText(user.getName());
        id.setText(user.getId());
        description.setText(user.getDescription());
    }
}
