package piiksuma.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXTabPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import piiksuma.exceptions.PiikInvalidParameters;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SearchController implements Initializable {

    @FXML
    private JFXButton back;

    @FXML
    private JFXButton Search;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        back.setOnAction(this::backButton);
        Search.setOnAction(this::handleSearch);
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

        Stage searchStage = new Stage();

        try {
            ContextHandler.getContext().register("search", searchStage);
        }catch(PiikInvalidParameters e){
            e.printStackTrace();
            return;
        }
        // Stage configuration
        searchStage.setTitle("Search");
        searchStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/searchWindow2.fxml"));
        JFXDecorator decorator;

        try {
            decorator = new JFXDecorator(searchStage, loader.load(), false, false, true);
        }catch (IOException e){
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(decorator, 450, 550);


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


}
