package piiksuma;

import com.jfoenix.controls.JFXDecorator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import piiksuma.gui.ContextHandler;

public class Arranque extends Application {
    public static void main(String[] args) {

//        SampleFachada.getDb().ejemploGETFK();
  //      SampleFachada.getDb().pruebasCheck();
        //SampleFachada.getDb().idGenerationTest();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {// Registers current stage into the Application Context

        //THIS CODE IS TO SHOW THE LOGIN WINDOW, UNCOMMENT THE NEXT CODE TO SHOW THE MAIN WINDOW
        ContextHandler.getContext().register("primary", primaryStage);
        // Stage configuration
        primaryStage.setTitle("Piiksuma");
        primaryStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/main.fxml"));

        // Decorator which is the visual whindow frame, holding close button title and minimize
        JFXDecorator decorator = new JFXDecorator(primaryStage, loader.load(), false, false, true);
        // TODO: Add a logo and a cool title to the JFXDecorator

        // Scene definition & binding to the Primary Stage
        Scene scene = new Scene(decorator, 450, 850);
        primaryStage.setScene(scene);
        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm(),
                getClass().getResource("/gui/css/main.css").toExternalForm()
        );

        // Show
        primaryStage.show();

    }
}
