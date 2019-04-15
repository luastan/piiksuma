package piiksuma.api.dao;

import piiksuma.*;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;
import piiksuma.database.UpdateMapper;
import piiksuma.exceptions.PiikDatabaseException;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractionDao extends AbstractDao {
    public InteractionDao(Connection connection) {
        super(connection);
    }

    public void removeEvent(Event event) throws PiikDatabaseException {

        if (event == null || !event.checkNotNull()) {
            throw new PiikDatabaseException("(event) Primary key constraints failed");
        }

        new DeleteMapper<Event>(super.getConnection()).add(event).defineClass(Event.class).delete();
    }

    public void removeReaction(Reaction reaction) throws PiikDatabaseException {

        if (reaction == null || !reaction.checkNotNull()) {
            throw new PiikDatabaseException("(reaction) Primary key constraints failed");
        }

        new DeleteMapper<Reaction>(super.getConnection()).add(reaction).defineClass(Reaction.class).delete();
    }

    public void updateEvent(Event event) throws PiikDatabaseException {

        if (event == null || !event.checkNotNull()) {
            throw new PiikDatabaseException("(event) Primary key constraints failed");
        }

        new UpdateMapper<Event>(super.getConnection()).add(event).defineClass(Event.class).update();

    }

    /**
     * Inserts into the database a given reaction
     *
     * @param reaction reaction to be inserted
     */
    public void react(Reaction reaction) throws PiikDatabaseException {

        if (reaction == null || !reaction.checkNotNull()) {
            throw new PiikDatabaseException("(reaction) Primary key constraints failed");
        }

        new InsertionMapper<Reaction>(super.getConnection()).add(reaction).defineClass(Reaction.class).insert();
    }

    public void createEvent(Event event) throws PiikDatabaseException {

        if (event == null || !event.checkNotNull()) {
            throw new PiikDatabaseException("(event) Primary key constraints failed");
        }

        new InsertionMapper<Event>(super.getConnection()).add(event).defineClass(Event.class).insert();

    }

    /**
     * Gets the number of reactions that a post has, classified by type
     *
     * @param post post whose reactions will be counted
     * @return number of reactions classified by type
     */
    public HashMap<ReactionType, Integer> getPostReactionsCount(Post post) throws PiikDatabaseException {

        if (post == null || !post.checkNotNull()) {
            throw new PiikDatabaseException("(post) Primary key constraints failed");
        }

        HashMap<ReactionType, Integer> result = new HashMap<>();

        // We get the reactions associated with the post and we group them by type
        List<Map<String, Object>> query = new QueryMapper<Object>(super.getConnection()).createQuery(
                "SELECT reactionType as type, count(usr) as number FROM react WHERE post = ? AND author = ? GROUP BY " +
                        "reactionType").defineParameters(post.getId(), post.getPostAuthor()).mapList();

        for (Map<String, Object> row : query) {
            result.put(ReactionType.stringToReactionType((String) row.get("type")), (Integer) row.get("number"));
        }

        return result;
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

        new InsertionMapper<Object>(super.getConnection()).createUpdate("INSERT INTO haveNotification (notification,usr) " +
                "VALUES (?,?)").defineClass(Object.class).defineParameters(notification.getId(), user.getEmail()).executeUpdate();

    }

    /**
     * Retrieves the notifications that a user has received
     *
     * @param user user whose notifications will be retrieved
     * @return found notifications
     */
    public List<Notification> getNotifications(User user) throws PiikDatabaseException {

        if (user == null || !user.checkNotNull()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }

        return new QueryMapper<Notification>(super.getConnection()).createQuery("SELECT n.* FROM notification as n," +
                "haveNotification as h WHERE n.id = h.notification AND h.usr = " + "?").defineParameters(
                user.getEmail()).list();
    }
}
