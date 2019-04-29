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
            e.printStackTrace();
        }

        post.setAuthor(author);

        postContent.setText(post.getText());
        authorName.setText(post.getAuthor().getName());
        authorId.setText(post.getAuthor().getId());

        // Multimedia insertion
        if (post.getMultimedia() != null && post.getMultimedia().getUri() != null && !post.getMultimedia().getUri().equals("")) {
            boxImage.setImage(new Image(post.getMultimedia().getUri(), 450, 800, true, true));
            boxImage.setViewport(new Rectangle2D((boxImage.getImage().getWidth() - 450) / 2, (boxImage.getImage().getWidth() - 170) / 2, 450, 170));
        } else if (post.getAuthor().getId().equals("usr1")) {  // TODO: Remove else if once multimedia is propperly parsed to the controller
            boxImage.setImage(new Image("/imagenes/post_image_test.jpg", 450, 800, true, true));
            boxImage.setViewport(new Rectangle2D((boxImage.getImage().getWidth() - 450) / 2, (boxImage.getImage().getHeight() - 170) / 2, 450, 170));
        }

        // Profile Picture
        Multimedia profileM = author.getMultimedia();
        if (profileM != null && !profileM.getUri().equals("")) {
            boxImage.setImage(new Image(post.getMultimedia().getUri(), 50, 50, true, true));
        }


        buttonLike.setOnAction(this::handleLike);
        buttonAnswer.setOnAction(this::handleAnswer);
        deleteButton.setOnAction(this::handleDelete);
    }

    private void handleAnswer(Event event){
        Stage newPostStage = new Stage();

        try {
            ContextHandler.getContext().register("Answer Post", newPostStage);
        } catch (PiikInvalidParameters e) {
            e.printStackTrace();
            return;
        }
        // Stage configuration
        newPostStage.setTitle("Answer Post");
        newPostStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/createAnswer.fxml"));

        CreatePostController controller = new CreatePostController();

        controller.setPostFather(post);

        loader.setController(controller);
        JFXDecorator decorator;

        try {
            decorator = new JFXDecorator(newPostStage, loader.load(), false, false, true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(decorator, 450, 550);


        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm(),
                getClass().getResource("/gui/css/main.css").toExternalForm()
        );
        newPostStage.initModality(Modality.WINDOW_MODAL);
        newPostStage.initOwner(ContextHandler.getContext().getStage("primary"));
        newPostStage.setScene(scene);
        // Show
        newPostStage.show();
    }

    private void handleLike(Event event) {
        User current = ContextHandler.getContext().getCurrentUser();
        Reaction react = new Reaction(current, post, ReactionType.LikeIt);
        try {

            if(ApiFacade.getEntrypoint().getSearchFacade().isReact(react, current, current)) {
                ApiFacade.getEntrypoint().getDeletionFacade().removeReaction(react, current);
                System.out.println("Like!");
                buttonLike.setStyle("-fx-background-color: #FF0000;");
            } else {
                ApiFacade.getEntrypoint().getInsertionFacade().react(react, current);
                System.out.println("Dislike!");
                buttonLike.setStyle("-fx-background-color: rgba(255,255,255,0);");
            }
        } catch (PiikInvalidParameters | PiikDatabaseException piikInvalidParameters) {
            piikInvalidParameters.printStackTrace();
        }
    }

    private void handleDelete(Event event){
        try{
            ApiFacade.getEntrypoint().getDeletionFacade().removePost(post,ContextHandler.getContext().getCurrentUser());
            ContextHandler.getContext().getFeedController().updateFeed();
            if(ContextHandler.getContext().getUserProfileController() != null){
                ContextHandler.getContext().getUserProfileController().updateFeed();
            }
            if(ContextHandler.getContext().getOtherUserProfileController() != null){
                ContextHandler.getContext().getOtherUserProfileController().updateFeed();
            }
            if(ContextHandler.getContext().getSearchController() != null){
                ContextHandler.getContext().getSearchController().updatePostFeed();
            }


        }catch (PiikException e){
            System.out.println("PROBLEM");
            return;
        }

        System.out.println("Eliminado con exito");
    }
}
