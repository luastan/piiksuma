package piiksuma.api.dao;

import piiksuma.*;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;
import piiksuma.database.UpdateMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao extends AbstractDao {

    public UserDao(Connection connection) {
        super(connection);
    }

    /**
     * Function to remove a user from the database
     *
     * @param user user to remove
     */
    public void removeUser(User user) {
        new DeleteMapper<User>(super.getConnection()).defineClass(User.class).add(user).delete();
    }

    /**
     * Function to add a new user in the database
     *
     * @param user    user to enter in the database
     */
    public void createUser(User user) {

        if (user == null) {
            return;
        }

        // Check that the primary keys are not null
        if (!user.checkNotNull()) {
            return;
        }

        // Insertion is done with the user data passed by parameter
        new InsertionMapper<User>(super.getConnection()).add(user).defineClass(User.class).insert();
    }

    public void updateUser(User user) {

    }

    /**
     * Function to get the user that meet with the specifications
     *
     * @param user    user that contains the specification about the user to search
     * @return user with all the information
     */
    public User getUser(User user) {

        if (user == null) {
            return null;
        }

        if (!user.checkPrimaryKey()) {
            return null;
        }

        return new QueryMapper<User>(super.getConnection()).createQuery("SELECT * FROM piiUser " +
                "WHERE id LIKE '?'").defineClass(User.class).defineParameters(user.getId()).findFirst();
    }

    /**
     * Function to get the users that meet with the specifications
     *
     * @param user    user that contains the specification about the users to search
     * @param limit   maximum of users to return
     * @return users with all the information
     */
    public List<User> searchUser(User user, Integer limit) {

        if (user == null) {
            return null;
        }

        if (limit <= 0) {
            return new ArrayList<>();
        }

        if(!user.checkPrimaryKey()){
            return null;
        }

        return new QueryMapper<User>(super.getConnection()).createQuery("SELECT * FROM piiUser " +
                "WHERE id LIKE '%?%' and name LIKE '%?%' LIMIT ?").defineClass(User.class).defineParameters(
                user.getId(), user.getName(), limit).list();
    }

    public List<Achievement> getAchievement(User user) {
        return null;
    }


    /**
     * Function to login in the app
     *
     * @param user user that contains the specification about the users to search
     * @return the user find in the database
     */
    public User login(User user) {
        if (user == null) {
            return null;
        }

        if (!user.checkPrimaryKey()) {
            return null;
        }

        return new QueryMapper<User>(super.getConnection()).createQuery("SELECT * FROM piiUser " +
                "Where id = ? and pass = ?").defineClass(User.class).defineParameters(user.getId(), user.getPass()).findFirst();
    }

    /**
     * Function to insert or update personal data in the user profile
     *
     * @param user user that is going to be modificated
     */
    public void administratePersonalData(User user) {
        if (user == null) {
            return;
        }

        if (!user.checkPrimaryKey()) {
            return;
        }

        new UpdateMapper<User>(super.getConnection()).add(user).defineClass(User.class).update();
    }

    public void createAchievement(Achievement achievement) {
    }

    public Achievement unlockAchievement(Achievement achievement) {
        return null;
    }

    public void followUser(User followed, User follower) {

    }

    public void unfollowUser(User followed, User follower) {

    }

    /**
     * Function that returns the statistics of the given user
     *
     * @param user user we want to know his statistics
     * @return all the information that we want in the stats
     * @throws SQLException
     */
    public Statistics getUserStatistics(User user) throws SQLException {
        Statistics statistics = new Statistics();
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        String query;
        //TODO HACE ESTO CON LOS MAPPER
        // Query 1
        query = "SELECT count(reactiontype) FROM react WHERE author LIKE ? AND reactiontype LIKE ?";
        preparedStatement = super.getConnection().prepareStatement(query);

        preparedStatement.setString(1, user.getEmail());
        for (ReactionType reation : ReactionType.values()) {
            preparedStatement.setString(2, reation.toString());
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                continue;
            }

            switch (reation) {
                case LikeIt:
                    statistics.setMaxLikeIt(resultSet.getInt(1));
                    break;
                case HateIt:
                    statistics.setMaxHateIt(resultSet.getInt(1));
                    break;
                case LoveIt:
                    statistics.setMaxLoveIt(resultSet.getInt(1));
                    break;
                case MakesMeAngry:
                    statistics.setMaxMakesMeAngry(resultSet.getInt(1));
                    break;
            }
            resultSet.close();
        }

        // Query 2
        query = "SELECT count(followed) FROM followuser WHERE follower LIKE ?";
        preparedStatement = super.getConnection().prepareStatement(query);
        preparedStatement.setString(1, user.getEmail());

        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            statistics.setFollowing(resultSet.getInt(1));
        }

        // Query 35
        query = "SELECT count(follower) FROM followuser WHERE followed LIKE ?";
        preparedStatement = super.getConnection().prepareStatement(query);
        preparedStatement.setString(1, user.getEmail());

        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            statistics.setFollowers(resultSet.getInt(1));
        }
        // Query 4

        query = "SELECT count(followed) FROM followuser AS him WHERE follower LIKE ? AND " +
                "EXISTS(SELECT FROM followuser WHERE followed LIKE  ? AND follower LIKE him.followed)";
        preparedStatement = super.getConnection().prepareStatement(query);
        preparedStatement.setString(1, user.getEmail());
        preparedStatement.setString(2, user.getEmail());

        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            statistics.setFollowBack(resultSet.getInt(1));
        }


        return statistics;
    }

}
