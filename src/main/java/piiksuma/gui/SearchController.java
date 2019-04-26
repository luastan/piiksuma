package piiksuma.gui;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.event.Event;
import javafx.scene.control.ScrollPane;
import piiksuma.Post;
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;


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

    private ObservableList<Post> feed;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        back.setOnAction(this::backButton);
        Search.setOnAction(this::handleSearch);

        feed = FXCollections.observableArrayList();

        ContextHandler.getContext().setSearchController(this);
        setUpFeedPostListener();

        updateFeed();
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
        updateFeed();
    }

    public void updateFeed() {
        // TODO: update the feed propperly
        feed.clear();
        if (searchText.getText().isEmpty()){
            searchText.setText("");
        }
        feed.addAll(new QueryMapper<Post>(ApiFacade.getEntrypoint().getConnection()).defineClass(Post.class).createQuery("SELECT * FROM post WHERE text LIKE ?;")
                .defineParameters("%"+searchText.getText()+"%").list());
    }

    /**
     * Code to be executed when the feed gets updated. Posts at the interface
     * have to be updated.
     * <p>
     * Recieves change performed to the list via {@link ListChangeListener#onChanged(ListChangeListener.Change)}
     */
    private void setUpFeedPostListener() {
        feed.addListener((ListChangeListener<? super Post>) change -> {
            searchMasonryPane.getChildren().clear();
            feed.forEach(this::insertPost);
        });
    }

    private void setUpFeedUserListener() {
        feed.addListener((ListChangeListener<? super Post>) change -> {
            searchMasonryPane.getChildren().clear();
            feed.forEach(this::insertPost);
        });
    }
    private void setUpFeedEventListener() {
        feed.addListener((ListChangeListener<? super Post>) change -> {
            searchMasonryPane.getChildren().clear();
            feed.forEach(this::insertPost);
        });
    }


    private void insertPost(Post post) {
        FXMLLoader postLoader = new FXMLLoader(this.getClass().getResource("/gui/fxml/searched.fxml"));
        postLoader.setController(new SearchedPostController(post));
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
