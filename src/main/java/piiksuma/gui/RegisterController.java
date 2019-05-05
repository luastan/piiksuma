package piiksuma.gui;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.TextFieldListCell;
import piiksuma.User;
import piiksuma.Utilities.PiikTextLimiter;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikException;

import java.net.URL;
import java.sql.Timestamp;
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
    private JFXDatePicker birthday;
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

    /**
     * Inits the window components
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Limit text fields
        PiikTextLimiter.addTextLimiter(userId, 32);
        PiikTextLimiter.addTextLimiter(userName, 35);
        PiikTextLimiter.addTextLimiter(email, 35);
        PiikTextLimiter.addTextLimiter(password, 256);
        PiikTextLimiter.addTextLimiter(home, 20);
        PiikTextLimiter.addTextLimiter(province, 30);
        PiikTextLimiter.addTextLimiter(country, 30);
        PiikTextLimiter.addTextLimiter(religion, 20);
        PiikTextLimiter.addTextLimiter(job, 35);
        PiikTextLimiter.addTextLimiter(description, 256);
        PiikTextLimiter.addTextLimiter(emotionalSituation, 20);
        PiikTextLimiter.addTextLimiter(birthplace, 30);
        PiikTextLimiter.addTextLimiter(city, 30);

        //Add options to the comboBox
        genderBox.setItems(FXCollections.observableArrayList("M", "H", "O"));

        register.setOnAction(this::handleRegister);

        addTelephone.setOnAction(this::handleAddTelephone);

        removeTelephone.setOnAction(this::handleRemove);

        telephoneList.getItems().add("Telephone. Click to edit!");
        telephoneList.setEditable(true);
        telephoneList.setCellFactory(TextFieldListCell.forListView());
    }

    /**
     * Function to remove selected telephone from list if button clicked
     *
     * @param event
     */
    private void handleRemove(Event event) {
        if (telephoneList.getSelectionModel().getSelectedIndex() == -1) {
            return;
        }

        telephoneList.getItems().remove(telephoneList.getSelectionModel().getSelectedIndex());
        telephoneList.getSelectionModel().selectPrevious();
        telephoneList.scrollTo(telephoneList.getSelectionModel().getSelectedIndex() - 1);
        telephoneList.layout();
    }

    /**
     * Function to add a telephone
     *
     * @param event
     */
    private void handleAddTelephone(Event event) {

        telephoneList.getItems().add("Edit!");
        telephoneList.scrollTo(telephoneList.getItems().size() - 1);
        telephoneList.layout();
        telephoneList.edit(telephoneList.getItems().size() - 1);

    }

    /**
     * Function to register an user if registerButton is clicked
     *
     * @param event
     */
    private void handleRegister(Event event) {
        Alert alert = new Alert(ContextHandler.getContext().getStage("register"));

        if (!checkFields()) {
            alert.setHeading("Fields empty!");
            alert.addText("Fields with * cannot be empty");
            alert.addCloseButton();
            alert.show();
            return;
        }

        User user = new User(userName.getText(), userId.getText(), email.getText());
        if (birthday.validate()) {
            user.setBirthday(Timestamp.valueOf(birthday.getValue().atStartOfDay()));
        }
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

        for (String s : telephoneList.getItems()) {

            try {
                Integer.parseInt(s);
                user.addPhone(s);
            } catch (NumberFormatException e) {

            }

        }
        try {
            ApiFacade.getEntrypoint().getInsertionFacade().createUser(user);
            ContextHandler.getContext().getStage("register").close();
        } catch (PiikException e) {
            e.showAlert(e, "Failure registering the user");
        }
    }

    /**
     * Function to check if *fields are not empty
     *
     * @return true if *fields are not empty, otherwise false
     */
    private boolean checkFields() {
        if (userId.getText().isEmpty() || userName.getText().isEmpty() || email.getText().isEmpty()
                || password.getText().isEmpty() || !birthday.validate()) {
            return false;
        }
        return true;
    }

}
