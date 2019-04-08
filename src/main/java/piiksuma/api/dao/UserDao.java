package piiksuma.api.dao;

import piiksuma.Achievement;
import piiksuma.User;
import piiksuma.UserType;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class UserDao extends AbstractDao{

    public UserDao(Connection connection) {
        super(connection);
    }

    public void removeUser(User user, User current){

    }

    /**
     * Function to add a new user in the database
     * @param user user to enter in the database
     * @param current current user logged in the app
     * @return user passed by parameter
     */
    public void createUser(User user, User current){

        if(current == null){
            return;
        }

        // Check that the current user is an administrator
        if(current.getType().equals(UserType.user)){
            return;
        }

        if(user == null){
            return;
        }

        // Check that the primary keys are not null
        if(!user.checkNotNull()){
            return;
        }

        // Insertion is done with the user data passed by parameter
        new InsertionMapper<User>(super.getConnection()).add(user).definirClase(User.class).insertar();
    }

    public void updateUser(User user, User current){

    }

    /**
     * Function to get the user that meet with the specifications
     * @param user user that contains the specification about the user to search
     * @param current current user logged in the app
     * @return user with all the information
     */
    public User getUser(User user, User current){

        if(user == null){
            return null;
        }

        if(!user.checkPrimaryKey()){
            return null;
        }

        return new QueryMapper<User>(super.getConnection()).crearConsulta("SELECT * FROM piiUser " +
                "WHERE id LIKE '?'").definirEntidad(User.class).definirParametros(user.getId()).findFirst();
    }

    /**
     * Function to get the users that meet with the specifications
     * @param user user that contains the specification about the users to search
     * @param current current user logged in the app
     * @param limit maximum of users to return
     * @return users with all the information
     */
    public List<User> searchUser(User user, User current, Integer limit){

        if(user == null){
            return null;
        }

        if(limit <= 0){
            return new ArrayList<>();
        }

        return new QueryMapper<User>(super.getConnection()).crearConsulta("SELECT * FROM piiUser " +
                "WHERE id LIKE '%?%' and name LIKE '%?%' LIMIT ?").definirEntidad(User.class).definirParametros(
                user.getId(), user.getName(), limit).list();
    }

    public List<Achievement> getAchievement(User user, User current){
        return null;
    }

    public User login(User user, User current){
        if (user == null){
            return null;
        }

        return new QueryMapper<User>(super.getConnection()).crearConsulta("SELECT * FROM piiUser "+
                "Where id = ? and pass = ?" ).definirEntidad(User.class).definirParametros(user.getId(),user.getPass()).findFirst();
    }

    public void createAchievement(Achievement achievement, User current){
    }

    public Achievement unlockAchievement(Achievement achievement, User current){
        return null;
    }

    public void followUser(User user, User current){

    }

    public void unfollowUser(User user, User current){

    }

}
