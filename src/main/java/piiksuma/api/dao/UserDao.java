package piiksuma.api.dao;

import piiksuma.Achievement;
import piiksuma.User;

import java.sql.Connection;
import java.util.List;

public class UserDao extends AbstractDao{

    public UserDao(Connection connection) {
        super(connection);
    }

    public void removeUser(User user, User current){

    }

    public User createUser(User user, User current){
        return null;
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
