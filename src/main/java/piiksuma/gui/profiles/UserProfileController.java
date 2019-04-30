package piiksuma.gui.profiles;

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
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;
import piiksuma.gui.PostController;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {

    public JFXButton buttonLeft;
    public ScrollPane publishedPosts;
    public JFXMasonryPane publishedPostsMasonry;
    public ScrollPane archivedPosts;
    public JFXMasonryPane archivedPostsMasonry;
    public JFXButton buttonCenter;
    public JFXButton buttonRight;

    @FXML
    private Label Name;

    @FXML
    private Label description;

    private User user;

    private ObservableList<Post> publishedPostsList;
    private ObservableList<Post> archivedPostsList;


    public UserProfileController(User user) {
        this.user = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ContextHandler.getContext().setUserProfileController(this);

        Name.setText(user.getName());
        description.setText(user.getDescription());


        publishedPostsList = FXCollections.observableArrayList();
        archivedPostsList = FXCollections.observableArrayList();

        // TODO: Initialize the buttons
        setUpPostsListener();
        updateFeed();
        updateArchivedPosts();
    }

    private void handleDelete(Event event){
        try{
            ApiFacade.getEntrypoint().getDeletionFacade().removeUser(ContextHandler.getContext().getCurrentUser(),ContextHandler.getContext().getCurrentUser());
        }catch (PiikException e){
            //TODO handle exception
            return;
        }
        ContextHandler.getContext().setCurrentUser(null);
        ContextHandler.getContext().stageJuggler();
    }

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
        publishedPostsList.clear();

        try {
            List<Post> searchPosts = ApiFacade.getEntrypoint().getSearchFacade().getPost(
                    ContextHandler.getContext().getCurrentUser(), ContextHandler.getContext().getCurrentUser());

            if(searchPosts != null && !searchPosts.isEmpty()) {
                publishedPostsList.addAll(searchPosts);
            }
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.printStackTrace();
        }

        publishedPosts.requestLayout();
        publishedPosts.requestFocus();

        publishedPostsMasonry.requestLayout();
    }

    public void updateArchivedPosts() {
        archivedPostsList.clear();

        /*try {
            posts.addAll(ApiFacade.getEntrypoint().getSearchFacade().getArchivedPosts(
                    ContextHandler.getContext().getCurrentUser(), ContextHandler.getContext().getCurrentUser()));
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.printStackTrace();
        }*/

        archivedPosts.requestLayout();
        archivedPosts.requestFocus();

        archivedPostsMasonry.requestLayout();
    }

    /**
     * Code to be executed when the shown posts get updated. Posts at the interface
     * have to be updated.
     * <p>
     * Recieves change performed to the list via {@link ListChangeListener#onChanged(ListChangeListener.Change)}
     */
    private void setUpPostsListener() {
        publishedPostsList.addListener((ListChangeListener<? super Post>) change -> {
            publishedPostsMasonry.getChildren().clear();
            publishedPostsList.forEach(this::insertPost);
        });
    }

    private void insertPost(Post post) {
        FXMLLoader postLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/post.fxml"));
        postLoader.setController(new PostController(post));
        try {
            publishedPostsMasonry.getChildren().add(postLoader.load());
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        publishedPosts.requestLayout();
        publishedPosts.requestFocus();
    }

    private void archivePost(Post post) {
        FXMLLoader postLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/post.fxml"));
        postLoader.setController(new PostController(post));
        try {
            archivedPostsMasonry.getChildren().add(postLoader.load());
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        archivedPosts.requestLayout();
        archivedPosts.requestFocus();
    }

    public void handleFeedButton(Event event) {
        updateFeed();
    }

    public void handleArchivedPostsButton(Event event) {
        updateArchivedPosts();
    }
}
