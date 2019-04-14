package piiksuma.api;

import piiksuma.*;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikForbiddenException;
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
    public void createUser(User newUser, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }
        parentFacade.getUserDao().createUser(newUser);
    }


    public void createAchievement(Achievement achievement, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }
        if (!currentUser.getType().equals(UserType.administrator)){
            throw new PiikForbiddenException("(user) You do not have permissions to do that");
        }
        parentFacade.getUserDao().createAchievement(achievement);
    }

    /**
     * Function to follow a user
     *
     * @param followed User to be followed
     * @param follower User who follows
     * @throws PiikDatabaseException
     */
    public void followUser(User followed, User follower, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }
        if (!follower.equals(currentUser)){
            throw new PiikForbiddenException("(user) You do not have permissions to do that");
        }
        parentFacade.getUserDao().followUser(followed, follower);
    }


    /* MLTIMEDIA related methods */

    public void addMultimedia(Multimedia multimedia,User currentUser) throws PiikInvalidParameters,PiikDatabaseException{
        if (multimedia == null || !multimedia.checkNotNull()) {
            throw new PiikInvalidParameters("(multimedia) The parameter is null");
        }
        if (!multimedia.checkPrimaryKey()){
            throw new PiikDatabaseException("(multimedia) Primary key constraints failed");
        }
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }
        if (!currentUser.checkPrimaryKey()){
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }
        parentFacade.getMultimediaDao().addMultimedia(multimedia);
    }


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
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }
        parentFacade.getPostDao().createPost(post);
    }

    /**
     * Inserts a new Hashtag into the database
     *
     * @param hashtag     hashtag to insrt
     * @param currentUser current user logged
     * @return the hashtag created
     */
    public void createHashtag(Hashtag hashtag, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }
        parentFacade.getPostDao().createHashtag(hashtag);
    }

    /**
     * Lets a user follow a hashtag
     *
     * @param hashtag     hashtag to follow
     * @param currentUser user in the apliccation
     * @throws PiikDatabaseException Thrown if null is encountered in currentUser, hashtag
     */
    public void followHastag(Hashtag hashtag, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        // Null check
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }
        // Null check
        if (hashtag == null || !hashtag.checkNotNull()) {
            throw new PiikInvalidParameters("(hashtag) The parameter is null");
        }
        parentFacade.getPostDao().followHastag(hashtag, currentUser);
    }

    /**
     * Function to archive a post privately by an user
     *
     * @param post post to be archived
     * @throws PiikDatabaseException Duplicated keys and null values that shouldn't be
     */
    public void archivePost(Post post, User user, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (!currentUser.checkPrimaryKey()){
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }

        if (user == null || !user.checkNotNull()) {
            throw new PiikInvalidParameters("(user) The parameter is null");
        }

        if (!user.checkPrimaryKey()){
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }

        if (!user.equals(currentUser)){
            throw new PiikForbiddenException("(user) You do not have permissions to do that");
        }
        parentFacade.getPostDao().archivePost(post, user);
    }

    /**
     * Function to update the text content of the post
     *
     * @param post post to be updated
     * @throws PiikDatabaseException Duplicated keys and null values that shouldn't be
     */
    public void updatePost(Post post,User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (!currentUser.checkPrimaryKey()){
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }
        if (post == null || !post.checkNotNull()) {
            throw new PiikInvalidParameters("(post) The parameter is null");
        }

        if (!post.checkPrimaryKey()){
            throw new PiikDatabaseException("(post) Primary key constraints failed");
        }

        parentFacade.getPostDao().updatePost(post);
    }


    /* MESSAGE related methods */


    /**
     * A new ticket, created by a user, is inserted into the database
     *
     * @param ticket      ticket to insert
     * @param currentUser current user logged
     */
    public void newTicket(Ticket ticket, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }
        parentFacade.getMessagesDao().newTicket(ticket);
    }


    /**
     * An user replies a ticket by creating a message that will be associated to it; therefore, message.getTicket()
     * cannot be null
     *
     * @param ticket      the ticket is not necessary actually, we just have to check if the ticket is in the message
     * @param message     reply to be added to the ticket
     * @param currentUser current user logged into the app
     */
    public void replyTicket(Ticket ticket, Message message, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }
        parentFacade.getMessagesDao().replyTicket(ticket, message);
    }

    /**
     * This function sends a private message to another user in the app
     *
     * @param privateMessage message to be sent
     * @param currentUser    current user logged into the app
     */

    public void sendMessage(Message privateMessage, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }
        parentFacade.getMessagesDao().sendMessage(privateMessage);
    }

    /**
     * This function modifies a message already sent to another user
     *
     * @param oldMessage  old message
     * @param newMessage  new message
     * @param currentUser logged User
     * @throws PiikDatabaseException Thrown if null is encountered in currentUser, oldMessage, newMessage
     */
    public void modifyMessage(Message oldMessage, Message newMessage, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        // Null check
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }
        if (oldMessage == null || !oldMessage.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }
        if (newMessage == null || !newMessage.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }
        // Permision check
        if (!currentUser.getType().equals(UserType.administrator) || !currentUser.getEmail().equals(oldMessage.getSender())) {
            throw new PiikForbiddenException("You do not have the required permissions");
        }
        parentFacade.getMessagesDao().modifyMessage(oldMessage, newMessage);
    }

    public void administratePersonalData(User user, User currentUser) throws PiikDatabaseException {

        if (user == null || !user.checkNotNull()) {
            throw new PiikDatabaseException("(user) The parameter is null");
        }
        if (!currentUser.checkPrimaryKey()){
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikDatabaseException("(user) The parameter is null");
        }
        if (!currentUser.checkPrimaryKey()){
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }
        parentFacade.getUserDao().administratePersonalData(user);
    }



    /* INTERACTION related methods */


    /**
     * Inserts a new notification on a user
     *
     * @param notification notification given to the user
     * @param currentUser  current user logged into the app
     */
    public void createNotification(Notification notification, User currentUser) throws PiikDatabaseException, PiikForbiddenException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (!currentUser.getType().equals(UserType.administrator)) {
            throw new PiikForbiddenException("The user is not an administrator");
        }
        parentFacade.getInteractionDao().createNotification(notification);
    }


    /**
     * This function associates a notification with a user
     *
     * @param notification notification that we want the user to see
     * @param user         user that we want to notify
     * @param currentUser  current user logged into the app
     */

    public void notifyUser(Notification notification, User user, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }
        parentFacade.getInteractionDao().notifyUser(notification, user);
    }

    public void createEvent(Event event, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }
        parentFacade.getInteractionDao().createEvent(event);
    }


}
