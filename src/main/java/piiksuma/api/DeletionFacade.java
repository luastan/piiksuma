package piiksuma.api;

import piiksuma.*;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikForbiddenException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.sql.Connection;
import java.sql.SQLException;

public class DeletionFacade {
    private ApiFacade parentFacade;

    public DeletionFacade(ApiFacade parentFacade) {
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
     * Function to remove a user from the database
     *
     * @param user        User to be deleted
     * @param currentUser Current user logged into the app
     * @throws PiikForbiddenException The currentUser does not have permissions to delete the user
     * @throws PiikDatabaseException  The user has null values or non unique values on primary keys
     * @throws PiikInvalidParameters  The currentUser is null
     */
    public void removeUser(User user, User currentUser) throws PiikForbiddenException, PiikDatabaseException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (user == null || !user.checkNotNull()) {
            throw new PiikInvalidParameters("(user) The parameter is null");
        }

        if (!currentUser.checkAdministrator() && !currentUser.getEmail().equals(user.getEmail())) {
            throw new PiikForbiddenException("(user) You do not have permissions to do that");
        }

        parentFacade.getUserDao().removeUser(user);
    }

    /**
     * Function to unfollow the user followed by the user follower, we have to delete it from the database
     *
     * @param followed    User to be unfollowed
     * @param follower    User that unfollows
     * @param currentUser Current user logged into the app
     * @throws PiikDatabaseException The followed user or the follower user have null values or non unique values on primary keys
     * @throws PiikInvalidParameters The currentUser is null
     */
    public void unfollowUser(User followed, User follower, User currentUser) throws PiikDatabaseException, PiikForbiddenException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (followed == null || !followed.checkNotNull()) {
            throw new PiikInvalidParameters("(followedUser) The parameter is null");
        }

        if (follower == null || follower.checkNotNull()) {
            throw new PiikInvalidParameters("(followedUser) The parameter is null");
        }

        // The current user has to be the follower user
        if (!currentUser.getEmail().equals(follower.getEmail())) {
            throw new PiikForbiddenException("(user) You do not have permissions to do that");
        }
        parentFacade.getUserDao().unfollowUser(followed, follower);
    }

    /* MULTIMEDIA related methods */

    /**
     * Function to remove multimedia from the database
     *
     * @param multimedia  Multimedia to be deleted
     * @param currentUser Current user logged into the app
     * @throws PiikDatabaseException  The user has null values or non unique values on primary keys
     * @throws PiikForbiddenException The currentUser is not the author of the repost or he is not an admin
     * @throws PiikInvalidParameters  The currentUser is null
     */
    public void removeMultimedia(Multimedia multimedia, User currentUser) throws PiikDatabaseException, PiikForbiddenException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (multimedia == null || multimedia.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (!currentUser.checkAdministrator()) {
            throw new PiikForbiddenException("(currentUser) You need to be an admin to delete media from the database");
        }

        parentFacade.getMultimediaDao().removeMultimedia(multimedia);
    }

    /* POST related methods */

    /**
     * Function to remove a post from the database
     *
     * @param post        Post to be deleted
     * @param currentUser Current user logged into the app
     * @throws PiikDatabaseException  The user has null values or non unique values on primary keys
     * @throws PiikForbiddenException The currentUser is not the author of the repost or he is not an admin
     * @throws PiikInvalidParameters  The currentUser is null
     */
    public void removePost(Post post, User currentUser) throws PiikDatabaseException, PiikForbiddenException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (post == null || !post.checkNotNull()) {
            throw new PiikInvalidParameters("(post) The parameter is null");
        }

        if (!currentUser.checkAdministrator() && !currentUser.getEmail().equals(post.getPostAuthor())) {
            throw new PiikForbiddenException("(user) You do not have permissions to do that");
        }

        parentFacade.getPostDao().removePost(post);
    }

    /**
     * Function to remove a repost from the database
     *
     * @param repost      Repost to be deleted
     * @param currentUser Current user logged into the app
     * @throws PiikDatabaseException  The user has null values or non unique values on primary keys
     * @throws PiikForbiddenException The currentUser is not the author of the repost or he is not an admin
     * @throws PiikInvalidParameters  The currentUser is null
     */
    public void removeRePost(Post repost, User currentUser) throws PiikDatabaseException, PiikForbiddenException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (repost == null || !repost.checkNotNull()) {
            throw new PiikInvalidParameters("(repost) The parameter is null");
        }

        if (!currentUser.checkAdministrator() && !currentUser.getEmail().equals(repost.getPostAuthor())) {
            throw new PiikForbiddenException("(user) You do not have permissions to do that");
        }

        parentFacade.getPostDao().removeRepost(repost);
    }

    /* MESSAGE related methods */

    /**
     * Function to remove a message from the database
     *
     * @param message     Message to be deleted
     * @param currentUser Current user logged into the app
     * @throws PiikDatabaseException  The user has null values or non unique values on primary keys
     * @throws PiikForbiddenException The currentUser is not the author of the repost or he is not an admin
     * @throws PiikInvalidParameters  The currentUser is null
     */
    public void deleteMessage(Message message, User currentUser) throws PiikDatabaseException, PiikForbiddenException, PiikInvalidParameters {
        // Null check
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("Parameter 'currentUser' can not be null");
        }

        if (message == null || !message.checkNotNull()) {
            throw new PiikInvalidParameters("Parameter 'message' can not be null");
        }

        // Permission check
        if (!currentUser.checkAdministrator() && !currentUser.getEmail().equals(message.getSender())) {
            throw new PiikForbiddenException("You do not have the required permissions");
        }

        parentFacade.getMessagesDao().deleteMessage(message);
    }

    /* INTERACTION related methods */

    /**
     * Function to remove a event from the database
     *
     * @param e           Event to be deleted
     * @param currentUser Current user logged into the app
     * @throws PiikDatabaseException  The user has null values or non unique values on primary keys
     * @throws PiikForbiddenException The currentUser is not the author of the repost or he is not an admin
     * @throws PiikInvalidParameters  The currentUser is null
     */
    public void removeEvent(Event e, User currentUser) throws PiikDatabaseException, PiikForbiddenException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (e == null || e.checkNotNull()) {
            throw new PiikInvalidParameters("(event) The parameter is null");
        }

        if (!currentUser.checkAdministrator() && !currentUser.getEmail().equals(e.getCreator())) {
            throw new PiikForbiddenException("(user) You do not have permissions to do that");
        }

        parentFacade.getInteractionDao().removeEvent(e);
    }

    /**
     * Function to remove a reaction from the database
     *
     * @param reaction    Reaction to be deleted
     * @param currentUser Current user logged into the app
     * @throws PiikDatabaseException  The user has null values or non unique values on primary keys
     * @throws PiikForbiddenException The currentUser is not the author of the reaction
     * @throws PiikInvalidParameters  The currentUser is null
     */
    public void removeReaction(Reaction reaction, User currentUser) throws PiikDatabaseException, PiikForbiddenException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) The parameter is null");
        }

        if (reaction == null || !reaction.checkNotNull()) {
            throw new PiikInvalidParameters("(reaction) The parameter is null");
        }

        // We only allow the user who created the reaction to delete or an admin
        if (!currentUser.checkAdministrator() && !currentUser.getEmail().equals(reaction.getUser())) {
            throw new PiikForbiddenException("(user) You do not have permissions to do that");
        }
        parentFacade.getInteractionDao().removeReaction(reaction);
    }

}
