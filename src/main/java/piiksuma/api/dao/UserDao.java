package piiksuma.api.dao;

import piiksuma.*;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;
import piiksuma.database.UpdateMapper;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserDao extends AbstractDao {

    public UserDao(Connection connection) {
        super(connection);
    }

    /**
     * Function to remove a user from the database
     *
     * @param user user to remove
     */
    public void removeUser(User user) throws PiikDatabaseException {

        new DeleteMapper<User>(super.getConnection()).defineClass(User.class).add(user).delete();
    }

    /**
     * Function to insert a new user into the database
     *
     * @param user user to insert into the database
     */
    public void createUser(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }

        // Insertion is done with the given user data, which is passed by parameters
        new InsertionMapper<User>(super.getConnection()).add(user).defineClass(User.class).insert();
    }

    public void updateUser(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }

        new UpdateMapper<User>(super.getConnection()).add(user).defineClass(User.class).update();
    }

    /**
     * Function to get the user that matches the given specifications
     *
     * @param user user that contains the requirements that will be applied in the search
     * @return user that meets the given information
     */
    public User getUser(User user) throws PiikDatabaseException {

        return new QueryMapper<User>(super.getConnection()).createQuery("SELECT * FROM piiUser " +
                "WHERE id LIKE '?'").defineClass(User.class).defineParameters(user.getId()).findFirst();
    }

    /**
     * Function to get the users that match with the specifications
     *
     * @param user  user that contains the requirements that will be applied in the search
     * @param limit maximum of users to return
     * @return users that meet the given information
     */
    public List<User> searchUser(User user, Integer limit) throws PiikDatabaseException{

        return new QueryMapper<User>(super.getConnection()).createQuery("SELECT * FROM piiUser " +
                "WHERE id LIKE '%?%' and name LIKE '%?%' LIMIT ?").defineClass(User.class).defineParameters(
                user.getId(), user.getName(), limit).list();
    }


    public List<Achievement> getAchievement(User user) throws PiikDatabaseException {

        return null;
    }


    /**
     * Function to login into the app; it will try to find a user that meets the given keys
     *
     * @param user user whose primary fields will be used in the search
     * @return user from database that meets the required attributes
     */
    public User login(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }

        return new QueryMapper<User>(super.getConnection()).createQuery("SELECT * FROM piiUser " +
                "Where id = ? and pass = ?").defineClass(User.class).defineParameters(user.getId(), user.getPass()).findFirst();
    }

    /**
     * Function to insert or to update personal data in the user profile
     *
     * @param user user that is going to be modified
     */
    public void administratePersonalData(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }

        /*if(!user.equals(currentUser)) {
            return;
        }*/

        new UpdateMapper<User>(super.getConnection()).add(user).defineClass(User.class).update();
    }

    public void createAchievement(Achievement achievement) throws PiikDatabaseException {

        if (achievement == null || !achievement.checkPrimaryKey()) {
            throw new PiikDatabaseException("(achievement) Primary key constraints failed");
        }

        new InsertionMapper<Achievement>(super.getConnection()).add(achievement).defineClass(Achievement.class).insert();
    }

    public Achievement unlockAchievement(Achievement achievement) throws PiikDatabaseException {

        if (achievement == null || !achievement.checkPrimaryKey()) {
            throw new PiikDatabaseException("(achievement) Primary key constraints failed");
        }

        return null;
    }

    /**
     * Function to follow a user
     *
     * @param followed User to be followed
     * @param follower User who follows
     * @throws PiikDatabaseException
     */
    public void followUser(User followed, User follower) throws PiikDatabaseException {

        new QueryMapper<Object>(super.getConnection()).createQuery("INSERT INTO followuser(followed,follower) " +
                "VALUES (?,?)").defineClass(Object.class).defineParameters(followed.getEmail(), follower.getEmail()).executeUpdate();

    }

    /**
     * Function to unfollow a user
     *
     * @param followed User to be unfollowed
     * @param follower User who wants to unfollow the followed user
     * @throws PiikDatabaseException
     */
    public void unfollowUser(User followed, User follower) throws PiikDatabaseException{

        new QueryMapper<Object>(super.getConnection()).createQuery("DELETE FROM followUser " +
                "WHERE followed=? AND follower=?").defineClass(Object.class).defineParameters(followed.getEmail(), follower.getEmail()).executeUpdate();
    }

    /**
     * Function to block a user
     *
     * @param blockedUser User to be blocked
     * @param user        User who wants to block the other user
     * @throws PiikDatabaseException
     */
    public void blockUser(User blockedUser, User user) throws PiikDatabaseException {

        if (blockedUser == null || !blockedUser.checkPrimaryKey()) {
            throw new PiikDatabaseException("(blockedUser) Primary key constraints failed");
        }
        if (user == null || !user.checkPrimaryKey()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }

        new QueryMapper<Object>(super.getConnection()).createQuery("INSERT INTO blockUser(usr,blocked) " +
                "VALUES (?,?)").defineClass(Object.class).defineParameters(user.getEmail(), blockedUser.getEmail()).executeUpdate();
    }

    /**
     * Function to silence a user
     *
     * @param silencedUser User to be silenced
     * @param user         User who wants to silence the other user
     * @throws PiikDatabaseException
     */
    public void silenceUser(User silencedUser, User user) throws PiikDatabaseException {
        if (silencedUser == null || !silencedUser.checkPrimaryKey()) {
            throw new PiikDatabaseException("(silencedUser) Primary key constraints failed");
        }
        if (user == null || !user.checkPrimaryKey()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }

        new QueryMapper<Object>(super.getConnection()).createQuery("INSERT INTO silenceUser(usr,silenced) " +
                "VALUES (?,?)").defineClass(Object.class).defineParameters(user.getEmail(), silencedUser.getEmail()).executeUpdate();
    }

    /**
     * Function that returns the statistics of the given user
     *
     * @param user user about whose statistics we want to know
     * @return computed statistics
     */
    public Statistics getUserStatistics(User user) throws PiikDatabaseException {
        Statistics statistics = new Statistics();

        List<Map<String, Object>> estatistics = new QueryMapper<User>(super.getConnection()).createQuery(
                //Query to take the number of followers the given user has
                "SELECT count(follower) AS followers \n" +
                        "FROM followuser \n" +
                        "WHERE followed LIKE ? \n").defineParameters(user.getEmail()).mapList();

        statistics.setFollowers((Long) estatistics.get(0).get("followers"));

        estatistics = new QueryMapper<User>(super.getConnection()).createQuery(
                "\n" +//Query to take the number of users that the user follows
                        "SELECT count(followed) AS followed \n" +
                        "FROM followuser \n" +
                        "WHERE follower LIKE ? \n").defineParameters(user.getEmail()).mapList();

        statistics.setFollowing((Long) estatistics.get(0).get("followed"));

        estatistics = new QueryMapper<User>(super.getConnection()).createQuery(
                "\n" +//Query to take the number of users that followback the given user
                        "WITH followerstable AS( SELECT * \n" +
                        "FROM followuser \n" +
                        "WHERE followed LIKE ? ),\n" +
                        "\n" +
                        "followedtable AS( SELECT * \n" +
                        "FROM followuser \n" +
                        "WHERE follower LIKE ? )\n" +
                        "\n" +
                        "SELECT COUNT(*) AS followback " +
                        "FROM followedtable, followerstable " +
                        "WHERE followedtable.followed=followerstable.follower").defineParameters(user.getEmail()).mapList();

        statistics.setFollowBack((Long) estatistics.get(0).get("followback"));

        estatistics = new QueryMapper<User>(super.getConnection()).createQuery(
                "SELECT count(reactiontype) AS reaction " +
                        "FROM react " +
                        "WHERE author LIKE ? AND reactiontype='LikeIt' ").defineParameters(user.getEmail()).mapList();

        statistics.setMaxLikeIt((Long) estatistics.get(0).get("reaction"));

        estatistics = new QueryMapper<User>(super.getConnection()).createQuery(
                "SELECT count(reactiontype) AS reaction " +
                        "FROM react " +
                        "WHERE author LIKE ? AND reactiontype='LoveIt' ").defineParameters(user.getEmail()).mapList();

        statistics.setMaxLoveIt((Long) estatistics.get(0).get("reaction"));

        estatistics = new QueryMapper<User>(super.getConnection()).createQuery(
                "SELECT count(reactiontype) AS reaction " +
                        "FROM react " +
                        "WHERE author LIKE ? AND reactiontype='HateIt' ").defineParameters(user.getEmail()).mapList();

        statistics.setMaxHateIt((Long) estatistics.get(0).get("reaction"));

        estatistics = new QueryMapper<User>(super.getConnection()).createQuery(
                "SELECT count(reactiontype) AS reaction " +
                        "FROM react " +
                        "WHERE author LIKE ? AND reactiontype='MakesMeAngry' ").defineParameters(user.getEmail()).mapList();

        statistics.setMaxMakesMeAngry((Long) estatistics.get(0).get("reaction"));

        return statistics;
    }

    /**
     * Retrieves userType from a desired user
     *
     * @param user User whose type wants to be known
     * @return UserType
     */
    public UserType getUserType(User user) throws PiikDatabaseException {
        return getUserType(user.getEmail());
    }

    /**
     * Retrieves userType from a desired user
     *
     * @param email Email from the desired user
     * @return UserType
     * @throws PiikDatabaseException When null values are passed as parameters
     */
    public UserType getUserType(String email) throws PiikDatabaseException {
        if (email == null) {
            throw new PiikDatabaseException("(user) email can't be null");
        }
        if (!(new QueryMapper<User>(this.getConnection()).createQuery("SELECT email FROM piiuser where email=?")
                .defineParameters(email).defineClass(User.class).list(false).size() > 0)) {
            throw new PiikDatabaseException("(user) User does not exist");
        }

        return new QueryMapper<User>(this.getConnection()).createQuery("SELECT id FROM administrator where id=?")
                .defineParameters(email).defineClass(User.class).list(false).size() > 0 ?
                UserType.administrator : UserType.user;
    }
}
