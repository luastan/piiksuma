package piiksuma.api;

import piiksuma.*;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikForbiddenException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.sql.Connection;

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
    public void removeUser(User user, User currentUser) throws PiikForbiddenException, PiikDatabaseException,
            PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (user == null || !user.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if (!currentUser.checkAdministrator() && !currentUser.equals(user)) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
        }

        parentFacade.getUserDao().removeUser(user);
    }

    /**
     * Un-silence the user
     *
     * @param user        User to be un-silenced
     * @param currentUser Current user logged into the app
     * @throws PiikDatabaseException Thrown if the currentUser does not have permissions to un-silenced the user
     * @throws PiikInvalidParameters Thrown if currentUser/user or its primary keys are null
     */

    public void unsileceUser(User user, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (user == null || !user.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        parentFacade.getUserDao().unsilenceUser(user, currentUser);
    }

    /**
     * Function to unfollow the user followed by the user follower, we have to delete it from the database
     *
     * @param followed    User to be unfollowed
     * @param follower    User that unfollows
     * @param currentUser Current user logged into the app
     * @throws PiikDatabaseException The followed user or the follower user have null values or non unique values on
     *                               primary keys
     * @throws PiikInvalidParameters The currentUser is null
     */
    public void unfollowUser(User followed, User follower, User currentUser) throws PiikDatabaseException,
            PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (followed == null || !followed.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("followed"));
        }

        if (follower == null || !follower.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("follower"));
        }

        // The current user has to be the follower user
        if (!currentUser.equals(follower)) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
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
    public void removeMultimedia(Multimedia multimedia, User currentUser) throws PiikDatabaseException,
            PiikForbiddenException, PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (multimedia == null || multimedia.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("multimedia"));
        }

        if (!currentUser.checkAdministrator()) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
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
        if (currentUser == null || !currentUser.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (post == null || !post.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("post"));
        }

        if (!currentUser.checkAdministrator() && !currentUser.equals(post.getPostAuthor())) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
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
    public void removeRePost(Post repost, User currentUser) throws PiikDatabaseException, PiikForbiddenException,
            PiikInvalidParameters {
        if (currentUser == null || !currentUser.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (repost == null || !repost.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("repost"));
        }

        if (!currentUser.checkAdministrator() && !currentUser.equals(repost.getPostAuthor())) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
        }

        parentFacade.getPostDao().removeRepost(repost);
    }

    /**
     * Unblock User previously blocked
     * @param blockedUser User that is blocked
     * @param user User that blocked him
     * @param currentUser Current user logged into the app
     * @throws PiikDatabaseException
     * @throws PiikInvalidParameters
     */
    public void unblockUser(User blockedUser, User user, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {

        if (currentUser == null || !currentUser.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (blockedUser == null || !blockedUser.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("blockedUser"));
        }
        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        parentFacade.getUserDao().unblockUser(blockedUser, user);
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
        if (currentUser == null || !currentUser.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (message == null || !message.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("message"));
        }

        // Permission check
        if (!currentUser.checkAdministrator() && !currentUser.equals(message.getSender())) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
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
        if (currentUser == null || !currentUser.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (e == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("event"));
        }

        if (!currentUser.checkAdministrator() && !currentUser.equals(e.getCreator())) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
        }

        parentFacade.getInteractionDao().removeEvent(e);
    }

    /**
     * Delete a user from an event
     * @param event
     * @param user
     * @throws PiikDatabaseException
     */
    public void deleteUserInEvent(Event event, User user, User current) throws PiikDatabaseException, PiikInvalidParameters {

        if(user == null){
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if(event == null){
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("event"));
        }

        if (!current.checkAdministrator() && !current.equals(user)) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
        }

        parentFacade.getInteractionDao().deleteUserInEvent(event, user);
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
        if (currentUser == null || !currentUser.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (reaction == null || !reaction.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("reaction"));
        }

        // We only allow the user who created the reaction to delete or an admin
        if (!currentUser.checkAdministrator() && !currentUser.equals(reaction.getUser())) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
        }
        parentFacade.getInteractionDao().removeReaction(reaction);
    }

    public void removeArchivedPost(Post post, User user, User current) throws PiikDatabaseException, PiikInvalidParameters{
        if (current == null ) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (post == null ) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("post"));
        }

        parentFacade.getPostDao().removeArchivePost(post, user);
    }
}
