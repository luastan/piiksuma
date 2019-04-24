package piiksuma.gui;

import com.jfoenix.controls.JFXTabPane;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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

    @FXML
    private StackPane deckContainer;

    @FXML
    private StackPane mainContainer;

    @FXML
    private JFXTabPane mainPane;


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
        Tab feedTab = new Tab(); // Creates the tab
        FontAwesomeIconView feedTabIcon = new FontAwesomeIconView(FontAwesomeIcon.COMMENT);  // Sets icon
        feedTabIcon.setStyle("-fx-fill: -white-high-emphasis;");
        feedTab.setGraphic(feedTabIcon);
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
        Tab searchTab = new Tab();
        FontAwesomeIconView searchTabIcon = new FontAwesomeIconView(FontAwesomeIcon.SEARCH_MINUS);
        searchTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");
        searchTab.setGraphic(searchTabIcon);

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
                    FXMLLoader searchViewLoader = new FXMLLoader(getClass().getResource("/gui/fxml/search.fxml"));
                    mainContainer.getChildren().add(searchViewLoader.load());
                } catch (IOException ignore) {
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
        Tab messagesTab = new Tab();
        FontAwesomeIconView messagesTabIcon = new FontAwesomeIconView(FontAwesomeIcon.ENVELOPE);
        messagesTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");
        messagesTab.setGraphic(messagesTabIcon);
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
        Tab eventTab = new Tab();
        FontAwesomeIconView eventTabIcon = new FontAwesomeIconView(FontAwesomeIcon.CALENDAR);
        eventTabIcon.setStyle("-fx-fill: -white-medium-emphasis;");
        eventTab.setGraphic(eventTabIcon);
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
        FXMLLoader eventViewLoader = new FXMLLoader(getClass().getResource("/gui/fxml/events.fxml"));
        eventTab.setContent(eventViewLoader.load());

       // Finally loads tabs into the pane
        mainPane.getTabs().addAll(
                feedTab,
                //profileTab,
                messagesTab,
                eventTab,
                searchTab

        );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Code to initialize current view
        nodesMainContainer = new ArrayList<>();
        nodesDeckContainer = new ArrayList<>();
        try {
            tabInitialization();
        } catch (IOException | PiikInvalidParameters ioEx) {
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
