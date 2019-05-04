package piiksuma.gui.events;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import piiksuma.Event;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;
import piiksuma.gui.ConversationController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EventsController implements Initializable {

    public StackPane noContentLabel;
    @FXML
    private ScrollPane eventScrollPane;
    @FXML
    private JFXMasonryPane eventMasonryPane;

    private ObservableList<Event> eventFeed;

    /**
     * Init the window components
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventFeed = FXCollections.observableArrayList();

        ContextHandler.getContext().setEventsController(this);
        setUpFeedListener();

        updateEventFeed();
    }

    /**
     * Update the event feed with new information
     */
    public void updateEventFeed() {
        User user = ContextHandler.getContext().getCurrentUser();
        eventFeed.clear();
        try {
            eventFeed.addAll(ApiFacade.getEntrypoint().getSearchFacade().getEvents(user, user));
        }catch (PiikException e){
            System.out.println(e.getMessage());
            return;
        }
        noContentLabel.setVisible(eventFeed.size() == 0);

    }

    /**
     * Sets up the feed listener
     */
    private void setUpFeedListener() {
        eventFeed.addListener((ListChangeListener<? super Event>) change -> {
            eventMasonryPane.getChildren().clear();
            eventFeed.forEach(this::insertEvent);
        });
    }

    /**
     * Inserts an event on the window
     * @param event Event which is goint to be inserted
     */
    private void insertEvent(Event event) {
        FXMLLoader eventLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/events/eventPreview.fxml"));
        eventLoader.setController(new EventPreviewController(event));

        try {
            Node node = eventLoader.load();
            node.setOnMouseClicked(this::clickEvent);
            eventMasonryPane.getChildren().add(node);
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        eventScrollPane.requestLayout();
        eventScrollPane.requestFocus();
    }

    /**
     * Sets up what happens with a mouse event
     * @param mouseEvent Mouse Event which activates this codde
     */
    private void clickEvent(MouseEvent mouseEvent) {
        if (!(mouseEvent.getSource() instanceof StackPane)) {
            return;
        }
        // Identifies clicked Event
        Event target = eventFeed.stream()
                .filter(event -> event.getId().equals(((StackPane) mouseEvent.getSource()).getChildren().stream()
                        .filter(node -> node instanceof Label).map(node -> (Label) node).map(Labeled::getText)
                        .findFirst().orElse(""))).findFirst().orElse(null);
        if (target == null) {
            return;
        }
        EventController controller = new EventController(target);
        try {
            ContextHandler.getContext().invokeStage("/gui/fxml/events/event.fxml", controller, "Event" + (target.getName() != null ? " - " + target.getName() : ""));
        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.printStackTrace();
        }
    }

}