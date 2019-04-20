package piiksuma;

import com.jfoenix.controls.JFXDecorator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import piiksuma.database.QueryMapper;
import piiksuma.database.SampleFachada;
import piiksuma.gui.ContextHandler;

import javax.management.Query;
import java.sql.Timestamp;
import java.time.Instant;

public class Arranque extends Application {
    public static void main(String[] args) {

        SampleFachada.getDb().ejemploGETFK();
        SampleFachada.getDb().pruebasCheck();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Registers current stage into the Application Context
        ContextHandler.getContext().register("primary", primaryStage);

        // Stage configuration
        primaryStage.setTitle("Piiksuma");
        primaryStage.setResizable(false);

        // Defines the loader to be used later
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/main.fxml"));

        // Decorator which is the visual whindow frame, holding close button title and minimize
        JFXDecorator decorator = new JFXDecorator(primaryStage, loader.load(), false, false, true);
        // TODO: Add a logo and a cool title to the JFXDecorator

        // Scene definition & binding to the Primary Stage
        Scene scene = new Scene(decorator, 450, 800);
        primaryStage.setScene(scene);

        // Stylesheet loading
        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm()
        );

        // Show
        primaryStage.show();
    }
}
