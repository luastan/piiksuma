package piiksuma.gui;

import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.Window;
import piiksuma.User;
import piiksuma.exceptions.PiikInvalidParameters;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

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
}
