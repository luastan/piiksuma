package piiksuma.gui.profiles;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import piiksuma.Multimedia;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.api.MultimediaType;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Base64;
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
    private JFXDatePicker birthday;
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

    private Multimedia multimedia;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initFields();

        addTelephone.setOnAction(this::handleAddTelephone);

        removeTelephone.setOnAction(this::handleRemove);

        telephoneList.setEditable(true);
        telephoneList.setCellFactory(TextFieldListCell.forListView());
        multimediaButton.setOnAction(this::handleMultimediaButton);
        update.setOnAction(this::handleUpdate);
        password.setEditable(false);

    }

    private void initFields() {
        User user = ContextHandler.getContext().getCurrentUser();

        genderBox.setItems(FXCollections.observableArrayList("M", "V", "O"));
        //We add to the fields all the info about the user
        userId.setText(user.getId());
        //WE dont let the user to changue his id
        userId.setDisable(true);
        userName.setText(user.getName());
        email.setText(user.getEmail());
        //  password.setText(user.getPass());
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

        birthday.setValue(user.getBirthday().toLocalDateTime().toLocalDate());

        for (String telephone : user.getPhones()) {
            telephoneList.getItems().add(telephone);
        }
    }

    /**
     * Function to handle the plus button to add an image
     *
     * @param event
     */
    private void handleMultimediaButton(Event event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a profiles picture");
        File imagen = fileChooser.showOpenDialog(null);

        if (imagen != null) {
            try {
                // Copy img from source to resource folder
                BufferedImage img = ImageIO.read(imagen);
                File outputFile = new File("src/main/resources/imagenes/" + imagen.getName());
                ImageIO.write(img, imagen.getName().split("\\.")[1], outputFile);
                imageView.setImage(new Image(outputFile.toURI().toString()));
                // Create multimedia
                multimedia = new Multimedia();
                try {
                    RandomAccessFile file = new RandomAccessFile(outputFile, "r");
                    byte[] imgBytes = new byte[Math.toIntExact(file.length())];
                    file.read(imgBytes);
                    multimedia.setHash(Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-512").digest(imgBytes)));
                    multimedia.setUri(outputFile.toURI().toString());
                    multimedia.setType(MultimediaType.image);
                    multimedia.setResolution(String.valueOf((int) imageView.getImage().getWidth()) + 'x'
                            + (int) imageView.getImage().getHeight());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Function to handle when removeTelephone button is clicked
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
     * Function to handle when addTelephone button is clicked
     *
     * @param event
     */
    private void handleAddTelephone(Event event) {

        telephoneList.getItems().add("Edit!");
        telephoneList.scrollTo(telephoneList.getItems().size() - 1);
        telephoneList.layout();
        telephoneList.edit(telephoneList.getItems().size() - 1);

    }

    private void handleUpdate(Event event) {
        User modifyUser = new User();

        modifyUser.setId(userId.getText());
        modifyUser.setName(userName.getText());
        modifyUser.setGender(genderBox.getSelectionModel().getSelectedItem());
        modifyUser.setEmail(email.getText());
        modifyUser.setDescription(description.getText());
        modifyUser.setEmotionalSituation(emotionalSituation.getText());
        modifyUser.setCity(city.getText());
        modifyUser.setJob(job.getText());
        modifyUser.setBirthplace(birthplace.getText());
        modifyUser.setReligion(religion.getText());
        modifyUser.setProvince(province.getText());
        modifyUser.setCountry(country.getText());
        modifyUser.setHome(home.getText());
        if(multimedia != null){
            modifyUser.setMultimedia(multimedia);
        }
        // Create multimedia
        // modifyUser.setPass(password.getText());

        for (String telephone : telephoneList.getItems()) {
            modifyUser.getPhones().add(telephone);
        }

        if (birthday.validate()) {
            modifyUser.setBirthday(Timestamp.valueOf(birthday.getValue().atStartOfDay()));
        }

        try {
            ApiFacade.getEntrypoint().getInsertionFacade().administratePersonalData(modifyUser, modifyUser);
            //Update the user in the app
            ContextHandler.getContext().setCurrentUser(ApiFacade.getEntrypoint().getSearchFacade().getUser(modifyUser, modifyUser));
            ContextHandler.getContext().getStage("User data").close();
        } catch (PiikException e) {
            e.showAlert();
        }


    }
}
