package piiksuma.api.dao;

import piiksuma.Achievement;
import piiksuma.User;
import piiksuma.UserType;
import piiksuma.database.InsertionMapper;

import java.sql.Connection;
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
    public User createUser(User user, User current){

        if(current == null){
            return null;
        }

        // Check that the current user is an administrator
        if(current.getType().equals(UserType.user)){
            return null;
        }

        if(user == null){
            return null;
        }

        // Check that the primary keys are not null
        if(!user.checkNotNull()){
            return null;
        }

        // Insertion is done with the user data passed by parameter
        new InsertionMapper<User>(super.getConnection()).add(user).definirClase(User.class).insertar();

        // TODO debería devolver el usuario con la fecha actual que le añadió la base de datos?
        return user;
    }

    public User updateUser(User user, User current){
        return null;
    }

    public User getUser(User user, User current){
        return null;
    }

    public List<Achievement> getAchievement(User user, User current){
        return null;
    }

    public Achievement createAchievement(Achievement achievement, User current){
        return null;
    }

    public Achievement unlockAchievement(Achievement achievement, User current){
        return null;
    }

    public void followUser(User user, User current){

    }

    public void unfollowUser(User user, User current){

    }

}
