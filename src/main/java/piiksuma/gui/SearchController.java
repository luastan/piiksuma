package piiksuma.gui;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import piiksuma.Post;
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SearchController implements Initializable {

    @FXML
    private JFXButton back;

    @FXML
    private JFXButton Search;

    @FXML
    private JFXTextField searchText;

    @FXML
    private JFXMasonryPane searchMasonryPane;

    @FXML
    private ScrollPane searchScrollPane;

    @FXML
    private JFXButton userButton;

    @FXML
    private JFXButton postButton;

    @FXML
    private JFXButton eventButton;

    private ObservableList<Post> postFeed;

    private ObservableList<User> userFeed;

    private ObservableList<piiksuma.Event> eventFeed;

    private boolean user = false;
    private boolean post = true;
    private boolean event = false;


    @Override

    public void initialize(URL location, ResourceBundle resources) {
        back.setOnAction(this::backButton);
        Search.setOnAction(this::handleSearch);

        postFeed = FXCollections.observableArrayList();
        userFeed = FXCollections.observableArrayList();
        eventFeed = FXCollections.observableArrayList();

        ContextHandler.getContext().setSearchController(this);
        setUpFeedPostListener();
        setUpFeedEventListener();
        setUpFeedUserListener();

        userButton.setOnAction(this::handleUserButton);
        eventButton.setOnAction(this::handleEventButton);
        postButton.setOnAction(this::handlePostButton);

        //updatePostFeed();
        try {
            updateEventFeed();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }
    }





    private void handleUserButton(Event eventW) {
        user = true;
        event = false;
        post = false;
    }

    private void handleEventButton(Event eventW) {
        user = false;
        event = true;
        post = false;

        System.out.println(event);
    }

    private void handlePostButton(Event eventW) {
        user = false;
        event = false;
        post = true;
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
        if (post) {
            try {
                updatePostFeed();
            } catch (PiikDatabaseException e) {
                e.printStackTrace();
            }
        }else if (user){
            try {
                updateUserFeed();
            } catch (PiikDatabaseException e) {
                e.printStackTrace();
            }
        }else{
            try {
                updateEventFeed();
            } catch (PiikDatabaseException e) {
                e.printStackTrace();
            }
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
        userFeed.addAll(new QueryMapper<User>(ApiFacade.getEntrypoint().getConnection()).defineClass(User.class).createQuery("SELECT * FROM piiUser WHERE name LIKE ?;")
                .defineParameters("%" + searchText.getText() + "%").list());
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
            searchMasonryPane.getChildren().clear();
            postFeed.forEach(this::insertPost);
        });
    }

    private void setUpFeedUserListener() {
        userFeed.addListener((ListChangeListener<? super User>) change -> {
            searchMasonryPane.getChildren().clear();
            userFeed.forEach(this::insertUser);
        });
    }

    private void setUpFeedEventListener() {
        eventFeed.addListener((ListChangeListener<? super piiksuma.Event>) change -> {
            searchMasonryPane.getChildren().clear();
            eventFeed.forEach(this::insertEvent);
        });
    }


    private void insertPost(Post post) {
        FXMLLoader postLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/post.fxml"));
        postLoader.setController(new PostController(post));
        try {
            searchMasonryPane.getChildren().add(postLoader.load());
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        searchScrollPane.requestLayout();
        searchScrollPane.requestFocus();
    }

    private void insertUser(User user) {
        FXMLLoader postLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/user.fxml"));
        postLoader.setController(new SearchedUserController(user));
        try {
            searchMasonryPane.getChildren().add(postLoader.load());
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        searchScrollPane.requestLayout();
        searchScrollPane.requestFocus();
    }

    private void insertEvent(piiksuma.Event event) {
        FXMLLoader postLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/event.fxml"));
        postLoader.setController(new EventController(event));
        try {
            searchMasonryPane.getChildren().add(postLoader.load());
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        searchScrollPane.requestLayout();
        searchScrollPane.requestFocus();
    }


}
