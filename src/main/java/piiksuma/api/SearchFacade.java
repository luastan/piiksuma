package piiksuma.api;

import javafx.scene.image.Image;
import piiksuma.*;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikForbiddenException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * @param user            user that contains the requirements that will be applied in the search
     * @param typeTransaction nivel of isolation
     * @param current         current user
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
            if (!getConnection().getMetaData().supportsTransactionIsolationLevel(typeTransaction)) {
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
     * Function to check if the user followed follow the user follower
     *
     * @param followed
     * @param follower
     * @return
     */
    public boolean isFollowed(User followed, User follower, User current) throws PiikDatabaseException, PiikInvalidParameters {
        if (followed == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("followed"));
        }

        if (follower == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("follower"));
        }

        if (current == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        return parentFacade.getUserDao().isFollowed(followed, follower);
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

         User candidate = parentFacade.getUserDao().login(user);
       // User candidate = parentFacade.getUserDao().getUser(user);
        if (candidate == null) {
            return null;
        }

        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new PiikInvalidParameters(ErrorMessage.getNotExistsMessage("hashAlgorithm"));
        }

        Matcher matcher = Pattern.compile("(.*)\\$(.*)").matcher(candidate.getPass());
        if (!matcher.find()) {
            return null;
        }
        byte[] salt = Base64.getDecoder().decode(matcher.group(1));
        messageDigest.update(salt);
        String storedHash = matcher.group(2);
        String calculatedHash = Base64.getEncoder().encodeToString(messageDigest.digest(user.getPass().getBytes()));
        //candidate.setPass(" ");
        return storedHash.equals(calculatedHash) ? candidate : null;
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

    /**
     * Gets the achievements from an user
     *
     * @param user    User we want to get the achievements from
     * @param current Curren user logged into the app
     * @return
     * @throws PiikInvalidParameters
     * @throws PiikDatabaseException
     */
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

    /**
     * Gets the dates when the achievements were unlocked by the user
     *
     * @param user User whom we get the dates from
     * @return Returns a map with the name of the achievement and the date
     * @throws PiikDatabaseException
     * @throws PiikInvalidParameters
     */
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

    /**
     * Gets one multimedia
     *
     * @param multimedia Multimedia we are searching for
     * @param current    Current user logged into the app
     * @return Returns the multimedia we were looking for
     * @throws PiikInvalidParameters
     * @throws PiikDatabaseException
     */
    public Multimedia getMultimedia(Multimedia multimedia, User current) throws PiikInvalidParameters, PiikDatabaseException {
        if (multimedia == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("multimedia"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        return parentFacade.getMultimediaDao().getMultimedia(multimedia);
    }

    /**
     * Checks if the multimedia already exists in the db
     *
     * @param multimedia Multimedia we search for
     * @param current    Current user logged into the app
     * @return
     * @throws PiikInvalidParameters
     * @throws PiikDatabaseException
     */
    public boolean existsMultimedia(Multimedia multimedia, User current) throws PiikInvalidParameters, PiikDatabaseException {
        if (multimedia == null || !multimedia.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("multimedia"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        return parentFacade.getMultimediaDao().existsMultimedia(multimedia);
    }

    /**
     * Count the number of post which contains the given multimedia
     *
     * @param multimedia Multimedia we want to count
     * @return Number of post containing multimedia
     * @throws PiikDatabaseException Thrown if multimedia or its primary key are null
     * @throws PiikInvalidParameters
     * @throws PiikDatabaseException
     */
    public Long numPostMultimedia(Multimedia multimedia, User current) throws PiikInvalidParameters, PiikDatabaseException {
        if (multimedia == null || !multimedia.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("multimedia"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        return parentFacade.getMultimediaDao().numPostMultimedia(multimedia);
    }

    /**
     * Gets all post containing multimedia
     *
     * @param multimedia Multimedia we are looking for on posts
     * @return List of post containing multimedia
     * @throws PiikInvalidParameters
     * @throws PiikDatabaseException
     */
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

    /**
     * Checks if the specified post contains the a given hashtag
     *
     * @param hashtag hashtag that will be checked
     * @param post    post which may contain the given hashtag
     * @return if the given hashtag is contained in the specified post
     */
    public boolean hashtagInPost(Hashtag hashtag, Post post) throws PiikInvalidParameters, PiikDatabaseException {
        if (hashtag == null || !hashtag.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("hashtag"));
        }

        if (post == null || !post.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("post"));
        }

        return parentFacade.getPostDao().hashtagInPost(hashtag, post);
    }

    /**
     * Returns a post form db
     *
     * @param post Post wanted
     * @return Post
     * @throws PiikDatabaseException Thrown if post or its primary keys are null
     */
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

    /**
     * Function that returns the answers / children of the indicated post
     *
     * @param post
     * @return
     */
    public List<Post> getAnswers(Post post) throws PiikDatabaseException, PiikInvalidParameters {
        if (post == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("post"));
        }

        return parentFacade.getPostDao().getAnswers(post);
    }

    // TODO we need to set a limit

    /**
     * Gets a list of post containing a given hashtag
     *
     * @param hashtag Hashtag to be searched for
     * @return List with all the posts that have the hashtag
     * @throws PiikDatabaseException Thrown if hashtag or its primary keys are null
     */
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

    /**
     * Gets a list of post containing a given user
     *
     * @param user User to be searched for
     * @return List post created by the user
     * @throws PiikDatabaseException Thrown if user or its primary keys are null
     */
    public List<Post> getPost(User user, User current) throws PiikDatabaseException, PiikInvalidParameters {
        if (current == null || !current.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        if (user == null || !user.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
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
     * Function that determines if a user has reacted to a concrete post with a concrete reaction
     *
     * @param reaction reaction to check
     * @param user     user who wants to check if he reacted
     * @param current  current user
     * @return
     */
    public boolean isReact(Reaction reaction, User user, User current) throws PiikDatabaseException,
            PiikInvalidParameters {
        if (reaction == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("reaction"));
        }

        if (user == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if (current == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("current"));
        }

        return parentFacade.getInteractionDao().isReact(reaction, user);
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
        if (!current.checkAdministrator() && !current.equals(user)) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
        }

        return parentFacade.getPostDao().getArchivedPosts(user);
    }

    /**
     * Gets a List of the most used hashtags
     *
     * @param limit   Number of hashtags to put on the list
     * @param current Current user logged into the app
     * @return Return the list of hastags
     * @throws PiikDatabaseException
     * @throws PiikInvalidParameters
     */
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

    /* This function allows an user to retrieve his open tickets
     * @param user whose tickets will be retrieved
     * @param current current user
     * @param limit maximum number of tickets to retrieve
     * @return the list of all the tickets which haven't been closed
     * @throws PiikDatabaseException Thrown on query gone wrong
     * @throws PiikInvalidParameters Thrown if limit is equal or less than 0
     */
    public List<Ticket> getUserTickets(User user, User current, Integer limit) throws PiikDatabaseException, PiikInvalidParameters {

        if (user == null || !user.checkNotNull(false)) {
            throw new PiikDatabaseException(ErrorMessage.getNullParameterMessage("user"));
        }

        if (current == null || !current.checkNotNull(false)) {
            throw new PiikDatabaseException(ErrorMessage.getNullParameterMessage("current"));
        }

        if (!current.checkAdministrator() && !current.equals(user)) {
            throw new PiikForbiddenException(ErrorMessage.getPermissionDeniedMessage());
        }

        if (limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }

        return parentFacade.getMessagesDao().getUserTickets(user, limit);
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

        // TODO permission check

        return parentFacade.getMessagesDao().readMessages(user);
    }

    /**
     * Function to get the messages with other user
     *
     * @param user send of the messages
     * @return
     */
    public Map<User, List<Message>> messageWithUser(User user, Integer limit, User current) throws
            PiikDatabaseException, PiikInvalidParameters {

        if (user == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if (current == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("current"));
        }

        // TODO permission check


        return parentFacade.getMessagesDao().messageWithUser(user, limit);
    }

    /**
     * Function to get the private conversation of two users
     *
     * @param user1
     * @param user2
     * @param limit
     * @return
     */
    public List<Message> getConversation(User user1, User user2, User current, Integer limit) throws PiikDatabaseException,
            PiikInvalidParameters {

        if (user1 == null) {
            throw new PiikDatabaseException(ErrorMessage.getNullParameterMessage("user1"));
        }

        if (user2 == null) {
            throw new PiikDatabaseException(ErrorMessage.getNullParameterMessage("user2"));
        }

        if (current == null) {
            throw new PiikDatabaseException(ErrorMessage.getNullParameterMessage("user2"));
        }

        if (limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }

        // TODO permission check


        return parentFacade.getMessagesDao().getConversation(user1, user2, limit);

    }

    /**
     * Function to get a chat associated to a ticket
     *
     * @param limit
     * @return
     */
    public List<Message> getConversationTicket(Ticket ticket, User current, Integer limit) throws PiikDatabaseException,
            PiikInvalidParameters {


        if(ticket == null || !ticket.checkNotNull(false)){
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("ticket"));
        }

        if(limit <= 0){
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }

        // TODO permission check



        return parentFacade.getMessagesDao().getConversationTicket(ticket, limit);
    }


    /* INTERACTION related methods */

    /**
     * Gets the number of reactions that a post has, classified by type
     *
     * @param post    post whose reactions will be counted
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
        if (multimedia == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("multimedia"));
        }

        return parentFacade.getMultimediaDao().getImage(multimedia);
    }

    /**
     * Function to get the events
     *
     * @param user    user
     * @param current current user logged
     * @return list of the events that the followed users created
     */
    public List<Event> getEvents(User user, User current) throws PiikDatabaseException, PiikInvalidParameters {
        if (user == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if (current == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }
        return parentFacade.getInteractionDao().getEvents(user);
    }

    /**
     * Function that returns the users that participate in the indicated event
     *
     * @param event
     * @return a map with the user indexed by her primary key
     * @throws PiikDatabaseException
     */
    public Map<String, User> usersInEvent(Event event, User current) throws PiikDatabaseException,
            PiikInvalidParameters {
        if (event == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("event"));
        }

        if (current == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        return parentFacade.getInteractionDao().usersInEvent(event);
    }

    /**
     * Function to check if the user1 has blocked the user2
     *
     * @param user1
     * @param user2
     * @return
     */
    public boolean isBlock(User user1, User user2, User current) throws PiikDatabaseException, PiikInvalidParameters {
        if (user1 == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user1"));
        }

        if (user2 == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user2"));
        }

        if (current == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        return parentFacade.getUserDao().isBlock(user1, user2);
    }

    /**
     * Function to check if an user has already reposted a post
     *
     * @param user        User we want to check if he has already reposted
     * @param post        Post we want to check
     * @param currentUser Current user logged in the app
     * @return "True" if the user reposted the post, otherwise "false"
     * @throws PiikDatabaseException
     * @throws PiikInvalidParameters
     */
    public boolean checkUserResposted(User user, Post post, User currentUser) throws PiikDatabaseException, PiikInvalidParameters {
        if (user == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if (post == null || !post.checkNotNull(false)) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("post"));
        }

        if (currentUser == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        return parentFacade.getPostDao().checkUserResposted(user, post, currentUser);
    }

    /**
     * Function to check if a user has already archived a post
     *
     * @param post
     * @param user
     * @param currentUser
     * @return
     * @throws PiikInvalidParameters
     * @throws PiikDatabaseException
     */
    public boolean isPostArchived(Post post, User user, User currentUser) throws PiikInvalidParameters, PiikDatabaseException {
        if (user == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("user"));
        }

        if (post == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("post"));
        }

        if (currentUser == null) {
            throw new PiikInvalidParameters(ErrorMessage.getNullParameterMessage("currentUser"));
        }

        return parentFacade.getPostDao().isPostArchived(post, user);
    }
}
