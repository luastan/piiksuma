package piiksuma.api;

import piiksuma.*;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.sql.Connection;

public class InsertionFacade {
    private ApiFacade parentFacade;

    public InsertionFacade(ApiFacade parentFacade) {
        this.parentFacade = parentFacade;
    }

    // Getters and Setters are used in Test methods
    public Connection getConnection() {
        return parentFacade.getConnection();
    }

    public void setConnection(Connection connection) {
        parentFacade.setConnection(connection);
    }

    /* USER related methods */


    /**
     * Adds a new user to the database
     *
     * @param newUser User to be inserted into the database
     * @throws PiikDatabaseException User already exists or it has invalid parameters such as null values or non unique
     *                               values on primary keys
     */
    public void createUser(User newUser) throws PiikDatabaseException {
            parentFacade.getUserDao().createUser(newUser);
    }


    public void createAchievement(Achievement achievement) {
        parentFacade.getUserDao().createAchievement(achievement);
    }

    public void followUser(User followed, User follower) {
        parentFacade.getUserDao().followUser(followed,follower);
    }


    /* MLTIMEDIA related methods */



    /* POST related methods */


    /**
     * Inserts a new Post into the database
     *
     * @param post        New Post
     * @param currentUser Current user using the application
     * @throws PiikDatabaseException Duplicated keys and null values that shouldn't be
     * @throws PiikInvalidParameters Given post or currentUser are invalid
     */
    public void createPost(Post post, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        parentFacade.getPostDao().createPost(post,currentUser);
    }

    /**
     * Inserts a new Hashtag into the database
     * @param hashtag hashtag to insrt
     * @param current current user logged
     * @return the hashtag created
     */
    public Hashtag createHashtag(Hashtag hashtag, User current){
        return parentFacade.getPostDao().createHashtag(hashtag,current);
    }


    /* MESSAGE related methods */


    /**
     * A new ticket, created by a user, is inserted into the database
     *
     * @param ticket      ticket to insert
     * @param currentUser current user logged
     */
    public void newTicket(Ticket ticket, User currentUser){
        parentFacade.getMessagesDao().newTicket(ticket,currentUser);
    }


    /**
     * An user replies a ticket by creating a message that will be associated to it; therefore, message.getTicket()
     * cannot be null
     *
     * @param ticket      the ticket is not necessary actually, we just have to check if the ticket is in the message
     * @param message     reply to be added to the ticket
     * @param currentUser current user logged into the app
     */
    public void replyTicket(Ticket ticket, Message message, User currentUser){
        parentFacade.getMessagesDao().replyTicket(ticket,message,currentUser);
    }

    /**
     * This function sends a private message to another user in the app
     *
     * @param privateMessage message to be sent
     * @param currentUser current user logged into the app
     */

    public void sendMessage(Message privateMessage, User currentUser) {
        parentFacade.getMessagesDao().sendMessage(privateMessage,currentUser);
    }


    /* INTERACTION related methods */


    /**
     * Inserts a new notification on a user
     *
     * @param notification notification given to the user
     * @param currentUser  current user logged into the app
     */
    public void createNotification(Notification notification, User currentUser){
        parentFacade.getInteractionDao().createNotification(notification,currentUser);
    }



    /**
     * This function associates a notification with a user
     *
     * @param notification notification that we want the user to see
     * @param user         user that we want to notify
     * @param currentUser  current user logged into the app
     */

    public void notifyUser(Notification notification, User user, User currentUser) throws PiikDatabaseException {
        parentFacade.getInteractionDao().notifyUser(notification,user,currentUser);
    }

    public Event createEvent(Event event, User current) {
        return parentFacade.getInteractionDao().createEvent(event,current);
    }


}
