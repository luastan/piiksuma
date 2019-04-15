package piiksuma.gui;

import com.jfoenix.controls.JFXTabPane;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import piiksuma.exceptions.PiikInvalidParameters;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    public JFXTabPane mainPane;

    /**
     * Loads all the required tabs into the mainPane
     */
    private void tabInitialization() throws IOException {

        // Feed
        Tab feedTab = new Tab();
        FontAwesomeIconView feedTabIcon = new FontAwesomeIconView(FontAwesomeIcon.COMMENT);
        feedTabIcon.setStyle("-fx-fill: -white-high-emphasis;");
        feedTab.setGraphic(feedTabIcon);
        feedTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                feedTabIcon.setStyle("-fx-fill: -white-high-emphasis;");
            } else {
                feedTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");
            }
        });
        FXMLLoader feedViewLoader = new FXMLLoader(getClass().getResource("/gui/fxml/feed.fxml"));
        feedTab.setContent(feedViewLoader.load());


        /*
        // Profile
        Tab profileTab = new Tab();
        profileTab.setText("P");
        FXMLLoader profileViewLoader = new FXMLLoader(getClass().getResource("/gui/fxml/profile.fxml"));
        profileTab.setContent(profileViewLoader.load());
        */

        // Search
        Tab searchTab = new Tab();
        FontAwesomeIconView searchTabIcon = new FontAwesomeIconView(FontAwesomeIcon.SEARCH_MINUS);
        searchTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");
        searchTab.setGraphic(searchTabIcon);
        searchTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) searchTabIcon.setStyle("-fx-fill: -white-high-emphasis;");
            else searchTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");

        });
        FXMLLoader searchViewLoader = new FXMLLoader(getClass().getResource("/gui/fxml/search.fxml"));
        searchTab.setContent(searchViewLoader.load());

        // Messages
        Tab messagesTab = new Tab();
        FontAwesomeIconView messagesTabIcon = new FontAwesomeIconView(FontAwesomeIcon.ENVELOPE);
        messagesTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");
        messagesTab.setGraphic(messagesTabIcon);
        messagesTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) messagesTabIcon.setStyle("-fx-fill: -white-high-emphasis;");
            else messagesTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");

        });
        FXMLLoader messagesViewLoader = new FXMLLoader(getClass().getResource("/gui/fxml/messages.fxml"));
        messagesTab.setContent(messagesViewLoader.load());

        // Events
        Tab eventTab = new Tab();
        FontAwesomeIconView eventTabIcon = new FontAwesomeIconView(FontAwesomeIcon.CALENDAR);
        eventTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");
        eventTab.setGraphic(eventTabIcon);
        eventTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) eventTabIcon.setStyle("-fx-fill: -white-high-emphasis;");
            else eventTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");

        });
        FXMLLoader eventViewLoader = new FXMLLoader(getClass().getResource("/gui/fxml/events.fxml"));
        eventTab.setContent(eventViewLoader.load());

        // Load into the pane
        mainPane.getTabs().addAll(
                feedTab,
                //profileTab,
                searchTab,
                messagesTab,
                eventTab
        );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Code to initialize current view
        try {
            tabInitialization();
        } catch (IOException ioEx) {
            System.out.println("Unable to load FXML");
        }


        // Sets code to be executed on exit event
        ContextHandler.getContext().getStage("primary").setOnCloseRequest(this::handleExit);

    }


    /**
     * Code to be run before exiting the application
     */
    private void handleExit(Event event) {
        ContextHandler.getContext().getCurrentStage().close();
    }
}
