package piiksuma;

import javafx.application.Application;
import javafx.stage.Stage;
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;
import piiksuma.gui.ContextHandler;

public class Arranque extends Application {
    public static void main(String[] args) {
//        SampleFachada.getDb().ejemploGETFK();
        //      SampleFachada.getDb().pruebasCheck();
        // SampleFachada.getDb().idGenerationTest();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Registers current stage into the Application Context

        ContextHandler.getContext().setCurrentUser(new QueryMapper<User>(ApiFacade.getEntrypoint().getConnection()).defineClass(User.class).createQuery("SELECT * FROM piiuser;").findFirst());
        ContextHandler.getContext().getCurrentUser().toString();
        ContextHandler.getContext().stageJuggler();
//        ContextHandler.getContext().register("primary", primaryStage);
    }
}
