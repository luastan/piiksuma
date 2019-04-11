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
    public void unfollowUser(User followed, User follower, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        parentFacade.getUserDao().unfollowUser(followed, follower);
    }

    /* MLTIMEDIA related methods */

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
        parentFacade.getPostDao().removePost(post, currentUser);//TODO quitar el current user del dao
    }

    /**
     * Function to remove a repost from the database
     *
     * @param repost      Repost to be deleted
     * @param currentUser Current user logged into the app
     * @throws PiikDatabaseException  The user has null values or non unique values on primary keys
     * @throws PiikForbiddenException The currentUser is not the author of the repost or he is not an admin
     * @throws SQLException
     */
    public void removeRePost(Post repost, User currentUser) throws PiikDatabaseException, PiikForbiddenException, SQLException {
        parentFacade.getPostDao().removeRepost(repost, currentUser);//TODO quitar el current user del dao
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
        parentFacade.getInteractionDao().removeEvent(e);
    }

    /**
     * Function to remove a reaction from the database
     *
     * @param reaction    Reaction to be deleted
     * @param currentUser Current user logged into the app
     * @throws PiikDatabaseException  The user has null values or non unique values on primary keys
     * @throws PiikForbiddenException The curretUser is not the author of the reaction
     * @throws PiikInvalidParameters  The currentUser is null
     */
    public void removeReaction(Reaction reaction, User currentUser) throws PiikDatabaseException, PiikForbiddenException, PiikInvalidParameters {
        parentFacade.getInteractionDao().removeReaction(reaction);
    }

}
