package piiksuma.gui;

import com.jfoenix.controls.JFXTabPane;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.deckControllers.EventsDeckController;
import piiksuma.gui.deckControllers.FeedDeckController;
import piiksuma.gui.deckControllers.MessagesDeckController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public Tab searchTab;
    public Tab messagesTab;
    public Tab eventTab;
    @FXML
    private StackPane deckContainer;

    @FXML
    private StackPane mainContainer;

    @FXML
    private JFXTabPane mainPane;

    @FXML
    private Tab feedTab;


    private List<Node> nodesMainContainer;
    private List<Node> nodesDeckContainer;

    private FXMLLoader deckLoader = new FXMLLoader(getClass().getResource("/gui/fxml/deck.fxml"));

    /**
     * Loads all the required tabs into the mainPane
     */
    private void tabInitialization() throws IOException, PiikInvalidParameters {
        // Register the mainPane into the context to access it from other Controllers
        ContextHandler.getContext().register("mainTabPane", mainPane);

        // Feed
        Node feedTabIcon = feedTab.getGraphic();  // Sets icon
        feedTabIcon.setStyle("-fx-fill: -white-high-emphasis;");
        feedTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // Adds listener to change the color when selected and the deck controller
            if (newValue) {
                feedTabIcon.setStyle("-fx-fill: -white-high-emphasis;");
                // Clears and loads the new deck
                deckContainer.getChildren().clear();
                deckLoader = new FXMLLoader(getClass().getResource("/gui/fxml/deck.fxml"));
                deckLoader.setController(new FeedDeckController());
                try {
                    deckContainer.getChildren().add(deckLoader.load());
                } catch (IOException ignore) {
                    // TODO: Handle exception
                }

            } else {
                feedTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");
            }
        });
        // Lads and sets the content to be represented when the tab gets selected
        FXMLLoader feedViewLoader = new FXMLLoader(getClass().getResource("/gui/fxml/feed.fxml"));
        feedTab.setContent(feedViewLoader.load());


        // Missing tabs follow the same pattern as the feed tab

        // Search
        Node searchTabIcon = searchTab.getGraphic();
        searchTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");

        // In the case of the search tab the mainPanel gets replaced
        searchTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                searchTabIcon.setStyle("-fx-fill: -white-high-emphasis;");
                // Replaces the contents in the mainContainer (Holds the mainPane) and saves them to enable easy
                // restoration
                nodesMainContainer.clear();
                nodesMainContainer.addAll(mainContainer.getChildren());
                mainContainer.getChildren().clear();
                try {
                    FXMLLoader searchViewLoader = new FXMLLoader(getClass().getResource("/gui/fxml/search/search.fxml"));
                    mainContainer.getChildren().add(searchViewLoader.load());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // TODO: Handle exception
                }
                deckContainer.getChildren().clear();
            } else {
                searchTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");
                // Restores the main Container
                mainContainer.getChildren().clear();
                mainContainer.getChildren().addAll(nodesMainContainer);
            }
        });


        // Messages
        Node messagesTabIcon = messagesTab.getGraphic();
        messagesTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");
        messagesTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                messagesTabIcon.setStyle("-fx-fill: -white-high-emphasis;");
                deckContainer.getChildren().clear();
                deckLoader = new FXMLLoader(getClass().getResource("/gui/fxml/deck.fxml"));
                try {
                    deckLoader.setController(new MessagesDeckController());
                    deckContainer.getChildren().add(deckLoader.load());
                } catch (IOException ignore) {
                    // TODO: Handle exception
                }
            } else {
                messagesTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");
            }
        });
        FXMLLoader messagesViewLoader = new FXMLLoader(getClass().getResource("/gui/fxml/messages.fxml"));
        messagesTab.setContent(messagesViewLoader.load());

        // Events
        Node eventTabIcon = eventTab.getGraphic();
        eventTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");
        eventTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                eventTabIcon.setStyle("-fx-fill: -white-high-emphasis;");
                deckContainer.getChildren().clear();
                deckLoader = new FXMLLoader(getClass().getResource("/gui/fxml/deck.fxml"));
                try {
                    deckLoader.setController(new EventsDeckController());
                    deckContainer.getChildren().add(deckLoader.load());
                } catch (IOException ignore) {
                    // TODO: Handle exception
                }
            } else {
                eventTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");
            }
        });
        FXMLLoader eventViewLoader = new FXMLLoader(getClass().getResource("/gui/fxml/events/events.fxml"));
        eventTab.setContent(eventViewLoader.load());

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Code to initialize current view
        nodesMainContainer = new ArrayList<>();
        nodesDeckContainer = new ArrayList<>();
        try {
            tabInitialization();
            deckContainer.getChildren().clear();
            deckLoader = new FXMLLoader(getClass().getResource("/gui/fxml/deck.fxml"));
            deckLoader.setController(new FeedDeckController());
            deckContainer.getChildren().add(deckLoader.load());
        } catch (IOException | PiikInvalidParameters ioEx) {
            ioEx.printStackTrace();
            System.out.println("Unable to load FXML");
        }


    }


    /**
     * Code to be run before exiting the application
     */
    private void handleExit(Event event) {
        ContextHandler.getContext().getCurrentStage().close();
    }


}
