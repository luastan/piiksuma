package piiksuma.api;

import piiksuma.*;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikForbiddenException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

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

        if (user == null || !user.checkNotNull()) {
            throw new PiikInvalidParameters("(user) can't be null");
        }
        if (!user.checkPrimaryKey()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(current) can't be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(current) Primary key constraints failed");
        }
        if (limit == null || limit <= 0) {
            throw new PiikInvalidParameters("(limit) can't be null");
        }
        return parentFacade.getUserDao().searchUser(user, limit);
    }

    /**
     * Function to get the user that matches the given specifications
     *
     * @param user    user that contains the requirements that will be applied in the search
     * @param current current user
     * @return user that meets the given information
     */
    public User getUser(User user, User current) throws PiikDatabaseException, PiikInvalidParameters {
        if (user == null || !user.checkNotNull()) {
            throw new PiikInvalidParameters("(user) Parameter can not be null");
        }
        if (!user.checkPrimaryKey()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(current) Parameter can not  be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(current) Primary key constraints failed");
        }
        return parentFacade.getUserDao().getUser(user);
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
        if (user == null || !user.checkNotNull()) {
            throw new PiikInvalidParameters("(user) Parameter can not be null");
        }
        if (!user.checkPrimaryKey()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) Parameter can not  be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }
        return parentFacade.getUserDao().getUserStatistics(user);
    }

    public List<Achievement> getAchievement(User user, User current) throws PiikInvalidParameters,
            PiikDatabaseException {
        if (user == null || !user.checkNotNull()) {
            throw new PiikInvalidParameters("(user) Parameter can not be null");
        }
        if (!user.checkPrimaryKey()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(current) Parameter is null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(current) Primary key constraints failed");
        }
        return parentFacade.getUserDao().getAchievement(user);
    }

    /* MULTIMEDIA related methods */

    public Multimedia existsMultimedia(Multimedia multimedia, User current) throws PiikInvalidParameters, PiikDatabaseException {
        if (multimedia == null || !multimedia.checkNotNull()) {
            throw new PiikInvalidParameters("(multimedia) Parameter can not be null");
        }
        if (multimedia.checkPrimaryKey()) {
            throw new PiikDatabaseException("(multimedia) Primary key constraints failed");
        }
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(current) Parameter can not  be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(current) Primary key constraints failed");
        }
        return parentFacade.getMultimediaDao().existsMultimedia(multimedia);
    }

    public Integer numPostMultimedia(Multimedia multimedia, User current) throws PiikInvalidParameters, PiikDatabaseException {
        if (multimedia == null || !multimedia.checkNotNull()) {
            throw new PiikInvalidParameters("(multimedia) Parameter is null");
        }
        if (multimedia.checkPrimaryKey()) {
            throw new PiikDatabaseException("(multimedia) Primary key constraints failed");
        }
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(current) Parameter can't  be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(current) Primary key constraints failed");
        }
        return parentFacade.getMultimediaDao().numPostMultimedia(multimedia);
    }

    public List<Post> postWithMultimedia(Multimedia multimedia, User current) throws PiikInvalidParameters, PiikDatabaseException {
        if (multimedia == null || !multimedia.checkNotNull()) {
            throw new PiikInvalidParameters("(multimedia) Parameter can not be null");
        }
        if (multimedia.checkPrimaryKey()) {
            throw new PiikDatabaseException("(multimedia) Primary key constraints failed");
        }
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) Parameter can not  be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }
        return parentFacade.getMultimediaDao().postWithMultimedia(multimedia);
    }

    /* POST related methods */

    public Post getPost(Post post, User current) throws PiikDatabaseException, PiikInvalidParameters {
        if (post == null || !post.checkNotNull()) {
            throw new PiikInvalidParameters("(post) Parameter can not be null");
        }
        if (!post.checkPrimaryKey()) {
            throw new PiikDatabaseException("(post) Primary key constraints failed");
        }
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) Parameter can not  be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }
        return parentFacade.getPostDao().getPost(post);
    }

    public List<Post> getPost(Hashtag hashtag, User current) throws PiikDatabaseException, PiikInvalidParameters {
        if (hashtag == null || !hashtag.checkNotNull()) {
            throw new PiikInvalidParameters("(hashtag) Parameter can not be null");
        }
        if (!hashtag.checkPrimaryKey()) {
            throw new PiikDatabaseException("(hashtag) Primary key constraints failed");
        }
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) Parameter can not  be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }
        return parentFacade.getPostDao().getPost(hashtag);
    }

    public List<Post> getPost(User user, User current) throws PiikDatabaseException, PiikInvalidParameters {
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) Parameter can not  be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }
        if (user == null || !user.checkNotNull()) {
            throw new PiikInvalidParameters("(user) Parameter can not  be null");
        }
        if (!user.checkPrimaryKey()) {
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }
        return parentFacade.getPostDao().getPost(user);
    }

    /**
     * Function to get the hashtag that matchs with the given specifications
     *
     * @param hashtag hashtag whose properties will be used in the search
     * @param current current user
     * @return hashtag that matches the given information
     */
    public Hashtag getHashtag(Hashtag hashtag, User current) throws PiikDatabaseException, PiikInvalidParameters {

        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) Parameter can not  be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }
        if (hashtag == null || !hashtag.checkNotNull()) {
            throw new PiikInvalidParameters("(hashtag) Parameter can not be null");
        }
        if (!hashtag.checkPrimaryKey()) {
            throw new PiikDatabaseException("(hashtag) Primary key constraints failed");
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
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) Parameter can not  be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }
        if (limit == null || limit <= 0) {
            throw new PiikInvalidParameters("(limit) Parameter can not  be null or less than zero");
        }
        if (hashtag == null || !hashtag.checkNotNull()) {
            throw new PiikInvalidParameters("(hashtag) Parameter can not be null");
        }
        if (!hashtag.checkPrimaryKey()) {
            throw new PiikDatabaseException("(hashtag) Primary key constraints failed");
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
            throw new PiikInvalidParameters("(text) Parameter can not  be null");
        }
        if (limit == null || limit <= 0) {
            throw new PiikInvalidParameters("(limit) Parameter can not  be null or less than zero");
        }
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) Parameter can not  be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
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

        if (user == null || !user.checkNotNull()) {
            throw new PiikInvalidParameters("(user) can not be null");
        }
        if (!user.checkPrimaryKey()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(current) Parameter can not be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }
        if (limit == null || limit <= 0) {
            throw new PiikInvalidParameters("(limit) must be greater than 0");
        }

        if (user.equals(current) || current.getType().equals(UserType.administrator)) {
            return parentFacade.getPostDao().getFeed(user, limit);
        } else {
            throw new PiikForbiddenException("The current user doesn't match the given user");
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

        if (user == null || !user.checkNotNull()) {
            throw new PiikInvalidParameters("(user) can not be null");
        }
        if (!user.checkPrimaryKey()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) Parameter can not be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }
        // A user's archived posts can be retrieved by an user or by an admin
        if (!current.getType().equals(UserType.administrator) && !current.equals(user)) {
            throw new PiikForbiddenException("The current user isn't allowed to retrieved the queried archived posts");
        }

        return parentFacade.getPostDao().getArchivedPosts(user);
    }

    public List<Hashtag> getTrendingTopics(Integer limit, User current) throws PiikDatabaseException, PiikInvalidParameters {
        if (limit == null || limit <= 0) {
            throw new PiikInvalidParameters("(limit) must be greater than 0");
        }
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) Parameter can not be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
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
    public List<Ticket> getAdminTickets(Integer limit, User current) throws PiikInvalidParameters, PiikDatabaseException, PiikForbiddenException {
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(currentUser) Parameter can not be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }
        if (!current.checkAdministrator()) {
            throw new PiikForbiddenException("The current user isn't an administrator");
        }
        if (limit == null || limit <= 0) {
            throw new PiikInvalidParameters("(limit) must be greater than 0");
        }
        return parentFacade.getMessagesDao().getAdminTickets(limit);
    }

    /**
     * Allows the user to read his messages
     *
     * @param currentUser current user in the aplication
     * @return Messages for the user
     * @throws PiikDatabaseException Thrown if null is encountered in currentUser
     */
    public List<Message> readMessages(User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        // Null check
        if (currentUser == null || !currentUser.checkNotNull()) {
            throw new PiikInvalidParameters("Parameter 'currentUser' can not be null");
        }
        if (!currentUser.checkPrimaryKey()) {
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }
        return parentFacade.getMessagesDao().readMessages(currentUser);
    }


    /* INTERACTION related methods */

    public HashMap<ReactionType, Integer> getPostReaction(Post post, User current) throws PiikDatabaseException, PiikInvalidParameters {
        if (post == null || post.checkNotNull()) {
            throw new PiikInvalidParameters("Parameter 'post' can not be null");
        }
        if(post.checkPrimaryKey()){
            throw new PiikDatabaseException("(post) Primary key constraints failed");
        }
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("Parameter 'currentUser' can not be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }
        return parentFacade.getInteractionDao().getPostReaction(post);
    }

    /**
     * Retrieves the notifications that a user has received
     *
     * @param user    user whose notifications will be retrieved
     * @param current current user
     * @return found notifications
     */
    public List<Notification> getNotifications(User user, User current) throws PiikDatabaseException, PiikInvalidParameters {

        if (user == null || !user.checkNotNull()) {
            throw new PiikInvalidParameters("(user) can not be null");
        }
        if (!user.checkPrimaryKey()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }
        if (current == null || !current.checkNotNull()) {
            throw new PiikInvalidParameters("(current) can not be null");
        }
        if (!current.checkPrimaryKey()) {
            throw new PiikDatabaseException("(currentUser) Primary key constraints failed");
        }
        // A user's notifications can be retrieved by the user or by an admin
        if (!current.checkAdministrator() && !current.equals(user)) {
            throw new PiikForbiddenException("The current user isn't allowed to retrieved the queried notifications");
        }

        return parentFacade.getInteractionDao().getNotifications(user);
    }
}
