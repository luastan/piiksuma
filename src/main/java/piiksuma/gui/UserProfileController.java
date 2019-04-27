package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXTabPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

public class UserProfileController implements Initializable {

    @FXML
    private JFXButton feedButton;

    @FXML
    private JFXButton newticketButton;

    @FXML
    private JFXButton ticketsButton;

    @FXML
    private Label Name;

    @FXML
    private Label description;

    @FXML
    private ScrollPane postScrollPane;

    @FXML
    private JFXMasonryPane postMasonryPane;
    private ObservableList<Post> feed;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //User user = new User("OswaldOswin", "user1", "@yahoo.es");
        //ContextHandler.getContext().setCurrentUser(user);
        ContextHandler.getContext().getCurrentUser().toString();
        Name.setText(ContextHandler.getContext().getCurrentUser().getName());
        description.setText(ContextHandler.getContext().getCurrentUser().getDescription());
        newticketButton.setOnAction(this::handleNewTicektButton);
        ticketsButton.setOnAction(this::handleTicketsButton);

        feed = FXCollections.observableArrayList();

        ContextHandler.getContext().setUserProfileController(this);
        setUpFeedListener();


        try {
            updateFeed();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Restores the original layout
     *
     * @param event Click Event
     */

    /**
     * Function to open a create ticket window if newTicketButton is clicked
     *
     * @param event
     */
    private void handleNewTicektButton(Event event) {
        Stage searchStage = new Stage();

        try {
            ContextHandler.getContext().register("New Ticket", searchStage);
        } catch (PiikInvalidParameters e) {
            e.printStackTrace();
            return;
        }
        // Stage configuration
        searchStage.setTitle("New Ticket");
        searchStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/newTicket.fxml"));
        JFXDecorator decorator;

        try {
            decorator = new JFXDecorator(searchStage, loader.load(), false, false, true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(decorator, 400, 300);


        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm(),
                getClass().getResource("/gui/css/main.css").toExternalForm()
        );
        searchStage.initModality(Modality.WINDOW_MODAL);
        searchStage.initOwner(ContextHandler.getContext().getStage("primary"));
        searchStage.setScene(scene);
        // Show and wait till it closes
        searchStage.showAndWait();
    }

    /**
     * Function to open a window with the tickets
     *
     * @param event
     */
    private void handleTicketsButton(Event event) {
        Stage searchStage = new Stage();

        try {
            ContextHandler.getContext().register("Tickets", searchStage);
        } catch (PiikInvalidParameters e) {
            e.printStackTrace();
            return;
        }
        // Stage configuration
        searchStage.setTitle("Tickets");
        searchStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/tickets.fxml"));
        JFXDecorator decorator;

        try {
            decorator = new JFXDecorator(searchStage, loader.load(), false, false, true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(decorator, 450, 600);


        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm(),
                getClass().getResource("/gui/css/main.css").toExternalForm()
        );
        searchStage.initModality(Modality.WINDOW_MODAL);
        searchStage.initOwner(ContextHandler.getContext().getStage("primary"));
        searchStage.setScene(scene);
        // Show and wait till it closes
        searchStage.showAndWait();
    }

    /**
     * Function to update the feed
     *
     * @throws PiikDatabaseException
     */
    public void updateFeed() throws PiikDatabaseException {
        // TODO: update the feed propperly
        feed.clear();
        feed.addAll(new QueryMapper<Post>(ApiFacade.getEntrypoint().getConnection()).defineClass(Post.class).createQuery("SELECT * FROM post WHERE author = ?;")
                .defineParameters(ContextHandler.getContext().getCurrentUser().getId()).list());
    }

    /**
     * Code to be executed when the feed gets updated. Posts at the interface
     * have to be updated.
     * <p>
     * Recieves change performed to the list via {@link ListChangeListener#onChanged(ListChangeListener.Change)}
     */
    private void setUpFeedListener() {
        feed.addListener((ListChangeListener<? super Post>) change -> {
            postMasonryPane.getChildren().clear();
            feed.forEach(this::insertPost);
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
        postScrollPane.requestLayout();
        postScrollPane.requestFocus();
    }

}
