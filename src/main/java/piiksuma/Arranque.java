package piiksuma;

import javafx.application.Application;
import javafx.stage.Stage;
import piiksuma.api.ApiFacade;
import piiksuma.database.QueryMapper;
import piiksuma.gui.ContextHandler;

public class Arranque extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        User user = new QueryMapper<User>(ApiFacade.getEntrypoint().getConnection()).defineClass(User.class).createQuery("SELECT * FROM piiuser where id='Alvaru';").findFirst();
        user.setPass("sesamo");

        // Comment to use the login
        ContextHandler.getContext().setCurrentUser(ApiFacade.getEntrypoint().getSearchFacade().login(user));

        ContextHandler.getContext().stageJuggler(primaryStage);
    }
}
