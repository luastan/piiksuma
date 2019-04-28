package piiksuma.api;

import javafx.scene.image.Image;
import piiksuma.*;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikForbiddenException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFacade {

    private ApiFacade parentFacade;

    public SearchFacade(ApiFacade parentFacade) {
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
     * Function to gt the users that match with the specifications
     *
     * @param user    user that contains the requirements that will be applied in the search
     * @param limit   maximum of users to return
     * @param current current user
     * @return users that meet the given information
     */
    public List<User> searchUser(User user, User current, Integer limit) throws PiikInvalidParameters,
            PiikDatabaseException {

        if (user == null || !user.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (limit == null || limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }

        return parentFacade.getUserDao().searchUser(user, limit);
    }

    /**
     * Function to get the user that matches the given specifications
     *
     * @param user user that contains the requirements that will be applied in the search
     * @param typeTransaction nivel of isolation
     * @param current current user
     * @return user that meets the given information
     */
    public User getUser(User user, Integer typeTransaction, User current) throws PiikDatabaseException,
            PiikInvalidParameters {

        if (user == null || !user.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        try {
            if(!getConnection().getMetaData().supportsTransactionIsolationLevel(typeTransaction)){
                throw new PiikDatabaseException("Transaction does not support isolation level");
            }
        } catch (SQLException e) {
            throw new PiikDatabaseException("Transaction does not support isolation level");
        }

        return parentFacade.getUserDao().getUser(user);
    }

    /**
     * Function to get the user that matches the given specifications
     *
     * @param user    user that contains the requirements that will be applied in the search
     * @param current current user
     * @return user that meets the given information
     */
    public User getUser(User user, User current) throws PiikDatabaseException, PiikInvalidParameters {
        if (user == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if (current == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        User result = parentFacade.getUserDao().getUser(user);

        return result;
    }

    /**
     * Function to login into the app; it will try to find a user that meets the given keys
     *
     * @param user user whose primary fields will be used in the search
     * @return user from database that meets the required attributes
     */

    public User login(User user) throws PiikInvalidParameters, PiikDatabaseException {
        if (user == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        return parentFacade.getUserDao().login(user);
    }

    /**
     * Function that returns the statistics of the given user
     *
     * @param user    user about whose statistics we want to know
     * @param current current user
     * @return computed statistics
     * @throws SQLException
     */

    public Statistics getUserStatistics(User user, User current) throws PiikInvalidParameters, PiikDatabaseException {
        if (user == null || !user.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (!current.checkAdministrator() && !user.equals(current)) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
        }

        return parentFacade.getUserDao().getUserStatistics(user);
    }

    public List<Achievement> getAchievements(User user, User current) throws PiikInvalidParameters,
            PiikDatabaseException {
        if (user == null || !user.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (!current.checkAdministrator() && !user.equals(current)) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
        }

        return parentFacade.getUserDao().getAchievements(user);
    }

    public Map<String, Timestamp> getUnlockDates(User user, User current) throws PiikInvalidParameters, PiikDatabaseException {

        if (user == null || !user.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (!current.checkAdministrator() && !user.equals(current)) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
        }

        return parentFacade.getUserDao().getUnlockDates(user);
    }

    /* MULTIMEDIA related methods */

    public Multimedia getMultimedia(Multimedia multimedia, User current) throws PiikInvalidParameters, PiikDatabaseException {
        if (multimedia == null || !multimedia.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("multimedia"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        return parentFacade.getMultimediaDao().getMultimedia(multimedia);
    }

    public boolean existsMultimedia(Multimedia multimedia, User current) throws PiikInvalidParameters, PiikDatabaseException {
        if (multimedia == null || !multimedia.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("multimedia"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        return parentFacade.getMultimediaDao().existsMultimedia(multimedia);
    }

    public Long numPostMultimedia(Multimedia multimedia, User current) throws PiikInvalidParameters, PiikDatabaseException {
        if (multimedia == null || !multimedia.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("multimedia"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        return parentFacade.getMultimediaDao().numPostMultimedia(multimedia);
    }

    public List<Post> postWithMultimedia(Multimedia multimedia, User current) throws PiikInvalidParameters, PiikDatabaseException {
        if (multimedia == null || !multimedia.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("multimedia"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        return parentFacade.getMultimediaDao().postWithMultimedia(multimedia);
    }

    /* POST related methods */

    public Post getPost(Post post, User current) throws PiikDatabaseException, PiikInvalidParameters {
        if (post == null || !post.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("post"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (!current.checkAdministrator() && !post.getPostAuthor().equals(current)) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
        }

        return parentFacade.getPostDao().getPost(post);
    }

    // TODO we need to set a limit
    public List<Post> getPost(Hashtag hashtag, User current) throws PiikDatabaseException, PiikInvalidParameters {
        if (hashtag == null || !hashtag.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("hashtag"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        return parentFacade.getPostDao().getPost(hashtag);
    }

    // TODO we need to set a limit
    public List<Post> getPost(User user, User current) throws PiikDatabaseException, PiikInvalidParameters {
        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (user == null || !user.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if (!current.checkAdministrator() && !user.equals(current)) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
        }

        return parentFacade.getPostDao().getPost(user);
    }

    /**
     * Function to get the hashtag that matches the given specifications
     *
     * @param hashtag hashtag whose properties will be used in the search
     * @param current current user
     * @return hashtag that matches the given information
     */
    public Hashtag getHashtag(Hashtag hashtag, User current) throws PiikDatabaseException, PiikInvalidParameters {

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (hashtag == null || !hashtag.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("hashtag"));
        }

        return parentFacade.getPostDao().getHashtag(hashtag);
    }

    /**
     * Function to get the hashtags that match with the specifications
     *
     * @param hashtag hashtag whose properties will be used in the search
     * @param limit   maximum number of tickets to retrieve
     * @param current current user
     * @return hashtags that match the given information
     */
    public List<Hashtag> searchHashtag(Hashtag hashtag, Integer limit, User current) throws PiikInvalidParameters,
            PiikDatabaseException {
        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (hashtag == null || !hashtag.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("hashtag"));
        }

        if (limit == null || limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }
        return parentFacade.getPostDao().searchHashtag(hashtag, limit);
    }

    /**
     * Function to search the posts which contain a given text, ordered by descending publication date
     *
     * @param text    text to be searched
     * @param limit   maximum number of tickets to retrieve
     * @param current current user
     * @return list of the posts which contain the given text
     */
    public List<Post> searchByText(String text, Integer limit, User current) throws PiikInvalidParameters, PiikDatabaseException {
        if (text == null || text.isEmpty()) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("text"));
        }

        if (limit == null || limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        return parentFacade.getPostDao().searchByText(text, limit);
    }

    /**
     * Function that composes a user's feed; the following posts are included:
     * - Posts made by followed users
     * - Posts made by the user
     * - The 20 most reacted to posts that are in the user's followed hashtags
     *
     * @param user    user whose feed will be retrieved
     * @param limit   limit of the posts
     * @param current current user
     * @return posts that make up the user's feed
     */
    public List<Post> getFeed(User user, Integer limit, User current) throws PiikDatabaseException,
            PiikInvalidParameters {

        if (user == null || !user.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (limit == null || limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }

        if (user.equals(current) || current.getType().equals(UserType.administrator)) {
            return parentFacade.getPostDao().getFeed(user, limit);
        } else {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
        }
    }

    /**
     * Retrieves the posts that a user has archived
     *
     * @param user    user whose archived posts will be retrieved
     * @param current current user
     * @return found posts
     */
    public List<Post> getArchivedPosts(User user, User current) throws PiikDatabaseException, PiikInvalidParameters {

        if (user == null || !user.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        // A user's archived posts can be retrieved by an user or by an admin
        if (!current.getType().equals(UserType.administrator) && !current.equals(user)) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
        }

        return parentFacade.getPostDao().getArchivedPosts(user);
    }

    public List<Hashtag> getTrendingTopics(Integer limit, User current) throws PiikDatabaseException, PiikInvalidParameters {
        if (limit == null || limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }
        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        return parentFacade.getPostDao().getTrendingTopics(limit);
    }

    /* MESSAGE related methods */

    /**
     * This function allows the admins to retrieve the current unresolved tickets
     *
     * @param limit   maximum number of tickets to retrieve
     * @param current current user
     * @return the list of all the tickets which haven't been closed
     */
    public List<Ticket> getAdminTickets(Integer limit, User current) throws PiikInvalidParameters, PiikDatabaseException {
        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }
        
        if (limit == null || limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }

        if (!current.checkAdministrator()) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
        }
        
        return parentFacade.getMessagesDao().getAdminTickets(limit);
    }

    /**
     * Allows the user to read his messages
     *
     * @param user        user whose messages will be retrieved
     * @param currentUser current user in the application
     * @return Messages for the user
     * @throws PiikDatabaseException Thrown if null is encountered in currentUser
     */
    public List<Message> readMessages(User user, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        // Null check
        if (currentUser == null || !currentUser.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (user == null || !user.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        return parentFacade.getMessagesDao().readMessages(user);
    }


    /* INTERACTION related methods */

    /**
     * Gets the number of reactions that a post has, classified by type
     *
     * @param post post whose reactions will be counted
     * @param current current user logged into the app
     * @return number of reactions classified by type
     */
    public HashMap<ReactionType, Long> getPostReactionCount(Post post, User current) throws PiikDatabaseException, PiikInvalidParameters {
        if (post == null || post.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("post"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("current"));
        }

        return parentFacade.getInteractionDao().getPostReactionsCount(post);
    }

    /**
     * Retrieves the notifications that a user has received
     *
     * @param user    user whose notifications will be retrieved
     * @param current current user
     * @return found notifications
     */
    public List<Notification> getNotifications(User user, User current) throws PiikDatabaseException, PiikInvalidParameters {

        if (user == null || !user.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        // A user's notifications can be retrieved by the user or by an admin
        if (!current.checkAdministrator() && !current.equals(user)) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
        }

        return parentFacade.getInteractionDao().getNotifications(user);
    }

    /**
     * Function to get the image of the multimedia
     *
     * @param multimedia Multimedia that contains the information of the image
     * @return the image of the multimedia
     * @throws PiikDatabaseException
     */
    public Image getImage(Multimedia multimedia, User current) throws PiikInvalidParameters, PiikDatabaseException {
        if(multimedia == null){
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("multimedia"));
        }

        return parentFacade.getMultimediaDao().getImage(multimedia);
    }

    /**
     * Function to get the events
     *
     * @param user user
     * @param current   current user logged
     * @return  list of the events that the followed users created
     */
    public List<Event> getEvents(User user, User current, Integer limit) throws PiikDatabaseException, PiikInvalidParameters{
        if (user == null || !user.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }
        
        if (limit == null || limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }
        return parentFacade.getInteractionDao().getEvents(user, current, limit);
    }
}
