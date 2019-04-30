package piiksuma.gui;

import com.jfoenix.controls.JFXDecorator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import piiksuma.User;
import piiksuma.exceptions.PiikInvalidParameters;
import piiksuma.gui.deckControllers.AchievementsController;
import piiksuma.gui.events.EventsController;
import piiksuma.gui.profiles.UserProfileController;
import piiksuma.gui.search.SearchController;
import piiksuma.gui.tickets.TicketsController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generifies handling multiple Stages, Nodes and other objects between
 * application controllers.
 *
 * @author luastan
 * @author CardamaS99
 * @author alvrogd
 * @author danimf99
 * @author OswaldOswin1
 * @author Marcos-marpin
 */
public class ContextHandler {
    private static ContextHandler context = new ContextHandler();

    private Map<String, Node> contextElements;  // Static Nodes to be used between multiple distinct classes
    private Map<String, Stage> contextStages;   // Static Stages to easily switch between them

    // Piiksuma static instances

    private User currentUser;   // User logged into the application
    private FeedController feedController;
    private EventsController eventsController;
    private MessagesController messagesController;
    private SearchController searchController;
    private AchievementsController achievementsController;
    private NotificationsController notificationsController;
    private ConversationController conversationController;
    private TicketsController ticketsController;
    private UserProfileController userProfileController;


    public NotificationsController getNotificationsController() {
        return notificationsController;
    }

    public void setNotificationsController(NotificationsController notificationsController) {
        this.notificationsController = notificationsController;
    }

    /**
     * Private Contrstructor since this is a Singleton Class
     */
    private ContextHandler() {
        contextElements = new HashMap<>();
        contextStages = new HashMap<>();
    }

    /**
     * Registers an element (JavaFx's Node) into the current context
     *
     * @param name Name used to refer the node
     * @param node Node reference to be saved
     * @throws PiikInvalidParameters When null parameters are given
     */
    public void register(String name, Node node) throws PiikInvalidParameters {
        if (name == null || node == null) {
            throw new PiikInvalidParameters("(Context handler) Can't register null values");
        }
        contextElements.put(name, node);
    }

    /**
     * Registers a Stage in the current context
     *
     * @param name  Name to identify the Stage
     * @param stage Stage to be stored
     * @throws PiikInvalidParameters When parameters are null
     */
    public void register(String name, Stage stage) throws PiikInvalidParameters {
        if (name == null || stage == null) {
            throw new PiikInvalidParameters("(Context handler) Can't register null values");
        }
        contextStages.put(name, stage);
    }


    /**
     * Unregisters a Node with the given name from the current context
     *
     * @param name String used to identify the Node
     * @throws PiikInvalidParameters Either the string was null or the node
     *                               isn't present at the current context.
     */
    public void unregisterNode(String name) throws PiikInvalidParameters {
        if (contextElements.remove(name) == null) {
            throw new PiikInvalidParameters("(Context handler) Couldn't find " + name + " at current context.");
        }
    }

    /**
     * Unregisters a Stage from the current context given its name.
     *
     * @param name String refering the Stage
     * @throws PiikInvalidParameters Either the string was null or the stage
     *                               isn't present at the current context.
     */
    public void unregisterStage(String name) throws PiikInvalidParameters {
        if (contextStages.remove(name) == null) {
            throw new PiikInvalidParameters("(Context handler) Couldn't find " + name + " at current context.");
        }
    }

    /**
     * Returns a Node given its name
     *
     * @param name String used to refer the wanted Node
     * @return Desired node
     */
    public Node getElement(String name) {
        return contextElements.get(name);
    }


    /**
     * Returns a Stage given its name
     *
     * @param name String used to identify the Stage
     * @return Desired Stage
     */
    public Stage getStage(String name) {
        return contextStages.get(name);
    }

    /**
     * Looks for a Stage currently showing. Returns the first one that it finds
     *
     * @return A stage which is currently showing on the Screen
     */
    public Stage getCurrentStage() {
        return contextStages.values().stream().filter(Window::isShowing).findAny()
                .orElse(null);
    }

    /**
     * Returns current Application Context
     *
     * @return Single context instance
     */
    public static ContextHandler getContext() {
        return context;
    }

    /**
     * Sets the current context. It should be only used on units tests for easy
     * mocking
     *
     * @param context Context to be set.
     */
    public static void setContext(ContextHandler context) {
        ContextHandler.context = context;
    }


    /* Custom Static instances */


    /**
     * Returns logged-in user
     *
     * @return Current user using the application
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Effectively logs-in a User
     *
     * @param currentUser User which will be using the application
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }


    /**
     * Rreturns Feed Controller
     *
     * @return Current feedController if existent
     */
    public FeedController getFeedController() {
        return feedController;
    }

    public void setFeedController(FeedController feedController) {
        this.feedController = feedController;
    }

    public EventsController getEventsController() {
        return eventsController;
    }

    public void setEventsController(EventsController eventsController) {
        this.eventsController = eventsController;
    }

    public AchievementsController getAchievementsController() {
        return achievementsController;
    }

    public void setAchievementsController(AchievementsController achievementsController) {
        this.achievementsController = achievementsController;
    }

    public MessagesController getMessagesController() {
        return messagesController;
    }

    public void setMessagesController(MessagesController messagesController) {
        this.messagesController = messagesController;
    }

    public SearchController getSearchController() {
        return searchController;
    }

    public void setSearchController(SearchController searchController) {
        this.searchController = searchController;
    }


    public TicketsController getTicketsController() {
        return ticketsController;
    }

    public void setTicketsController(TicketsController ticketsController) {
        this.ticketsController = ticketsController;
    }

    public UserProfileController getUserProfileController() {
        return userProfileController;
    }

    public void setUserProfileController(UserProfileController userProfileController) {
        this.userProfileController = userProfileController;
    }

    /**
     * Closes and deletes all the registered stages. Then loads the
     * corresponding Window depending on whether or not the User is logged in
     */
    public void stageJuggler() {
        stageJuggler(new Stage());
    }

    /**
     * Closes and deletes all the registered stages. Then loads the
     * corresponding Window depending on whether or not the User is logged in
     *
     * @param newStage Uninitialized Stage to be used
     */
    public void stageJuggler(Stage newStage) {
        this.contextStages.forEach((s, stage) -> stage.close());
        this.contextStages.clear();
        FXMLLoader loader;
        // Stage configuration

        newStage.setResizable(false);

        // Decorator which is the visual whindow frame, holding close button title and minimize
        JFXDecorator decorator;
        try {
            if (currentUser != null) {
                loader = new FXMLLoader(getClass().getResource("/gui/fxml/main.fxml"));
                this.register("primary", newStage);
                newStage.setTitle("Piiksuma");
            } else {
                loader = new FXMLLoader(getClass().getResource("/gui/fxml/login.fxml"));
                this.register("login", newStage);
                newStage.setTitle("Piiksuma - Login");
            }
            decorator = new JFXDecorator(newStage, loader.load(), false, false, true);
        } catch (IOException | PiikInvalidParameters e) {
            e.printStackTrace();
            return;
        }
        // TODO: Add a logo and a cool title to the JFXDecorator

        // Scene definition & binding to the Primary Stage
        Scene scene = new Scene(decorator, 450, 900);
        newStage.setScene(scene);
        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm(),
                getClass().getResource("/gui/css/main.css").toExternalForm()
        );

        // Show
        newStage.show();
    }

    public void invokeStage(String fxml, Object controller) throws PiikInvalidParameters {
        Matcher matcher = Pattern.compile("(\\w+)\\.\\w+$").matcher(fxml);
        if (!matcher.find()) {
            throw new PiikInvalidParameters("Invalid FXML name");
        }
        invokeStage(fxml, controller, matcher.group(1));
    }


    public void invokeStage(String fxml, Object controller, String title) throws PiikInvalidParameters {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        if (controller != null) {
            loader.setController(controller);
        }
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        JFXDecorator decorator;
        try {
            decorator = new JFXDecorator(stage, loader.load(), false, false, true);
        } catch (IOException e) {
            throw new PiikInvalidParameters("Couldn't load the provided FXML file: " + fxml);
        }
        Scene scene = new Scene(decorator);
        stage.setScene(scene);
        scene.getStylesheets().addAll(
                getClass().getResource("/gui/css/global.css").toExternalForm(),
                getClass().getResource("/gui/css/main.css").toExternalForm()
        );
        stage.show();
    }

    public ConversationController getConversationController() {
        return conversationController;
    }

    public void setConversationController(ConversationController conversationController) {
        this.conversationController = conversationController;
    }
}
