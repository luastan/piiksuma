package piiksuma.gui;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import piiksuma.*;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PostController implements Initializable {

    @FXML
    private Label authorId;

    @FXML
    private Label authorName;

    @FXML
    private Label postContent;

    @FXML
    private JFXButton buttonLike;

    @FXML
    private JFXButton buttonAnswer;

    @FXML
    private JFXButton deleteButton;

    @FXML
    private ImageView boxImage;

    @FXML
    private ImageView profilePicture;

    private Post post;

    public PostController(Post post) {
        this.post = post;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        User author = post.getAuthor();
        User current = ContextHandler.getContext().getCurrentUser();

        if ((current.getType() == null || current.getType() == UserType.user) &&
                !current.getPK().equals(post.getAuthor().getPK())) {
            deleteButton.setVisible(false);
        } else {
            deleteButton.setVisible(true);
        }

        try {
            author = ApiFacade.getEntrypoint().getSearchFacade().getUser(
                    author, ContextHandler.getContext().getCurrentUser());
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert();
        }

        post.setAuthor(author);

        postContent.setText(post.getText());
        authorName.setText(post.getAuthor().getName());
        authorId.setText(post.getAuthor().getId());

        // Multimedia insertion
        if (post.getMultimedia() != null && post.getMultimedia().getUri() != null && !post.getMultimedia().getUri().equals("")) {
            boxImage.setImage(new Image(post.getMultimedia().getUri(), 450, 800, true, true));
            boxImage.setViewport(new Rectangle2D((boxImage.getImage().getWidth() - 450) / 2, (boxImage.getImage().getHeight() - 170) / 2, 450, 170));
        } else if (post.getAuthor().getId().equals("usr1")) {  // TODO: Remove else if once multimedia is propperly parsed to the controller
            boxImage.setImage(new Image("/imagenes/post_image_test.jpg", 450, 800, true, true));
            boxImage.setViewport(new Rectangle2D((boxImage.getImage().getWidth() - 450) / 2, (boxImage.getImage().getHeight() - 170) / 2, 450, 170));
        }

        // Profile Picture
        Multimedia profileM = author.getMultimedia();
        if (profileM != null && !profileM.getUri().equals("")) {
            boxImage.setImage(new Image(profileM.getUri(), 50, 50, true, true));
        }

        Reaction react = new Reaction(current, post, ReactionType.LikeIt);

        try {
            if(ApiFacade.getEntrypoint().getSearchFacade().isReact(react, current, current)){
                buttonLike.getGraphic().setStyle("-fx-fill: -piik-dark-pink;");
            }
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert();
        }

        buttonLike.setOnAction(this::handleLike);
        buttonAnswer.setOnAction(this::handleAnswer);
        deleteButton.setOnAction(this::handleDelete);
    }

    private void handleAnswer(Event event){
        try {
            ContextHandler.getContext().invokeStage("/gui/fxml/createAnswer.fxml", null, "Answer Post");
        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.showAlert();
        }

    }

    private void handleLike(Event event) {
        User current = ContextHandler.getContext().getCurrentUser();
        Reaction react = new Reaction(current, post, ReactionType.LikeIt);
        try {
            if(ApiFacade.getEntrypoint().getSearchFacade().isReact(react, current, current)) {
                ApiFacade.getEntrypoint().getDeletionFacade().removeReaction(react, current);
                buttonLike.getGraphic().setStyle("");
            } else {
                ApiFacade.getEntrypoint().getInsertionFacade().react(react, current);
                buttonLike.getGraphic().setStyle("-fx-fill: -piik-dark-pink;");
            }
            if (ContextHandler.getContext().getSearchController() != null) {
                ContextHandler.getContext().getSearchController().clear();
            }

        } catch (PiikInvalidParameters | PiikDatabaseException piikInvalidParameters) {
            piikInvalidParameters.showAlert();
        }

    }

    private void handleDelete(Event event){
        try{
            ApiFacade.getEntrypoint().getDeletionFacade().removePost(post,ContextHandler.getContext().getCurrentUser());
            if(ContextHandler.getContext().getUserProfileController() != null){
                ContextHandler.getContext().getUserProfileController().updateFeed();
                ContextHandler.getContext().getUserProfileController().updateArchivedPosts();
            }
            ContextHandler.getContext().getFeedController().updateFeed();
            if(ContextHandler.getContext().getSearchController() != null){
                ContextHandler.getContext().getSearchController().updatePostFeed();
            }
            if (ContextHandler.getContext().getFeedController() != null) {
                ContextHandler.getContext().getFeedController().updateFeed();
            }
            if (ContextHandler.getContext().getSearchController() != null) {
                ContextHandler.getContext().getSearchController().clear();
            }
        }catch (PiikException e){
            e.showAlert();
        }

    }
}
