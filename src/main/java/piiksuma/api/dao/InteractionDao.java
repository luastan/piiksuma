package piiksuma.api.dao;

import piiksuma.*;
import piiksuma.api.ErrorMessage;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;
import piiksuma.database.UpdateMapper;
import piiksuma.exceptions.PiikDatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractionDao extends AbstractDao {
    public InteractionDao(Connection connection) {
        super(connection);
    }

    // ==================================================== EVENTS =====================================================

    /*******************************************************************************************************************
     * Creates a new event
     * @param event Event to create
     * @return Returns the newly created event
     * @throws PiikDatabaseException Thrown if event or the primary key are null
     */
    public Event createEvent(Event event) throws PiikDatabaseException {
        // Check if event or primary key are null
        if (event == null || !event.checkPrimaryKey(true)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("event"));
        }

        // It will be returned when the method executes successfully
        Event completeEvent = new Event(event);

        // Which may-be-null parameters will be inserted
        boolean descriptionExists = event.getDescription() != null && !event.getDescription().isEmpty();
        boolean locationExists = event.getLocation() != null && !event.getLocation().isEmpty();
        boolean dateExists = event.getDate() != null;
        boolean authorExists = event.getCreator() != null && event.getCreator().checkNotNull(false);

        // Connection to the database
        Connection con = getConnection();
        // SQL clause
        PreparedStatement statement = null;

        // Built clause
        StringBuilder clause = new StringBuilder();

        try {

            /* Auto-commit */

            // The event won't be created unless there's no error generating its ID
            con.setAutoCommit(false);


            /* Statement */

            // Event's IDs are generated automatically when inserted
            clause.append("INSERT INTO event(name, description, location, date, author) VALUES (?");

            // Some attributes may be null
            if (descriptionExists) {
                clause.append(", ?");
            } else {
                clause.append(", NULL");
            }

            if (locationExists) {
                clause.append(", ?");
            } else {
                clause.append(", NULL");
            }

            if (dateExists) {
                clause.append(", ?");
            } else {
                clause.append(", NULL");
            }

            if (authorExists) {
                clause.append(", ?)");
            } else {
                clause.append(", NULL)");
            }

            clause.append(" RETURNING id");

            statement = con.prepareStatement(clause.toString());


            /* Clause's data insertion */

            statement.setString(1, event.getName());

            int offset = 2;

            if (descriptionExists) {
                statement.setString(offset++, event.getDescription());
            }

            if (locationExists) {
                statement.setString(offset++, event.getDescription());
            }

            if (dateExists) {
                statement.setTimestamp(offset++, event.getDate());
            }

            if (authorExists) {
                statement.setString(offset, event.getCreator().getPK());
            }


            /* Execution and key retrieval */

            ResultSet keys = statement.executeQuery();

            // ID generation successful
            if (keys.next()) {
                completeEvent.setId(keys.getString("id"));
            } else {
                throw new PiikDatabaseException("Event ID generation failed");
            }


            /* Commit */

            con.commit();

            // Restoring auto-commit to its default value
            con.setAutoCommit(true);


        } catch (SQLException e) {
            // Performed modifications in the database are rolled-back
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new PiikDatabaseException(ex.getMessage());
            }

            throw new PiikDatabaseException(e.getMessage());

        } finally {

            try {
                if (statement != null) {
                    statement.close();
                }

            } catch (SQLException e) {
                throw new PiikDatabaseException(e.getMessage());
            }
        }

        return (completeEvent);
    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Removes an event from the db
     *
     * @param event Event you want to remove
     * @throws PiikDatabaseException Thrown if event or the primary key are null
     */
    public void removeEvent(Event event) throws PiikDatabaseException {

        // Check if event or primary key are null
        if (event == null || !event.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("event"));
        }
        // Delete event
        new DeleteMapper<Event>(super.getConnection()).add(event).defineClass(Event.class).delete();
    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Update an event already on the db
     *
     * @param event Event you want to update
     * @throws PiikDatabaseException Thrown if event or the primary key are null
     */
    public void updateEvent(Event event) throws PiikDatabaseException {
        // Check if event or primary key are null
        if (event == null || !event.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("event"));
        }
        // Update event
        new UpdateMapper<Event>(super.getConnection()).add(event).defineClass(Event.class).update();
    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Allows an user to participate in one event
     * @param event Event you want to participate in
     * @param user  User that wants to participate in the event
     * @throws PiikDatabaseException Thrown if event/user or the primary key of event/user the are null
     */
    public void participateEvent(Event event, User user) throws PiikDatabaseException {
        // Check if event or primary key are null
        if (event == null || !event.checkPrimaryKey(true)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("event"));
        }

        if (user == null || !user.checkPrimaryKey(true)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }
        // Insert user as an attendant of the event
        new InsertionMapper<>(super.getConnection()).createUpdate("INSERT INTO participateevent VALUES(?,?,?)")
                .defineParameters(
                        event.getId(),
                        event.getCreator().getId(),
                        user.getId()
                ).executeUpdate();
    }
    //******************************************************************************************************************

    //==================================================================================================================
    // ================================================== REACTIONS ====================================================

    /*******************************************************************************************************************
     *  Inserts a reaction to a post from a user
     * @param reaction Reaction to insert
     * @throws PiikDatabaseException Thrown if reaction or the primary key are null
     */
    public void react(Reaction reaction) throws PiikDatabaseException {
        // Check if reaction or primary key are null
        if (reaction == null || !reaction.checkPrimaryKey(true)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("reaction"));
        }

        new InsertionMapper<>(super.getConnection()).createUpdate("INSERT INTO react(reactiontype, post, usr, " +
                "author) VALUES(?,?,?,?)")
                .defineParameters(reaction.getReactionType().toString(), reaction.getPost().getId(),
                        reaction.getUser().getPK(), reaction.getPost().getAuthor().getPK()).executeUpdate();
    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Removes an user reaction to a post
     *
     * @param reaction Reaction to be removed
     * @throws PiikDatabaseException Thrown if reaction or the primary key are null
     */
    public void removeReaction(Reaction reaction) throws PiikDatabaseException {
        // Check if reaction or primary key are null
        if (reaction == null || !reaction.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("reaction"));
        }
        // Delete reaction
        new DeleteMapper<Reaction>(super.getConnection()).createUpdate("DELETE FROM react WHERE" +
                " post = ? and usr = ? and author = ?").defineParameters(reaction.getPost().getId(),
                reaction.getUser().getPK(), reaction.getPost().getAuthor().getPK()).executeUpdate();
    }
    //******************************************************************************************************************
    //==================================================================================================================

    /**
     * Gets the number of reactions that a post has, classified by type
     *
     * @param post post whose reactions will be counted
     * @return number of reactions classified by type
     */
    public HashMap<ReactionType, Long> getPostReactionsCount(Post post) throws PiikDatabaseException {

        if (post == null || !post.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("post"));
        }

        HashMap<ReactionType, Long> result = new HashMap<>();

        // We get the reactions associated with the post and we group them by type
        List<Map<String, Object>> query = new QueryMapper<Object>(super.getConnection()).createQuery(
                "SELECT reactiontype as type, count(usr) as number FROM react WHERE post = ? AND author = ? GROUP BY " +
                        "reactiontype").defineParameters(post.getId(), post.getPostAuthor().getId()).mapList();

        for (Map<String, Object> row : query) {
            result.put(ReactionType.stringToReactionType((String) row.get("type")), (Long) row.get("number"));
        }

        return result;
    }

    /**
     * Inserts a new notification on a user
     *
     * @param notification notification given to the user
     * @return ticket containing the given data and its generated ID
     */
    public Notification createNotification(Notification notification) throws PiikDatabaseException {

        if (notification == null || !notification.checkPrimaryKey(true)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("notification"));
        }

        // It will be returned when the method executes successfully
        Notification completeNotification = new Notification(notification);

        // Connection to the database
        Connection con = getConnection();
        // SQL clause
        PreparedStatement statement = null;

        // Built clause
        StringBuilder clause = new StringBuilder();

        try {

            /* Auto-commit */

            // The notification won't be created unless there's no error generating its ID
            con.setAutoCommit(false);


            /* Statement */

            // Notification's IDs are generated automatically when inserted
            clause.append("INSERT INTO notification(creationdate, content) VALUES (?, ?) RETURNING id");

            statement = con.prepareStatement(clause.toString());


            /* Clause's data insertion */

            statement.setTimestamp(1, notification.getCreationDate());
            statement.setString(2, notification.getContent());


            /* Execution and key retrieval */

            ResultSet keys = statement.executeQuery();

            // ID generation successful
            if (keys.next()) {
                completeNotification.setId(keys.getString("id"));
            } else {
                throw new PiikDatabaseException("Notification ID generation failed");
            }


            /* Commit */

            con.commit();

            // Restoring auto-commit to its default value
            con.setAutoCommit(true);


        } catch (SQLException e) {
            // Performed modifications in the database are rolled-back
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new PiikDatabaseException(ex.getMessage());
            }

            throw new PiikDatabaseException(e.getMessage());

        } finally {

            try {
                if (statement != null) {
                    statement.close();
                }

            } catch (SQLException e) {
                throw new PiikDatabaseException(e.getMessage());
            }
        }

        return (completeNotification);
    }

    /**
     * This function associates a notification with a user
     *
     * @param notification notification that we want the user to see
     * @param user         user that we want to notify
     */
    public void notifyUser(Notification notification, User user) throws PiikDatabaseException {

        // We check that the given objects are not null and that the primary keys are correct
        if (notification == null || !notification.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("notification"));
        }

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        new InsertionMapper<Object>(super.getConnection()).createUpdate("INSERT INTO havenotification (notification,usr) " +
                "VALUES (?,?)").defineClass(Object.class).defineParameters(notification.getId(), user.getPK()).executeUpdate();

    }

    /**
     * Retrieves the notifications that a user has received
     *
     * @param user user whose notifications will be retrieved
     * @return found notifications
     */
    public List<Notification> getNotifications(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        return new QueryMapper<Notification>(super.getConnection()).createQuery("SELECT n.* FROM notification as n," +
                "havenotification as h WHERE n.id = h.notification AND h.usr = " + "?").defineClass(Notification.class).defineParameters(
                user.getPK()).list();
    }

    /**
     * Function to get the events
     *
     * @param user    user
     * @param current current user logged
     * @return list of the events that the followed users created
     */
    public List<Event> getEvents(User user, User current, Integer limit) throws PiikDatabaseException {

        return new QueryMapper<Event>(super.getConnection()).createQuery(
                "SELECT e.* " +
                        "FROM event as e " +
                        "WHERE e.author IN (SELECT followed FROM followuser WHERE follower = ?) " +
                        "ORDER BY e.date DESC " +
                        "LIMIT ?;"
        ).defineClass(Event.class).defineParameters(user.getId(), limit).list();
    }
}
