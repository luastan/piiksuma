package piiksuma.api;

import piiksuma.*;

import java.sql.Connection;
import java.sql.SQLException;
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
    public List<User> searchUser(User user, User current, Integer limit){
        return parentFacade.getUserDao().searchUser(user, limit);
    }

    /**
     * Function to get the user that matches the given specifications
     *
     * @param user user that contains the requirements that will be applied in the search
     * @return user that meets the given information
     */
    public User getUser(User user, User current){
        return parentFacade.getUserDao().getUser(user);
    }

    /**
     * Function that returns the statistics of the given user
     *
     * @param user user about whose statistics we want to know
     * @return computed statistics
     * @throws SQLException
     */

    public Statistics getUserStatistics(User user, User current) throws SQLException{
        return parentFacade.getUserDao().getUserStatistics(user);
    }

    public List<Achievement> getAchievement(User user, User current){
        return parentFacade.getUserDao().getAchievement(user);
    }

    /* MULTIMEDIA related methods */

    public Multimedia existsMultimedia(Multimedia multimedia, User current){
        return parentFacade.getMultimediaDao().existsMultimedia(multimedia);
    }

    public Integer numPostMultimedia(Multimedia multimedia, User current){
        return parentFacade.getMultimediaDao().numPostMultimedia(multimedia);
    }

    public List<Post> postWithMultimedia(Multimedia multimedia){
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

    /* MESSAGE related methods */



    /* INTERACTION related methods */


}
