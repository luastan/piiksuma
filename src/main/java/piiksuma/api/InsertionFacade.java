package piiksuma.api;

import piiksuma.*;
import piiksuma.database.InsertionMapper;
import piiksuma.database.UpdateMapper;
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
    public void createUser(User newUser) throws PiikDatabaseException, PiikInvalidParameters {

        if (newUser == null || !newUser.checkNotNull()) {
            throw new PiikInvalidParameters("(newUser) null parameter");
        }

        parentFacade.getUserDao().createUser(newUser);
    }

    public void updateUser(User user, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {

        if (user == null || !user.checkPrimaryKey()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }

        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if(!user.equals(currentUser) && !currentUser.checkAdministrator()) {
            throw new PiikForbiddenException("forbidden");
        }

        parentFacade.getUserDao().updateUser(user);
    }

    public void createAchievement(Achievement achievement, User currentUser) throws PiikDatabaseException,
            PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (achievement == null || !achievement.checkNotNull()) {
            throw new PiikInvalidParameters("(achievement) null parameter");
        }

        if (!currentUser.getType().equals(UserType.administrator)) {
            throw new PiikForbiddenException("(user) You do not have permissions to do that");
        }

        parentFacade.getUserDao().createAchievement(achievement);
    }

    public void unlockAchievement(Achievement achievement, User user, User currentUser) throws PiikDatabaseException,
            PiikInvalidParameters {

        if (achievement == null || !achievement.checkNotNull()) {
            throw new PiikInvalidParameters("(achievement) null");
        }

        if(user == null || !user.checkNotNull()) {
            throw new PiikInvalidParameters("(user) null");
        }

        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if(!currentUser.checkAdministrator()) {
            throw new PiikForbiddenException("forbidden");
        }

        parentFacade.getUserDao().unlockAchievement(achievement, user);
    }

    /**
     * Function to follow a user
     *
     * @param followed User to be followed
     * @param follower User who follows
     * @throws PiikDatabaseException
     */
    public void followUser(User followed, User follower, User currentUser) throws PiikDatabaseException,
            PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (followed == null || !followed.checkNotNull()) {
            throw new PiikInvalidParameters("(followed) null parameter");
        }

        if (follower == null || !follower.checkNotNull()) {
            throw new PiikInvalidParameters("(follower) null parameter");
        }

        if (!follower.equals(currentUser)) {
            throw new PiikForbiddenException("(user) You do not have permissions to do that");
        }

        parentFacade.getUserDao().followUser(followed, follower);
    }


    /* MULTIMEDIA related methods */

    public void addMultimedia(Multimedia multimedia, User currentUser) throws PiikInvalidParameters,
            PiikDatabaseException {
        if (multimedia == null || !multimedia.checkNotNull()) {
            throw new PiikInvalidParameters("(multimedia) The parameter is null");
        }

        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
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

        if (post == null || !post.checkNotNull()) {
            throw new PiikInvalidParameters("(post) null parameter");
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

        if (hashtag == null || !hashtag.checkNotNull()) {
            throw new PiikInvalidParameters("(hashtag) null parameter");
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
    public void archivePost(Post post, User user, User currentUser) throws PiikDatabaseException, PiikInvalidParameters{
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (user == null || !user.checkNotNull()) {
            throw new PiikInvalidParameters("(user) The parameter is null");
        }

        if (post == null || !post.checkNotNull()) {
            throw new PiikInvalidParameters("(post) The parameter is null");
        }

        if (!user.equals(currentUser)) {
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
    public void updatePost(Post post, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (post == null || !post.checkNotNull()) {
            throw new PiikInvalidParameters("(post) The parameter is null");
        }

        if (!currentUser.checkAdministrator() && !post.getPostAuthor().equals(currentUser)) {
            throw new PiikForbiddenException("Permission denied");
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

        if (ticket == null || !ticket.checkNotNull()) {
            throw new PiikInvalidParameters("(ticket) null parameter");
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
    public void replyTicket(Ticket ticket, Message message, User currentUser) throws PiikDatabaseException,
            PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (ticket == null || !ticket.checkNotNull()) {
            throw new PiikInvalidParameters("(ticket) null parameter");
        }

        if (message == null || !message.checkNotNull()) {
            throw new PiikInvalidParameters("(message) null parameter");
        }
        parentFacade.getMessagesDao().replyTicket(ticket, message);
    }

    /**
     * The admin closes a ticket, marking it as "resolved"
     *
     * @param ticket the ticket which is going to be closed
     * @param currentUser user performing the action
     */

    public void closeTicket(Ticket ticket, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {

        if (ticket == null || !ticket.checkNotNull()) {
            throw new PiikInvalidParameters("(ticket) null");
        }

        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if(!currentUser.checkAdministrator()) {
            throw new PiikForbiddenException("you're not an admin");
        }

        parentFacade.getMessagesDao().closeTicket(ticket);
    }

    /**
     * This function creates a private message to another user in the app
     *
     * @param privateMessage message to be sent
     * @param currentUser    current user logged into the app
     */

    public void createMessage(Message privateMessage, User currentUser) throws PiikDatabaseException,
            PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (privateMessage == null || !privateMessage.checkNotNull()) {
            throw new PiikInvalidParameters("(privateMessage) null parameter");
        }

        if(!privateMessage.getSender().equals(currentUser)) {
            throw new PiikForbiddenException("forbidden");
        }

        parentFacade.getMessagesDao().createMessage(privateMessage);
    }

    /**
     * This function modifies a message already sent to another user
     *
     * @param oldMessage  old message
     * @param newMessage  new message
     * @param currentUser logged User
     * @throws PiikDatabaseException Thrown if null is encountered in currentUser, oldMessage, newMessage
     */
    public void modifyMessage(Message oldMessage, Message newMessage, User currentUser) throws PiikDatabaseException,
            PiikInvalidParameters {
        // Null check
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }
        if (oldMessage == null || !oldMessage.checkNotNull()) {
            throw new PiikInvalidParameters("(oldMessage) The parameter is null");
        }
        if (newMessage == null || !newMessage.checkNotNull()) {
            throw new PiikInvalidParameters("(newMessage) The parameter is null");
        }
        // Permision check
        if (!currentUser.getType().equals(UserType.administrator) || !currentUser.equals(oldMessage.getSender())) {
            throw new PiikForbiddenException("You do not have the required permissions");
        }
        parentFacade.getMessagesDao().modifyMessage(oldMessage, newMessage);
    }

    public void administratePersonalData(User user, User currentUser) throws PiikDatabaseException {

        if (user == null || !user.checkNotNull()) {
            throw new PiikDatabaseException("(user) The parameter is null");
        }


        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikDatabaseException("(user) The parameter is null");
        }

        parentFacade.getUserDao().administratePersonalData(user);
    }

    /**
     * Function to reply a post with other post
     *
     * @param reply response to the post father
     * @param currentUser current user logged into the app
     * @throws PiikDatabaseException
     */
    public void reply(Post reply, User currentUser) throws PiikDatabaseException {
        if(reply == null || !reply.checkNotNull()){
            throw new PiikDatabaseException("(reply) can't be null");
        }

        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikDatabaseException("(currentUser) can't be null");
        }

        if(!reply.getPostAuthor().equals(currentUser) && !currentUser.getType().equals(UserType.administrator)){
            throw new PiikForbiddenException("You do not have the required permissions");
        }

        if(reply.getFatherPost() == null || !reply.checkNotNull()){
            throw new PiikDatabaseException("The post father can not be null");
        }

        parentFacade.getPostDao().createPost(reply);
    }



    /* INTERACTION related methods */


    /**
     * Inserts a new notification on a user
     *
     * @param notification notification given to the user
     * @param currentUser  current user logged into the app
     */
    public void createNotification(Notification notification, User currentUser) throws PiikDatabaseException,
            PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (notification == null || !notification.checkNotNull()) {
            throw new PiikInvalidParameters("(notification) null parameter");
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

    public void notifyUser(Notification notification, User user, User currentUser) throws PiikDatabaseException,
            PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (notification == null || !notification.checkNotNull()) {
            throw new PiikInvalidParameters("(notification) null parameter");
        }

        if (user == null || !user.checkNotNull()) {
            throw new PiikInvalidParameters("(user) null parameter");
        }

        parentFacade.getInteractionDao().notifyUser(notification, user);
    }

    public void createEvent(Event event, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (event == null || !event.checkNotNull()) {
            throw new PiikInvalidParameters("(event) null parameter");
        }

        parentFacade.getInteractionDao().createEvent(event);
    }

    /**
     * Inserts into the database a given reaction
     *
     * @param reaction reaction to be inserted
     * @param current user executing the action
     */
    public void react(Reaction reaction, User current) throws PiikInvalidParameters, PiikDatabaseException {

        if (reaction == null || !reaction.checkNotNull()) {
            throw new PiikInvalidParameters("(reaction) null parameter");
        }

        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(current) The parameter is null");
        }

        if(!current.equals(reaction.getUser())) {
            throw new PiikForbiddenException("forbidden");
        }

        parentFacade.getInteractionDao().react(reaction);
    }

    public void updateEvent(Event event, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {

        if (event == null || !event.checkNotNull()) {
            throw new PiikDatabaseException("(event) Primary key constraints failed");
        }

        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if(!currentUser.equals(event.getCreator())) {
            throw new PiikForbiddenException("forbidden");
        }

        parentFacade.getInteractionDao().updateEvent(event);
    }
}
