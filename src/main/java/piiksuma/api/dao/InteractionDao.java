package piiksuma.api.dao;

import piiksuma.*;
import piiksuma.Notification;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;
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


        // We check that the given objects are not null and that the primary keys are correct
        if (notification == null || !notification.checkNotNull()) {
            throw new PiikDatabaseException("(notification) Primary key constraints failed");
        }

        if (user == null || !user.checkNotNull()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }

        new QueryMapper<Object>(super.getConnection()).createQuery("INSERT INTO haveNotification (notification,usr) " +
                "VALUES (?,?)").defineClass(Object.class).defineParameters(notification.getId(), user.getEmail()).executeUpdate();

    }

    /**
     * Retrieves the notifications that a user has received
     *
     * @param user user whose notifications will be retrieved
     * @return found notifications
     */
    public List<Notification> getNotifications(User user) throws PiikDatabaseException {

        return new QueryMapper<Notification>(super.getConnection()).createQuery("SELECT n.* FROM notification as n," +
                "haveNotification as h WHERE n.id = h.notification AND h.usr = " + "?").defineParameters(
                user.getEmail()).list();
    }
}
