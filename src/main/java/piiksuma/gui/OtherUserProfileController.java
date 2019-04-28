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
import piiksuma.User;
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class OtherUserProfileController implements Initializable {

    @FXML
    private Label userName;

    @FXML
    private Label description;
    @FXML
    private ScrollPane postScrollPane;

    @FXML
    private JFXMasonryPane postMasonryPane;
    private ObservableList<Post> feed;

    @FXML
    private JFXButton messageButton;

    @FXML
    private JFXButton blockButton;

    @FXML
    private JFXButton followButton;

    @FXML
    private JFXButton deleteButton;

    @FXML
    private JFXButton silenceButton;

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        ContextHandler.getContext().getCurrentUser().setName("OswaldOswin");
        // userName.setText(ContextHandler.getContext().getCurrentUser().getName());
        if(ContextHandler.getContext().getCurrentUser().getType() == null){ //TODO me daba error con getType.equals(UserType.USER),puse esto de momento
            deleteButton.setVisible(false);
        }
        feed = FXCollections.observableArrayList();
        userName.setText(user.getName());
        description.setText(user.getDescription());
        blockButton.setOnAction(this::handleBlockButton);
        followButton.setOnAction(this::handleFollowButton);
        deleteButton.setOnAction(this::handleDeleteButton);
        silenceButton.setOnAction(this::handleSilence);

        ContextHandler.getContext().setOtherUserProfileController(this);
        setUpFeedListener();

        try {
            updateFeed();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }
        messageButton.setOnAction(this::handleMessageButton);
    }

    private void handleSilence(Event event){

        boolean silenced = false;
        List<Map<String,Object>> lista;
        try {
            lista = new QueryMapper<>(ApiFacade.getEntrypoint().getConnection()).createQuery("SELECT silenced from silenceuser WHERE usr = ?")
                    .defineParameters(ContextHandler.getContext().getCurrentUser().getId()).mapList();
            for (Map<String,Object> row : lista){
                if (row.containsValue(user.getId())){
                    silenced=true;
                    break;
                }
            }
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }
        if (!silenced) {
            try{
                ApiFacade.getEntrypoint().getInsertionFacade().silenceUser(user, ContextHandler.getContext().getCurrentUser());
            }catch (PiikDatabaseException e){
                System.out.println(e.getMessage());
                return;
            }catch (PiikInvalidParameters e1){
                System.out.println(e1.getMessage());
                return;
            }
            System.out.println("SILENCED");

        }else{
            try {
                ApiFacade.getEntrypoint().getDeletionFacade().unsileceUser(user,ContextHandler.getContext().getCurrentUser());
                System.out.println("UNSILENCED");
            } catch (PiikDatabaseException e) {
                e.printStackTrace();
            } catch (PiikInvalidParameters piikInvalidParameters) {
                piikInvalidParameters.printStackTrace();
            }
        }
    }

    public void updateFeed() throws PiikDatabaseException {
        // TODO: update the feed propperly
        feed.clear();
        feed.addAll(new QueryMapper<Post>(ApiFacade.getEntrypoint().getConnection()).defineClass(Post.class).createQuery("SELECT * FROM post;").list());
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
    private void handleMessageButton(Event event) {

        Stage searchStage = new Stage();

        try {
            ContextHandler.getContext().register("Start chat", searchStage);
        } catch (
                PiikInvalidParameters e) {
            e.printStackTrace();
            return;
        }
        // Stage configuration
        searchStage.setTitle("Start chat");
        searchStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/startChatFromProfile.fxml"));
        JFXDecorator decorator;
        loader.setController(new MessageFromProfileController(user));

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

    private void handleBlockButton(Event event){
        boolean blocked = false;
        List<Map<String,Object>> lista;
        try {
            lista = new QueryMapper<>(ApiFacade.getEntrypoint().getConnection()).createQuery("SELECT blocked from blockUser WHERE usr = ?")
                    .defineParameters(ContextHandler.getContext().getCurrentUser().getId()).mapList();
            for (Map<String,Object> row : lista){
                if (row.containsValue(user.getId())){
                    blocked=true;
                    break;
                }
            }
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }
        if (!blocked) {
            try {
                ApiFacade.getEntrypoint().getInsertionFacade().blockUser(user, ContextHandler.getContext().getCurrentUser(),ContextHandler.getContext().getCurrentUser());
                System.out.println("BLOCKED");
            } catch (PiikDatabaseException e) {
                e.printStackTrace();
            } catch (PiikInvalidParameters piikInvalidParameters) {
                piikInvalidParameters.printStackTrace();
            }
        }else{
            try {
                ApiFacade.getEntrypoint().getDeletionFacade().unblockUser(user,ContextHandler.getContext().getCurrentUser(),ContextHandler.getContext().getCurrentUser());
                System.out.println("UNBLOCKED");
            } catch (PiikDatabaseException e) {
                e.printStackTrace();
            } catch (PiikInvalidParameters piikInvalidParameters) {
                piikInvalidParameters.printStackTrace();
            }
        }
    }

    private void handleFollowButton(Event event){
        boolean followed = false;
        List<Map<String,Object>> lista;
        try {
            lista = new QueryMapper<>(ApiFacade.getEntrypoint().getConnection()).createQuery("SELECT followed from followUser WHERE follower = ?")
                    .defineParameters(ContextHandler.getContext().getCurrentUser().getId()).mapList();
            for (Map<String,Object> row : lista){
                if(row.containsValue(user.getId())){
                    followed=true;
                }
            }
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }
        if (!followed){
            try {
                ApiFacade.getEntrypoint().getInsertionFacade().followUser(user,ContextHandler.getContext().getCurrentUser(),ContextHandler.getContext().getCurrentUser());
                System.out.println("SEGUIDO");
            } catch (PiikDatabaseException e) {
                e.printStackTrace();
            } catch (PiikInvalidParameters piikInvalidParameters) {
                piikInvalidParameters.printStackTrace();
            }

        }else{
            try {
                ApiFacade.getEntrypoint().getDeletionFacade().unfollowUser(user,ContextHandler.getContext().getCurrentUser(),ContextHandler.getContext().getCurrentUser());
                System.out.println("DEJADO DE SEGUIR");
            } catch (PiikDatabaseException e) {
                e.printStackTrace();
            } catch (PiikInvalidParameters piikInvalidParameters) {
                piikInvalidParameters.printStackTrace();
            }

        }
    }

    private void handleDeleteButton(Event event){
        //We remove the user from the database
        try{
            ApiFacade.getEntrypoint().getDeletionFacade().removeUser(user,ContextHandler.getContext().getCurrentUser());
        }catch (PiikException e){
            //TODO handle exception
            return;
        }
        //Now we close the profile window
        ContextHandler.getContext().getStage("User Profile").close();
    }
}
