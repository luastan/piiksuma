package piiksuma.gui;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import piiksuma.User;

import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ResourceBundle;

public class UserDataController implements Initializable {
    @FXML
    private JFXTextField userId;
    @FXML
    private JFXTextField userName;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXComboBox<String> genderBox;
    @FXML
    private JFXTextField home;
    @FXML
    private JFXTextField province;
    @FXML
    private JFXTextField country;
    @FXML
    private JFXTextField birthday;
    @FXML
    private JFXTextField religion;
    @FXML
    private JFXTextField job;
    @FXML
    private JFXTextArea description;
    @FXML
    private JFXButton update;
    @FXML
    private JFXListView<String> telephoneList;
    @FXML
    private JFXButton addTelephone;
    @FXML
    private JFXButton removeTelephone;
    @FXML
    private JFXTextField emotionalSituation;
    @FXML
    private JFXTextField birthplace;
    @FXML
    private JFXTextField city;
    @FXML
    private ImageView imageView;
    @FXML
    private JFXButton multimediaButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       // User user = ContextHandler.getContext().getCurrentUser();
        User user = new User("Francisco Javier", "Cardama", "francardama@gmail.com");

        user.setBirthday(Timestamp.from(Instant.now()));
        user.setPass("pass1");
        user.setGender("O");
        user.setDescription("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        user.addPhone("12345");
        user.addPhone("573643736");

        genderBox.setItems(FXCollections.observableArrayList("M", "V", "O"));
        //We add to the fields all the info about the user
        userId.setText(user.getId());
        //WE dont let the user to changue his id
        userId.setDisable(true);
        userName.setText(user.getName());
        email.setText(user.getEmail());
        password.setText(user.getPass());
        home.setText(user.getHome());
        city.setText(user.getCity());
        birthplace.setText(user.getBirthplace());
        province.setText(user.getProvince());
        country.setText(user.getCountry());
        job.setText(user.getJob());
        emotionalSituation.setText(user.getEmotionalSituation());
        description.setText(user.getDescription());
        religion.setText(user.getReligion());
        genderBox.getSelectionModel().select(user.getGender());

        for(String telephone: user.getPhones()){
            telephoneList.getItems().add(telephone);
        }

        addTelephone.setOnAction(this::handleAddTelephone);

        removeTelephone.setOnAction(this::handleRemove);

        telephoneList.setEditable(true);
        telephoneList.setCellFactory(TextFieldListCell.forListView());
        multimediaButton.setOnAction(this::handleMultimediaButton);


    }

    private void handleMultimediaButton(Event event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Escoja una imagen de fondo (150x65)");
        File imagen = fileChooser.showOpenDialog(null);

        // Si el usuario ha escogido una imagen
        if (imagen != null) {
            imageView.setImage(new Image(imagen.toURI().toString()));
        }

        // Si el usuario quiere provocar un SEGFAULT
        else {
            System.out.println("Operación cancelada");

        }
    }

    private void handleRemove(Event event) {
        if (telephoneList.getSelectionModel().getSelectedIndex() == -1) {
            return;
        }

        telephoneList.getItems().remove(telephoneList.getSelectionModel().getSelectedIndex());
        telephoneList.getSelectionModel().selectPrevious();
        telephoneList.scrollTo(telephoneList.getSelectionModel().getSelectedIndex()-1);
        telephoneList.layout();
    }

    private void handleAddTelephone(Event event) {

        telephoneList.getItems().add("Edit!");
        telephoneList.scrollTo(telephoneList.getItems().size() - 1);
        telephoneList.layout();
        telephoneList.edit(telephoneList.getItems().size() - 1);

    }
}
