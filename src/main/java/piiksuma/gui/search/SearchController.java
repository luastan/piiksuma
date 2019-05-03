package piiksuma.gui.search;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import piiksuma.Post;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;
import piiksuma.gui.events.EventController;
import piiksuma.gui.posts.PostController;
import piiksuma.gui.events.EventPreviewController;
import piiksuma.gui.profiles.ProfilePreviewController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SearchController implements Initializable {


    public JFXButton buttonSearch;
    public ScrollPane userScrollPane;
    public HBox userMasonryPane;
    public ScrollPane eventScrollPane;
    public JFXMasonryPane eventMasonryPane;
    public ScrollPane postScrollPane;
    public JFXMasonryPane postMasonryPane;
    public JFXButton trendingTopics;
    @FXML
    private JFXButton back;

    @FXML
    private JFXTextField searchText;

    private ObservableList<Post> postFeed;

    private ObservableList<User> userFeed;

    private ObservableList<piiksuma.Event> eventFeed;

    private boolean user = false;
    private boolean post = true;
    private boolean event = false;


    @Override

    public void initialize(URL location, ResourceBundle resources) {
        back.setOnAction(this::backButton);
        buttonSearch.setOnAction(this::handleSearch);

        postFeed = FXCollections.observableArrayList();
        userFeed = FXCollections.observableArrayList();
        eventFeed = FXCollections.observableArrayList();
        ContextHandler.getContext().setSearchController(this);
        setUpFeedPostListener();
        setUpFeedEventListener();
        setUpFeedUserListener();

        trendingTopics.setOnAction(event1 -> {
            try {
                ContextHandler.getContext().invokeStage("/gui/fxml/hashtags/trending.fxml", null);
            } catch (PiikInvalidParameters invalidParameters) {
                invalidParameters.showAlert();
            }
        });

    }

    public void clear() {
        postFeed.clear();
        userFeed.clear();
        eventFeed.clear();
    }



    /**
     * Restores the original layout
     *
     * @param event Click Event
     */
    private void backButton(javafx.event.Event event) {
        // Just by selecting another tab will renew the contents
        JFXTabPane tabPane = (JFXTabPane) ContextHandler.getContext().getElement("mainTabPane");
        tabPane.getSelectionModel().select(0);
        event.consume();  // Consumes it just in case another residual handler was listening to it


    }

    private void handleSearch(Event event){
        try {
            updatePostFeed();
            updateUserFeed();
            updateEventFeed();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }
    }

    public void updatePostFeed() throws PiikDatabaseException {
        // TODO: update the feed propperly
        postFeed.clear();
        if (searchText.getText().isEmpty()) {
            searchText.setText("");
        }
        postFeed.addAll(new QueryMapper<Post>(ApiFacade.getEntrypoint().getConnection()).defineClass(Post.class).createQuery("SELECT * FROM post WHERE text LIKE ?;")
                .defineParameters("%" + searchText.getText() + "%").list());
    }

    public void updateUserFeed() throws PiikDatabaseException {
        // TODO: update the feed propperly
        userFeed.clear();
        if (searchText.getText().isEmpty()) {
            searchText.setText("");
        }
        userFeed.addAll(new QueryMapper<User>(ApiFacade.getEntrypoint().getConnection()).defineClass(User.class).createQuery("SELECT * FROM piiUser WHERE name LIKE ?" +
                " AND id != ?;")
                .defineParameters("%" + searchText.getText() + "%",ContextHandler.getContext().getCurrentUser().getId()).list());
    }

    public void updateEventFeed() throws PiikDatabaseException{
        // TODO: update the feed propperly
        eventFeed.clear();
        if (searchText.getText().isEmpty()) {
            searchText.setText("");
        }
        eventFeed.addAll(new QueryMapper<piiksuma.Event>(ApiFacade.getEntrypoint().getConnection()).defineClass(piiksuma.Event.class).createQuery("SELECT * FROM event WHERE name LIKE ?;")
                .defineParameters("%" + searchText.getText() + "%").list());
    }

    /**
     * Code to be executed when the feed gets updated. Posts at the interface
     * have to be updated.
     * <p>
     * Recieves change performed to the list via {@link ListChangeListener#onChanged(ListChangeListener.Change)}
     */
    private void setUpFeedPostListener() {
        postFeed.addListener((ListChangeListener<? super Post>) change -> {
            postMasonryPane.getChildren().clear();
            postScrollPane.setVisible(postFeed.size() != 0);
            postFeed.forEach(this::insertPost);
        });
    }

    private void setUpFeedUserListener() {
        userFeed.addListener((ListChangeListener<? super User>) change -> {
            userMasonryPane.getChildren().clear();
            userScrollPane.setVisible(userFeed.size() != 0);
            userFeed.forEach(this::insertUser);
        });
    }

    private void setUpFeedEventListener() {
        eventFeed.addListener((ListChangeListener<? super piiksuma.Event>) change -> {
            eventMasonryPane.getChildren().clear();
            eventScrollPane.setVisible(userFeed.size() != 0);
            eventFeed.forEach(this::insertEvent);
        });
    }


    private void insertPost(Post post) {
        FXMLLoader postLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/post.fxml"));
        postLoader.setController(new PostController(post));
        try {
            postMasonryPane.getChildren().add(postLoader.load());
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        postScrollPane.requestFocus();
        postScrollPane.requestLayout();
    }

    private void insertUser(User user) {
        FXMLLoader postLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/profile/profilePreview.fxml"));
        postLoader.setController(new ProfilePreviewController(user));
        try {
            userMasonryPane.getChildren().add(postLoader.load());
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        userScrollPane.requestLayout();
        userScrollPane.requestFocus();
    }

    private void insertEvent(piiksuma.Event event) {
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

    private void clickEvent(MouseEvent mouseEvent) {
        if (!(mouseEvent.getSource() instanceof StackPane)) {
            return;
        }
        // Identifies clicked Event
        piiksuma.Event target = eventFeed.stream()
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
