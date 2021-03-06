package piiksuma.gui.posts;

import com.jfoenix.controls.JFXButton;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import piiksuma.*;
import piiksuma.Utilities.PiikLogger;
import piiksuma.api.ApiFacade;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikException;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.Alert;
import piiksuma.gui.ContextHandler;
import piiksuma.gui.hashtag.HashtagPreviewController;
import piiksuma.gui.profiles.ProfilePreviewController;
import piiksuma.gui.profiles.UserProfileController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

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
    private StackPane profilePicture;

    @FXML
    private HBox hashtags;

    @FXML
    private ScrollPane hashtagsContainer;

    @FXML
    private JFXButton repost;

    @FXML
    private JFXButton archiveButton;

    @FXML
    private JFXButton loveItButton;

    @FXML
    private JFXButton hateItButton;

    @FXML
    private JFXButton makesMeAngry;

    @FXML
    private JFXButton replies;

    @FXML
    private JFXButton mainButton;

    @FXML
    private StackPane boxImageContainer;

    private Post post;

    public PostController(Post post) {
        this.post = post;
//        try {
//            this.post = ApiFacade.getEntrypoint().getSearchFacade().getPost(post, ContextHandler.getContext().getCurrentUser());
//        } catch (PiikDatabaseException | PiikInvalidParameters e) {
//            e.showAlert();
//        }
    }

    /**
     * Inits the window components
     * @param location
     * @param resources
     */
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
            e.showAlert(e, "Failure user not found");
        }

        post.setAuthor(author);

        replies.setOnAction(event -> {
            try {
                ContextHandler.getContext().invokeStage("/gui/fxml/postAnswers.fxml", new AnswerPostsController(post));
            } catch (PiikInvalidParameters invalidParameters) {
                invalidParameters.showAlert(invalidParameters, "Failure loading the postAnswes window");
            }
        });

        postContent.setText(post.getText());
        authorName.setText(post.getAuthor().getName());
        authorId.setText(post.getAuthor().getId());

        // Multimedia insertion
        if (post.getMultimedia() != null && post.getMultimedia().getHash() != null
                && !post.getMultimedia().getHash().isEmpty()) {
            if (post.getMultimedia().getUri() == null || post.getMultimedia().getUri().isEmpty()) {
                try {
                    post.setMultimedia(ApiFacade.getEntrypoint().getSearchFacade().getMultimedia(post.getMultimedia(),
                            current));
                } catch (PiikInvalidParameters | PiikDatabaseException piikInvalidParameters) {
                    piikInvalidParameters.showAlert(piikInvalidParameters, "Failure loading the image");
                }
            }

            boxImage.setImage(new Image(post.getMultimedia().getUri(), 450, 800, true, true));
            boxImage.setViewport(new Rectangle2D((boxImage.getImage().getWidth() - 450) / 2, (boxImage.getImage().getHeight() - 170) / 2, 450, 170));
        } else {
            boxImageContainer.setVisible(false);
        }

        // Profile Picture
        FXMLLoader profilePicLoader = new FXMLLoader(getClass().getResource("/gui/fxml/profile/profilePreview.fxml"));
        profilePicLoader.setController(new ProfilePreviewController(post.getAuthor()));
        try {
            profilePicture.getChildren().add(profilePicLoader.load());
        } catch (IOException e) {
            PiikLogger.getInstance().log(Level.SEVERE, "PostController -> initialize", e);
            Alert.newAlert().setHeading("Loading image error").addText("Failure loading the profile picture").addCloseButton().show();
        }

        Reaction react = new Reaction(current, post, ReactionType.LikeIt);

        try {
            if (ApiFacade.getEntrypoint().getSearchFacade().isReact(react, current, current)) {
                buttonLike.getGraphic().setStyle("-fx-fill: -piik-dark-pink;");
                mainButton.setDisable(true);
                hateItButton.setDisable(true);
                loveItButton.setDisable(true);
                hateItButton.setDisable(true);

            }
            react = new Reaction(current, post, ReactionType.LoveIt);
            if (ApiFacade.getEntrypoint().getSearchFacade().isReact(react, current, current)) {
                loveItButton.getGraphic().setStyle("-fx-fill: -piik-pastel-green;");
                buttonLike.setDisable(true);
                hateItButton.setDisable(true);
                makesMeAngry.setDisable(true);
            }
            react = new Reaction(current, post, ReactionType.HateIt);
            if (ApiFacade.getEntrypoint().getSearchFacade().isReact(react, current, current)) {
                hateItButton.getGraphic().setStyle("-fx-fill: -color-error;");
                buttonLike.setDisable(true);
                loveItButton.setDisable(true);
                makesMeAngry.setDisable(true);
            }
            react = new Reaction(current, post, ReactionType.MakesMeAngry);
            if (ApiFacade.getEntrypoint().getSearchFacade().isReact(react, current, current)) {
                makesMeAngry.getGraphic().setStyle("-fx-fill:-piik-orange-ripple;");
                buttonLike.setDisable(true);
                loveItButton.setDisable(true);
                hateItButton.setDisable(true);
            }
            if (ApiFacade.getEntrypoint().getSearchFacade().checkUserResposted(post.getAuthor(), post,
                    ContextHandler.getContext().getCurrentUser())) {
                repost.getGraphic().setStyle("-fx-fill: -piik-blue-green;");
            }
            if (ApiFacade.getEntrypoint().getPostDao().isPostArchived(post, ContextHandler.getContext().getCurrentUser())) {
                archiveButton.getGraphic().setStyle("-fx-fill: -piik-pastel-green;");
            }
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert(e, "Failure loading the reactions to the post");
        }

        buttonLike.setOnAction(this::handleLike);
        buttonAnswer.setOnAction(this::handleAnswer);
        deleteButton.setOnAction(this::handleDelete);
        repost.setOnAction(this::handleRepost);
        archiveButton.setOnAction(this::handleArchive);
        deleteButton.setVisible(post.getAuthor().equals(current) || current.getType().equals(UserType.administrator));

        loveItButton.setOnAction(this::handleLoveIt);
        hateItButton.setOnAction(this::handleHateIt);
        makesMeAngry.setOnAction(this::handleAngry);
//        FontAwesomeIcon.HASHTAG

        // Hashtags
        if (post.getHashtags().size() > 0) {
            post.getHashtags().forEach(this::addHashtag);
        } else {
            hashtagsContainer.setStyle("-fx-pref-height: 0;");
        }
    }

    /**
     * Funtion to be executed when love it reaction is pressed
     * @param event Event on the window
     */
    private void handleLoveIt(Event event){
        Reaction reaction = new Reaction(ContextHandler.getContext().getCurrentUser(), post, ReactionType.LoveIt);
        try {
            if (ApiFacade.getEntrypoint().getSearchFacade().isReact(reaction, ContextHandler.getContext().getCurrentUser(), ContextHandler.getContext().getCurrentUser())) {
                ApiFacade.getEntrypoint().getDeletionFacade().removeReaction(reaction, ContextHandler.getContext().getCurrentUser());
                loveItButton.getGraphic().setStyle("");
                hateItButton.setDisable(false);
                makesMeAngry.setDisable(false);
                buttonLike.setDisable(false);
            } else {
                ApiFacade.getEntrypoint().getInsertionFacade().react(reaction, ContextHandler.getContext().getCurrentUser());
                loveItButton.getGraphic().setStyle("-fx-fill: -piik-pastel-green;");
                hateItButton.setDisable(true);
                makesMeAngry.setDisable(true);
                buttonLike.setDisable(true);
            }

        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert(e, "Failure loving the post");
        }
    }

    /**
     * Funtion to be executed when hate it reaction is pressed
     * @param event Event on the window
     */
    private void handleHateIt(Event event){
        Reaction reaction = new Reaction(ContextHandler.getContext().getCurrentUser(), post, ReactionType.HateIt);
        try {
            if (ApiFacade.getEntrypoint().getSearchFacade().isReact(reaction, ContextHandler.getContext().getCurrentUser(), ContextHandler.getContext().getCurrentUser())) {
                ApiFacade.getEntrypoint().getDeletionFacade().removeReaction(reaction, ContextHandler.getContext().getCurrentUser());
                hateItButton.getGraphic().setStyle("");
                loveItButton.setDisable(false);
                makesMeAngry.setDisable(false);
                buttonLike.setDisable(false);
            } else {
                ApiFacade.getEntrypoint().getInsertionFacade().react(reaction, ContextHandler.getContext().getCurrentUser());
                hateItButton.getGraphic().setStyle("-fx-fill: -color-error;");
                loveItButton.setDisable(true);
                makesMeAngry.setDisable(true);
                buttonLike.setDisable(true);
            }
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert(e, "Failure hating the post");
        }
    }

    /**
     * Funtion to be executed when angry reaction is pressed
     * @param event Event on the window
     */
    private void handleAngry(Event event){
        Reaction reaction = new Reaction(ContextHandler.getContext().getCurrentUser(), post, ReactionType.MakesMeAngry);
        try {
            if (ApiFacade.getEntrypoint().getSearchFacade().isReact(reaction, ContextHandler.getContext().getCurrentUser(), ContextHandler.getContext().getCurrentUser())) {
                ApiFacade.getEntrypoint().getDeletionFacade().removeReaction(reaction, ContextHandler.getContext().getCurrentUser());
                makesMeAngry.getGraphic().setStyle("");
                loveItButton.setDisable(false);
                hateItButton.setDisable(false);
                buttonLike.setDisable(false);
            } else {
                ApiFacade.getEntrypoint().getInsertionFacade().react(reaction, ContextHandler.getContext().getCurrentUser());
                makesMeAngry.getGraphic().setStyle("-fx-fill: -piik-orange-ripple;");
                loveItButton.setDisable(true);
                hateItButton.setDisable(true);
                buttonLike.setDisable(true);
            }
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert(e, "Failure getting angry at the post");
        }
    }

    private void handleArchive(Event event) {
        try {
            if (ApiFacade.getEntrypoint().getPostDao().isPostArchived(post, ContextHandler.getContext().getCurrentUser())) {
                ApiFacade.getEntrypoint().getDeletionFacade().removeArchivedPost(post, ContextHandler.getContext().getCurrentUser(), ContextHandler.getContext().getCurrentUser());
                archiveButton.getGraphic().setStyle("");
            } else {
                ApiFacade.getEntrypoint().getInsertionFacade().archivePost(post, ContextHandler.getContext().getCurrentUser(), ContextHandler.getContext().getCurrentUser());
                archiveButton.getGraphic().setStyle("-fx-fill: -piik-pastel-green;");
            }
            UserProfileController profileController = ContextHandler.getContext().getUserProfileController();
            if (profileController != null) {
                profileController.updateArchivedPosts();
                profileController.updateFeed();
            }
        } catch (PiikDatabaseException | PiikInvalidParameters e) {
            e.showAlert(e, "Failure archiving the post");
        }
    }

    /**
     * Adds a hashtag to post's hashtag list
     *
     * @param hashtag Hashtag to be inserted in the HBox
     */
    private void addHashtag(Hashtag hashtag) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/hashtags/hashtagPreview.fxml"));
        loader.setController(new HashtagPreviewController(hashtag));
        try {
            hashtags.getChildren().add(loader.load());
        } catch (IOException e) {
            PiikLogger.getInstance().log(Level.SEVERE, "PostController -> addHashtag", e);
            Alert.newAlert().setHeading("Adding hashtag error").addText("Failure adding the hashtag").addCloseButton().show();
        }

    }

    /**
     * Function to answer a post
     * @param event Event on the window
     */
    private void handleAnswer(Event event){
        try {
            ContextHandler.getContext().invokeStage("/gui/fxml/createPost.fxml", new CreatePostController(post), "Create Post");
        } catch (PiikInvalidParameters invalidParameters) {
            invalidParameters.showAlert(invalidParameters, "Failure loading the answer");
        }

    }
    /**
     * Funtion to be executed when like reaction is pressed
     * @param event Event on the window
     */
    private void handleLike(Event event) {
        User current = ContextHandler.getContext().getCurrentUser();
        Reaction react = new Reaction(current, post, ReactionType.LikeIt);
        try {
            if (ApiFacade.getEntrypoint().getSearchFacade().isReact(react, current, current)) {
                ApiFacade.getEntrypoint().getDeletionFacade().removeReaction(react, current);
                buttonLike.getGraphic().setStyle("");
                mainButton.setDisable(false);
            } else {


                ApiFacade.getEntrypoint().getInsertionFacade().react(react, current);
                buttonLike.getGraphic().setStyle("-fx-fill: -piik-dark-pink;");
                mainButton.setDisable(true);
            }
        } catch (PiikInvalidParameters | PiikDatabaseException piikInvalidParameters) {
            piikInvalidParameters.showAlert(piikInvalidParameters, "Failure reacting to the post");
        }

    }

    /**
     * Function to delete a post
     * @param event Event on the window
     */
    private void handleDelete(Event event){
        try{
            ApiFacade.getEntrypoint().getDeletionFacade().removePost(post,ContextHandler.getContext().getCurrentUser());
            if(ContextHandler.getContext().getUserProfileController() != null){
                ContextHandler.getContext().getUserProfileController().updateFeed();
                ContextHandler.getContext().getUserProfileController().updateArchivedPosts();
            }
            ContextHandler.getContext().getFeedController().updateFeed();
            if (ContextHandler.getContext().getSearchController() != null) {
                ContextHandler.getContext().getSearchController().updatePostFeed();
            }
            if (ContextHandler.getContext().getFeedController() != null) {
                ContextHandler.getContext().getFeedController().updateFeed();
            }
            if (ContextHandler.getContext().getSearchController() != null) {
                ContextHandler.getContext().getSearchController().updatePostFeed();
            }
        } catch (PiikException e) {
            e.showAlert(e, "Failure deleting the post");
        }
    }

    /**
     * Function to repost a post
     * @param event Event on the window
     */
    private void handleRepost(Event event) {
        try {
            if (ApiFacade.getEntrypoint().getSearchFacade().checkUserResposted(post.getAuthor(), post,
                    ContextHandler.getContext().getCurrentUser())) {
                // If the repost exists it gets deleted and the graphic gets grey color

                ApiFacade.getEntrypoint().getDeletionFacade().removeRePost(post, ContextHandler.getContext().getCurrentUser());
                repost.getGraphic().setStyle("");
            } else {

                // If the repost doesn't exist it gets reposted and the graphic gets green color
                ApiFacade.getEntrypoint().getInsertionFacade().repost(ContextHandler.getContext().getCurrentUser(), post,
                        post.getAuthor(), ContextHandler.getContext().getCurrentUser());
                repost.getGraphic().setStyle("-fx-fill: -piik-blue-green;");
            }
//            ContextHandler.getContext().getFeedController().updateFeed();
        } catch (PiikInvalidParameters | PiikDatabaseException e) {
            e.showAlert(e, "Failure re-posting");
        }
    }
}
