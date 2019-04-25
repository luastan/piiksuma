package piiksuma.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class OtherUserProfileController implements Initializable {

    @FXML
    private Label Name;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ContextHandler.getContext().getCurrentUser().setName("OswaldOswin");
        Name.setText(ContextHandler.getContext().getCurrentUser().getName());
    }
}
