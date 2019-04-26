package piiksuma.gui;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.TextFieldListCell;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikException;

import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
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
    private JFXButton register;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Add options to the comboBox
        genderBox.setItems(FXCollections.observableArrayList("M", "V", "O"));

        register.setOnAction(this::handleRegister);

        addTelephone.setOnAction(this::handleAddTelephone);

        removeTelephone.setOnAction(this::handleRemove);

        telephoneList.setEditable(true);
        telephoneList.setCellFactory(TextFieldListCell.forListView());
    }

    private void handleRemove(Event event) {
        if (telephoneList.getSelectionModel().getSelectedIndex() == -1) {
            return;
        }
        telephoneList.getItems().remove(telephoneList.getSelectionModel().getSelectedIndex());
        telephoneList.layout();
    }

    private void handleAddTelephone(Event event) {

        telephoneList.getItems().add("Edit!");
        telephoneList.scrollTo(telephoneList.getItems().size() - 1);
        telephoneList.layout();
        telephoneList.edit(telephoneList.getItems().size() - 1);

    }

    private void handleRegister(Event event) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date;
        try {
            date = dateFormat.parse(birthday.getText());
        } catch (ParseException e) {
            System.out.println("ERROR DATE");
            return;
        }

        if (!checkFields()) {
            //TODO display an alert
            System.out.println("ERROR FIELDS");
            return;
        }

        User user = new User(userName.getText(), userId.getText(), email.getText());
        user.setBirthday(new Timestamp(date.getTime()));
        user.setPass(password.getText());
        user.setGender(genderBox.getSelectionModel().getSelectedItem());
        user.setDescription(description.getText());
        user.setHome(home.getText());
        user.setCountry(country.getText());
        user.setProvince(province.getText());
        user.setReligion(religion.getText());
        user.setJob(job.getText());
        user.setBirthplace(birthplace.getText());
        user.setCity(city.getText());
        user.setEmotionalSituation(emotionalSituation.getText());

        //TODO add telephones to an arraylist or somethng
        try {
            ApiFacade.getEntrypoint().getInsertionFacade().createUser(user);
        } catch (PiikException e) {
            //TODO handle exceptions properly
            System.out.println(e.getMessage());
            return;
        }
    }

    private boolean checkFields() {
        if (userId.getText().isEmpty() || userName.getText().isEmpty() || email.getText().isEmpty()
                || password.getText().isEmpty() || birthday.getText().isEmpty()) {
            return false;
        }
        return true;
    }

}
