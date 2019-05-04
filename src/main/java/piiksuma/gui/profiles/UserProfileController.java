package piiksuma.gui.profiles;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import piiksuma.Post;
import piiksuma.User;
import piiksuma.UserType;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.ContextHandler;
import piiksuma.gui.posts.PostController;

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
    private BorderPane profileContent;

    @FXML
    private Label Name;

    @FXML
    private Label userId;

    @FXML
    private Label description;

    @FXML
    private Label userNotFound;

    @FXML
    private StackPane profilePicture;

    @FXML
    private Tab archivedTab;

    private User user;

    private ObservableList<Post> publishedPostsList;
    private ObservableList<Post> archivedPostsList;


    public UserProfileController(User user) {
        this.user = user;
        publishedPostsList = FXCollections.observableArrayList();
        archivedPostsList = FXCollections.observableArrayList();
    }


    public void setProfilePicture() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/profile/profilePreview.fxml"));
        loader.setController(new ProfilePreviewController(user));
        profilePicture.getChildren().clear();
        try {
            profilePicture.getChildren().add(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            User user = ApiFacade.getEntrypoint().getSearchFacade().getUser(this.user, ContextHandler.getContext().getCurrentUser());

            if (user == null) {
                // User doesn't exist
                profileContent.setVisible(false);
                userNotFound.setText("User with id " + '"' + this.user.getId() + '"' + " does not exist");
                return;
            }
            this.user = user;

            ContextHandler.getContext().setUserProfileController(this);

            Name.setText(user.getName());
            description.setText(user.getDescription());
            userId.setText(user.getId());

            publishedPostsList.addListener((ListChangeListener<? super Post>) c -> {
                publishedPostsMasonry.getChildren().clear();
                publishedPostsList.forEach(this::insertPost);
            });
            updateFeed();

//            publishedPostsList.forEach(this::insertPost);

            if (user.equals(ContextHandler.getContext().getCurrentUser())) {
                archivedPostsList.addListener((ListChangeListener<? super Post>) c -> {
                    archivedPostsMasonry.getChildren().clear();
                    archivedPostsList.forEach(this::archivePost);
                });
                updateArchivedPosts();
//                archivedPostsList.forEach(this::archivePost);
            } else {
                archivedTab.getTabPane().getTabs().remove(archivedTab);
            }

            // TODO: Initialize the buttons
            buttonInit();
            setUpPostsListener();
            setProfilePicture();

        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert();
        }


    }

    private void buttonInit() {
        User current = ContextHandler.getContext().getCurrentUser();
        if (user.equals(current)) { /* If user is the current user */
            buttonRight.setText("Delete");
            buttonRight.setOnAction(this::handleDelete); /* If user is admin */
            buttonCenter.setText("View Tickets");
            buttonCenter.setOnAction(this::handleTicketsButton);
            buttonLeft.setText("New Ticket");
            buttonLeft.setOnAction(this::handleNewTicektButton);
        } else if (current.getType().equals(UserType.administrator)) { /* If user is different from the other cases */
            buttonRight.setText("Delete");
            buttonRight.setOnAction(this::handleDelete);
            buttonCenter.setOnAction(this::handleFollow);
            buttonLeft.setOnAction(this::handleBlock);
            updateFollowButton();
            updateBlockButton();
        } else {
            buttonRight.setVisible(false);
            buttonCenter.setOnAction(this::handleFollow);
            buttonLeft.setOnAction(this::handleBlock);
            updateFollowButton();
            updateBlockButton();
        }

    }


    private void updateFollowButton() {
        User current = ContextHandler.getContext().getCurrentUser();
        try {
            Boolean currentUserFollows = ApiFacade.getEntrypoint().getSearchFacade().isFollowed(user, current, current);
            if (!currentUserFollows) {
                buttonCenter.setText("Follow");
                buttonCenter.setStyle("-fx-background-color: -primary-color-5");
            } else {
                buttonCenter.setText("UnFollow");
                buttonCenter.setStyle("-fx-background-color: -primary-color-2; -fx-text-fill: -black-high-emphasis");
            }
        }catch (PiikInvalidParameters | PiikDatabaseException e){
            e.showAlert();
        }
    }

    private void handleFollow(Event event) {
        User current = ContextHandler.getContext().getCurrentUser();

        try {
            Boolean currentUserFollows = ApiFacade.getEntrypoint().getSearchFacade().isFollowed(user, current, current);
            if (currentUserFollows) {
                ApiFacade.getEntrypoint().getDeletionFacade().unfollowUser(user, current, current);
            } else {
                ApiFacade.getEntrypoint().getInsertionFacade().followUser(user, current, current);
            }
        } catch (PiikInvalidParameters | PiikDatabaseException e) {
            e.showAlert();
        }
        updateFollowButton();
    }


    private void updateBlockButton() {
        User current = ContextHandler.getContext().getCurrentUser();
        try {
            Boolean currentUserBlocks = ApiFacade.getEntrypoint().getSearchFacade().isBlock(user, current, current);
            if (!currentUserBlocks) {
                buttonLeft.setText("Block");
                buttonLeft.setStyle("-fx-background-color: -primary-color-5");
            } else {
                buttonLeft.setText("UnBlock");
                buttonLeft.setStyle("-fx-background-color: -primary-color-2; -fx-text-fill: -black-high-emphasis");
            }
        }catch (PiikInvalidParameters | PiikDatabaseException e) {
            e.showAlert();
        }
    }

    private void handleBlock(Event event) {
        User current = ContextHandler.getContext().getCurrentUser();

        try {
            Boolean currentUserBlocks = ApiFacade.getEntrypoint().getSearchFacade().isBlock(user, current, current);
            if (currentUserBlocks) {
                ApiFacade.getEntrypoint().getDeletionFacade().unblockUser(user, current, current);
            } else {
                ApiFacade.getEntrypoint().getInsertionFacade().blockUser(user, current, current);
            }
        } catch (PiikInvalidParameters | PiikDatabaseException invalidParameters) {
            invalidParameters.showAlert();
        }
        updateBlockButton();
    }


    private void handleDelete(Event event){
        try {
            ApiFacade.getEntrypoint().getDeletionFacade().removeUser(user, ContextHandler.getContext().getCurrentUser());
            if (user.equals(ContextHandler.getContext().getCurrentUser())) {
                ContextHandler.getContext().setCurrentUser(null);
                ContextHandler.getContext().stageJuggler();
            } else {
                ContextHandler.getContext().getStage("User profile").close();
                ContextHandler.getContext().getMessagesController().updateMessageFeed();
                ContextHandler.getContext().getFeedController().updateFeed();
                ContextHandler.getContext().getEventsController().updateEventFeed();
            }

        } catch (PiikException e) {
            //TODO handle exception
            e.showAlert();
        }


    }

    /**
     * Function to open a create ticket window if newTicketButton is clicked
     *
     * @param event Button event
     */
    private void handleNewTicektButton(Event event) {
        try {
            ContextHandler.getContext().invokeStage("/gui/fxml/tickets/newTicket.fxml", null, "New Ticket");
        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.showAlert();
        }
    }

    /**
     * Function to open a window with the tickets
     *
     * @param event Event generated by the button
     */
    private void handleTicketsButton(Event event) {
        try {
            ContextHandler.getContext().invokeStage("/gui/fxml/tickets/tickets.fxml", null, "Tickets");
        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.showAlert();
        }
    }

    /**
     * Function to update the feed
     *
     */
    public void updateFeed()  {
        // TODO: update the feed propperly
        publishedPostsList.clear();

        try {
            List<Post> searchPosts = ApiFacade.getEntrypoint().getSearchFacade().getPost(
                    user , ContextHandler.getContext().getCurrentUser());

            if(searchPosts != null && !searchPosts.isEmpty()) {
                publishedPostsList.addAll(searchPosts);
            }
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert();
        }

        publishedPosts.requestLayout();
        publishedPosts.requestFocus();

        publishedPostsMasonry.requestLayout();
        setProfilePicture();
    }

    public void updateArchivedPosts() {
        if (!user.equals(ContextHandler.getContext().getCurrentUser())) {
            return;
        }
        archivedPostsList.clear();

        try {
            archivedPostsList.addAll(ApiFacade.getEntrypoint().getSearchFacade().getArchivedPosts(
                    user, ContextHandler.getContext().getCurrentUser()));
        } catch (PiikDatabaseException e) {
            e.showAlert();
        } catch (PiikInvalidParameters e) {
            e.showAlert();
        }
        archivedPostsMasonry.requestLayout();
        archivedPosts.requestLayout();
        archivedPosts.requestFocus();
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
        placePost(post, publishedPostsMasonry, publishedPosts);
    }

    private void archivePost(Post post) {
        placePost(post, archivedPostsMasonry, archivedPosts);
    }

    private void placePost(Post post, JFXMasonryPane masonry, ScrollPane scrollPane) {
        FXMLLoader postLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/post.fxml"));
        try {
            postLoader.setController(new PostController(post));
            masonry.getChildren().add(postLoader.load());
        } catch (IOException e) {
            // TODO: Handle Exception
            e.printStackTrace();
        }
        scrollPane.requestFocus();
        scrollPane.requestLayout();
    }


    public void handleFeedButton(Event event) {
        updateFeed();
    }

    public void handleArchivedPostsButton(Event event) {
        updateArchivedPosts();
    }
}
