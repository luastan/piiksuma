package piiksuma;

import javafx.application.Application;
import javafx.stage.Stage;
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;
import piiksuma.database.SampleFachada;
import piiksuma.gui.ContextHandler;
import piiksuma.piikLog.PiikLogger;

import java.util.logging.Level;

public class Arranque extends Application {
    public static void main(String[] args) {
//        SampleFachada.getDb().ejemploGETFK();
        //      SampleFachada.getDb().pruebasCheck();
        // SampleFachada.getDb().idGenerationTest();
        SampleFachada.getDb().aTest();
        PiikLogger.getInstance().log(Level.SEVERE, "prueba");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        User user = new QueryMapper<User>(ApiFacade.getEntrypoint().getConnection()).defineClass(User.class).createQuery("SELECT * FROM piiuser where id='usr1';").findFirst();
        user.setPass("supercontraseña");

        // Comment to use the login
//        ContextHandler.getContext().setCurrentUser(ApiFacade.getEntrypoint().getSearchFacade().login(user));

        ContextHandler.getContext().stageJuggler(primaryStage);
    }
}
