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

    public void removeEvent(Event event) throws PiikDatabaseException {

        if (event == null || !event.checkNotNull()) {
            throw new PiikDatabaseException("(event) Primary key constraints failed");
        }
    }

    public void removeReaction(Reaction reaction) throws PiikDatabaseException {

        if (reaction == null || !reaction.checkNotNull()) {
            throw new PiikDatabaseException("(reaction) Primary key constraints failed");
        }
    }

    public Event updateEvent(Event event) throws PiikDatabaseException {

        if (event == null || !event.checkNotNull()) {
            throw new PiikDatabaseException("(event) Primary key constraints failed");
        }

        return null;
    }

    public void react(Reaction reaction) throws PiikDatabaseException {

        if (reaction == null || !reaction.checkNotNull()) {
            throw new PiikDatabaseException("(reaction) Primary key constraints failed");
        }
    }

    public Event createEvent(Event event) throws PiikDatabaseException {

        if (event == null || !event.checkNotNull()) {
            throw new PiikDatabaseException("(event) Primary key constraints failed");
        }

        return null;
    }

    public HashMap<ReactionType, Integer> getPostReaction(Post post) throws PiikDatabaseException {

        if (post == null || !post.checkNotNull()) {
            throw new PiikDatabaseException("(post) Primary key constraints failed");
        }

        return null;
    }

    /**
     * Inserts a new notification on a user
     *
     * @param notification notification given to the user
     */
    public void createNotification(Notification notification) throws PiikDatabaseException {

        if (notification == null || !notification.checkNotNull()) {
            throw new PiikDatabaseException("(notification) Primary key constraints failed");
        }

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
        if (notification == null || !notification.checkNotNull()) {
            throw new PiikDatabaseException("(notification) Primary key constraints failed");
        }

        if (user == null  || !user.checkNotNull()) {
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
