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
     * @param user user that contains the requirements that will be applied in the search
     * @param limit maximum of users to return
     * @param current current user
     * @return users that meet the given information
     */
    public List<User> searchUser(User user, User current, Integer limit) throws PiikInvalidParameters,
            PiikDatabaseException {
        return parentFacade.getUserDao().searchUser(user, limit);
    }

    /**
     * Function to get the user that matches the given specifications
     *
     * @param user user that contains the requirements that will be applied in the search
     * @param current current user
     * @return user that meets the given information
     */
    public User getUser(User user, User current) throws PiikDatabaseException {
        return parentFacade.getUserDao().getUser(user);
    }

    /**
     * Function that returns the statistics of the given user
     *
     * @param user user about whose statistics we want to know
     * @param current current user
     * @return computed statistics
     * @throws SQLException
     */

    public Statistics getUserStatistics(User user, User current) throws SQLException, PiikDatabaseException {
        return parentFacade.getUserDao().getUserStatistics(user);
    }

    public List<Achievement> getAchievement(User user, User current) throws PiikDatabaseException {
        return parentFacade.getUserDao().getAchievement(user);
    }

    /* MULTIMEDIA related methods */

    public Multimedia existsMultimedia(Multimedia multimedia, User current){
        return parentFacade.getMultimediaDao().existsMultimedia(multimedia);
    }

    public Integer numPostMultimedia(Multimedia multimedia, User current){
        return parentFacade.getMultimediaDao().numPostMultimedia(multimedia);
    }

    public List<Post> postWithMultimedia(Multimedia multimedia, User current){
        return parentFacade.getMultimediaDao().postWithMultimedia(multimedia);
    }

    /* POST related methods */

    public Post getPost(Post post, User current) {
        return parentFacade.getPostDao().getPost(post);
    }

    public List<Post> getPost(Hashtag hashtag, User current) {
        return parentFacade.getPostDao().getPost(hashtag);
    }

    public List<Post> getPost(User user, User current) {
        return parentFacade.getPostDao().getPost(user);
    }

    /**
     * Function to get the hashtag that matchs with the given specifications
     *
     * @param hashtag hashtag whose properties will be used in the search
     * @param current current user
     * @return hashtag that matches the given information
     */
    public Hashtag getHashtag(Hashtag hashtag, User current) {
        return parentFacade.getPostDao().getHashtag(hashtag);
    }

    /**
     * Function to get the hashtags that match with the specifications
     *
     * @param hashtag hashtag whose properties will be used in the search
     * @param limit maximum number of tickets to retrieve
     * @param current current user
     * @return hashtags that match the given information
     */
    public List<Hashtag> searchHashtag(Hashtag hashtag, Integer limit, User current) {
        return parentFacade.getPostDao().searchHashtag(hashtag, limit);
    }

    /**
     * Function to search the posts which contain a given text, ordered by descending publication date
     *
     * @param text        text to be searched
     * @param limit maximum number of tickets to retrieve
     * @param current current user
     * @return list of the posts which contain the given text
     */
    public List<Post> searchByText(String text, Integer limit, User current) {
        return parentFacade.getPostDao().searchByText(text, limit);
    }

    /**
     * Function that composes a user's feed; the following posts are included:
     * - Posts made by followed users
     * - Posts made by the user
     * - The 20 most reacted to posts that are in the user's followed hashtags
     *
     * @param user        user whose feed will be retrieved
     * @param limit limit of the posts
     * @param current current user
     * @return posts that make up the user's feed
     */
    public List<Post> getFeed(User user, Integer limit, User current) throws PiikForbiddenException {

        if(user.equals(current)){
            return parentFacade.getPostDao().getFeed(user, limit);
        } else {
            throw new PiikForbiddenException("The current user doesn't match the given user");
        }
    }

    /**
     * Retrieves the posts that a user has archived
     *
     * @param user user whose archived posts will be retrieved
     * @param current current user
     * @return found posts
     */
    public List<Post> getArchivedPosts(User user, User current) throws PiikDatabaseException {

        // A user's archived posts can be retrieved by an user or by an admin
        if(!current.getType().equals(UserType.administrator) && !current.equals(user)) {
            throw new PiikForbiddenException("The current user isn't allowed to retrieved the queried archived posts");
        }

        return parentFacade.getPostDao().getArchivedPosts(user);
    }

    public List<Hashtag> getTrendingTopics(Integer limit, User current) throws PiikInvalidParameters{
        return parentFacade.getPostDao().getTrendingTopics(limit);
    }

    /* MESSAGE related methods */

    /**
     * This function allows the admins to retrieve the current unresolved tickets
     *
     * @param limit maximum number of tickets to retrieve
     * @param current current user
     * @return the list of all the tickets which haven't been closed
     */
    public List<Ticket> getAdminTickets(Integer limit, User current) throws PiikInvalidParameters, PiikForbiddenException {
        if(!current.getType().equals(UserType.administrator)){
            throw new PiikForbiddenException("The current user isn't an administrator");
        }

        return parentFacade.getMessagesDao().getAdminTickets(limit);
    }


    /* INTERACTION related methods */

    public HashMap<ReactionType, Integer> getPostReaction(Post post, User current) throws PiikDatabaseException {
        return parentFacade.getInteractionDao().getPostReaction(post);
    }

    /**
     * Retrieves the notifications that a user has received
     *
     * @param user user whose notifications will be retrieved
     * @param current current user
     * @return found notifications
     */
    public List<Notification> getNotifications(User user, User current) throws PiikDatabaseException {

        // A user's notifications can be retrieved by an user or by an admin
        if(!current.getType().equals(UserType.administrator) && !current.equals(user)) {
            throw new PiikForbiddenException("The current user isn't allowed to retrieved the queried notifications");
        }

        return parentFacade.getInteractionDao().getNotifications(user);
    }
}
