package piiksuma.api.dao;

import piiksuma.*;
import piiksuma.Notification;
import piiksuma.database.InsertionMapper;
import piiksuma.exceptions.PiikDatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class InteractionDao extends AbstractDao {
    public InteractionDao(Connection connection) {
        super(connection);
    }

    public void removeEvent(Event e) {

    }

    public void removeReaction(Reaction reaction) {

    }

    public Event updateEvent(Event event) {
        return null;
    }

    public void react(Reaction reaction) {

    }

    public Event createEvent(Event event) {
        return null;
    }

    public List<Reaction> getPostReaction(Post post) {
        return null;
    }

    public HashMap<ReactionType, Integer> getPostReaction(Post post) {
        return null;
    }

    /**
     * Inserts a new notification on a user
     *
     * @param notification notification given to the user
     */
    public void createNotification(Notification notification) {

        new InsertionMapper<Notification>(super.getConnection()).add(notification).defineClass(Notification.class).insert();
    }

    /**
     * This function associates a notification with a user
     *
     * @param notification notification that we want the user to see
     * @param user         user that we want to notify
     */
    public void notifyUser(Notification notification, User user) throws PiikDatabaseException {

        Connection con;
        PreparedStatement stmtNotification;

        // We check that the given objects are not null and that the primary keys are correct
        if (notification == null || !notification.checkPrimaryKey()) {
            throw new PiikDatabaseException("(notification) Primary key constraints failed");
        }

        if (user == null  || !user.checkPrimaryKey()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }


        con = super.getConnection();

        try {
            // We insert the data on haveNotification to notify the user
            stmtNotification = con.prepareStatement("INSERT INTO haveNotification (notification,usr) VALUES (?,?)");
            // Set the parameters
            stmtNotification.setString(1, notification.getId());
            stmtNotification.setString(2, user.getEmail());

            try {
                stmtNotification.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                // We need to close the statement before exiting the function
                stmtNotification.close();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
