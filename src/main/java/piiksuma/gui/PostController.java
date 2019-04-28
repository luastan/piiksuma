package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import piiksuma.*;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikException;
import piiksuma.exceptions.PiikInvalidParameters;

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

    private Post post;

    public PostController(Post post) {
        this.post = post;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        User author = post.getAuthor();

        if (ContextHandler.getContext().getCurrentUser().getType() == null) { //TODO fix this
            deleteButton.setVisible(false);
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

        buttonLike.setOnAction(this::handleLike);
        buttonAnswer.setOnAction(this::handleAnswer);
        deleteButton.setOnAction(this::handleDelete);
    }

    private void handleAnswer(Event event){

    }

    private void handleLike(Event event) {
        User current = ContextHandler.getContext().getCurrentUser();
        Reaction react = new Reaction(current, post, ReactionType.LikeIt);
        try {
            ApiFacade.getEntrypoint().getInsertionFacade().react(react, current);

            System.out.println("Like!");
            //buttonLike.setStyle("-icons-color: #FF0000;");
        } catch (PiikInvalidParameters | PiikDatabaseException piikInvalidParameters) {

            // TODO Este contains podría modificarse con una excepción nueva. O que dentro de las excepciones
            // haya códigos de error
            if (piikInvalidParameters.getMessage().contains("duplicate key")) {
                try {
                    ApiFacade.getEntrypoint().getDeletionFacade().removeReaction(react, current);
                    System.out.println("Dislike!");
                } catch (PiikDatabaseException | PiikInvalidParameters e) {
                    e.printStackTrace();
                }
            } else {
                piikInvalidParameters.getStackTrace();
            }
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
