package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXMasonryPane;
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
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {

    @FXML
    private JFXButton feedButton;

    @FXML
    private JFXButton archivedPostsButton;

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

    @FXML
    private JFXButton deleteButton;

    private ObservableList<Post> posts;

    // Which tab is selected
    boolean feedSelected = true;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //User user = new User("OswaldOswin", "user1", "@yahoo.es");
        //ContextHandler.getContext().setCurrentUser(user);
        ContextHandler.getContext().getCurrentUser().toString();
        Name.setText(ContextHandler.getContext().getCurrentUser().getName());
        description.setText(ContextHandler.getContext().getCurrentUser().getDescription());
        newticketButton.setOnAction(this::handleNewTicektButton);
        ticketsButton.setOnAction(this::handleTicketsButton);
        feedButton.setOnAction(this::handleFeedButton);
        archivedPostsButton.setOnAction(this::handleArchivedPostsButton);
        deleteButton.setOnAction(this::handleDelete);

        posts = FXCollections.observableArrayList();

        ContextHandler.getContext().setUserProfileController(this);
        setUpPostsListener();

        if(feedSelected) {
            updateFeed();
        }
        else {
            updateArchivedPosts();
        }
    }

    private void handleDelete(Event event){
        try{
            ApiFacade.getEntrypoint().getDeletionFacade().removeUser(ContextHandler.getContext().getCurrentUser(),ContextHandler.getContext().getCurrentUser());
        }catch (PiikException e){
            //TODO handle exception
            return;
        }
        Stage stage = new Stage();
        FXMLLoader loader;
        JFXDecorator decorator;
        //We remove the user from the database
        try {
            ApiFacade.getEntrypoint().getDeletionFacade().removeUser(ContextHandler.getContext().getCurrentUser(), ContextHandler.getContext().getCurrentUser());
        } catch (PiikException e) {
            //TODO handle exception
            return;
        }
        ContextHandler.getContext().setCurrentUser(null);
        ContextHandler.getContext().getCurrentStage().close();
        // Now we show the login window
        stage.setTitle("Login");
        loader = new FXMLLoader(getClass().getResource("/gui/fxml/login.fxml"));
        try {
            decorator = new JFXDecorator(stage, loader.load(), false, false, true);
        } catch (IOException e) {
            return;
        }

        // Scene definition & binding to the Primary Stage
        Scene scene = new Scene(decorator, 450, 550);
        stage.setScene(scene);
        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm(),
                getClass().getResource("/gui/css/main.css").toExternalForm()
        );

        try {
            ContextHandler.getContext().register("login", stage);
        } catch (PiikInvalidParameters piikInvalidParameters) {
            piikInvalidParameters.printStackTrace();
        }
        // Show
        ContextHandler.getContext().getCurrentStage().close();
        stage.show();
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
    public void updateFeed()  {
        // TODO: update the feed propperly
        posts.clear();

        try {
            posts.addAll(ApiFacade.getEntrypoint().getSearchFacade().getPost(
                    ContextHandler.getContext().getCurrentUser(), ContextHandler.getContext().getCurrentUser()));
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.printStackTrace();
        }

        postScrollPane.requestLayout();
        postScrollPane.requestFocus();

        postMasonryPane.requestLayout();
    }

    public void updateArchivedPosts() {
        posts.clear();

        /*try {
            posts.addAll(ApiFacade.getEntrypoint().getSearchFacade().getArchivedPosts(
                    ContextHandler.getContext().getCurrentUser(), ContextHandler.getContext().getCurrentUser()));
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.printStackTrace();
        }*/

        postScrollPane.requestLayout();
        postScrollPane.requestFocus();

        postMasonryPane.requestLayout();
    }

    /**
     * Code to be executed when the shown posts get updated. Posts at the interface
     * have to be updated.
     * <p>
     * Recieves change performed to the list via {@link ListChangeListener#onChanged(ListChangeListener.Change)}
     */
    private void setUpPostsListener() {
        posts.addListener((ListChangeListener<? super Post>) change -> {
            postMasonryPane.getChildren().clear();
            posts.forEach(this::insertPost);
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

    public void handleFeedButton(Event event) {
        updateFeed();
    }

    public void handleArchivedPostsButton(Event event) {
        updateArchivedPosts();
    }
}
