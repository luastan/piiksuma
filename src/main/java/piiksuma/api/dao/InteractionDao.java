package piiksuma.api.dao;

import piiksuma.*;
import piiksuma.Notification;
import piiksuma.database.InsertionMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class InteractionDao extends AbstractDao {
    public InteractionDao(Connection connection) {
        super(connection);
    }

    public void removeEvent(Event e, User current) {

    }

    public void removeReaction(Reaction reaction, User current) {

    }

    public Event updateEvent(Event event, User current) {
        return null;
    }

    public void react(Reaction reaction, User current) {

    }

    public Event createEvent(Event event, User current) {
        return null;
    }

    public List<Reaction> getPostReaction(Post post) {
        return null;
    }

    public HashMap<ReactionType, Integer> getPostReaction(Post post, User current) {
        return null;
    }

    public void createNotification(Notification notification, User currentUser) {

        new InsertionMapper<Notification>(super.getConnection()).add(notification).defineClass(Notification.class).insert();
    }

    /**
     * This function adds a notification to a user to advice him
     * @param notification notification that we want the user to see
     * @param user user that we want to notify
     * @param currentUser current user logged in the app
     */
    public void notifyUser(Notification notification, User user, User currentUser) {
        Connection con;
        PreparedStatement stmtNotification;

        //We check that the given objects are not null and the primary keys are correct
        if(notification==null || user==null){
            return;
        }
        if(!user.checkPrimaryKey() || !notification.checkPrimaryKey()){
            return;
        }

        con = super.getConnection();

        try {
            //We insert the data on haveNotification to notify the user
            stmtNotification = con.prepareStatement("INSERT INTO haveNotification (notification,usr) VALUES (?,?)");
            //Set the parameters
            stmtNotification.setString(1, notification.getId());
            stmtNotification.setString(2, user.getEmail());

            stmtNotification.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                // We also must close de connection to the database to free its resources
                con.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
