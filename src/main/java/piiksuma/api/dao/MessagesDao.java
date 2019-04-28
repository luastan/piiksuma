package piiksuma.api.dao;

import piiksuma.Message;
import piiksuma.Multimedia;
import piiksuma.Ticket;
import piiksuma.User;
import piiksuma.api.ErrorMessage;
import piiksuma.api.MultimediaType;
import piiksuma.database.DeleteMapper;
import piiksuma.database.QueryMapper;
import piiksuma.database.UpdateMapper;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.sql.*;
import java.util.List;

public class MessagesDao extends AbstractDao {

    public MessagesDao(Connection connection) {
        super(connection);
    }
//====================================================== Message =======================================================

    /*******************************************************************************************************************
     * Delete a message you sent or a not allowed message checked by the admin
     *
     * @param message Message to delete
     * @throws PiikDatabaseException Thrown if message or its primary key are null
     */
    public void deleteMessage(Message message) throws PiikDatabaseException {
        // Check if event or primary key are null
        if (message == null || !message.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("Message"));
        }

        // We delete the message from the system
        new DeleteMapper<Message>(super.getConnection()).add(message).defineClass(Message.class).delete();
    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Replace the content of a message that the user wants to modify or the admin decides to censor
     *
     * @param message message to be updated
     * @throws PiikDatabaseException Thrown if message or its primary key are null
     */
    public void modifyMessage(Message message) throws PiikDatabaseException {
        // Check if event or primary key are null
        if (message == null || !message.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("Message"));
        }

        Multimedia multimedia = message.getMultimedia();

        // If multimedia will be inserted
        boolean multimediaExists = multimedia != null && multimedia.checkPrimaryKey(false);
        // If ticket will be inserted
        boolean ticketExists = message.getTicket() != null && message.getTicket().checkPrimaryKey(false);

        // Connection to the database
        Connection con = getConnection();
        // SQL clause
        PreparedStatement statement = null;

        // Built clause
        StringBuilder clause = new StringBuilder();

        try {

            /* Statement */

            // If the message will display some kind of media, it gets inserted if it does not exist in the database
            // (we can't be sure that the app hasn't given us the same old multimedia or a new one)
            if (multimediaExists) {
                clause.append("INSERT INTO multimedia(hash, resolution, uri) SELECT ?, ?, ? WHERE NOT EXISTS" +
                        " (SELECT * FROM multimedia WHERE hash = ? FOR UPDATE); ");

                String type = multimedia.getType().equals(MultimediaType.image) ? "multimediaimage " :
                        "multimediavideo ";
                clause.append("INSERT INTO ").append(type).append("SELECT ? WHERE NOT EXISTS (SELECT * " +
                        "FROM ").append(type).append("WHERE hash = ? FOR UPDATE); ");
            }

            clause.append("UPDATE message SET text = ?, date = ?, multimedia = ");

            // Multimedia or ticket may be null
            if (multimediaExists) {
                clause.append("?");
            } else {
                clause.append("NULL");
            }

            clause.append(", ticket = ");

            if (ticketExists) {
                clause.append("?");
            } else {
                clause.append("NULL");
            }

            clause.append(" WHERE id = ? AND author = ?");


            statement = con.prepareStatement(clause.toString());


            /* Clause's data insertion */

            int offset = 1;

            if (multimediaExists) {
                statement.setString(1, multimedia.getHash());
                statement.setString(2, multimedia.getResolution());
                statement.setString(3, multimedia.getUri());
                statement.setString(4, multimedia.getHash());

                statement.setString(5, multimedia.getHash());
                statement.setString(6, multimedia.getHash());

                offset += 6;
            }

            statement.setString(offset++, message.getText());
            statement.setTimestamp(offset++, message.getDate());

            if (multimediaExists) {
                statement.setString(offset++, multimedia.getHash());
            }

            if (ticketExists) {
                statement.setInt(offset++, message.getTicket().getId());
            }

            statement.setString(offset++, message.getId());
            statement.setString(offset, message.getSender().getPK());


            /* Execution */

            statement.executeUpdate();

        } catch (SQLException e) {
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
    }

    /**
     * Allows the user to read his messages
     *
     * @param user user whose messages will be retrieved
     * @return list of messages for the user
     */
    public List<Message> readMessages(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        return new QueryMapper<Message>(super.getConnection()).createQuery("SELECT * FROM recievemessage WHERE " +
                "receiver = ? ").defineClass(Message.class).defineParameters(user.getPK()).list();
    }

    /**
     * This function creates a private message to be sent to another users in the social network
     *
     * @param message message to be sent
     * @return message containing the given data and its generated ID
     */
    public Message createMessage(Message message) throws PiikDatabaseException {

        if (message == null || !message.checkPrimaryKey(true)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("message"));
        }

        // It will be returned when the method executes successfully
        Message completeMessage = new Message(message);

        Multimedia multimedia = message.getMultimedia();

        // If multimedia will be inserted
        boolean multimediaExists = multimedia != null && multimedia.checkPrimaryKey(false);
        // If ticket will be inserted
        boolean ticketExists = message.getTicket() != null && message.getTicket().checkPrimaryKey(false);

        // Connection to the database
        Connection con = getConnection();
        // SQL clause
        PreparedStatement statement = null;

        // Built clause
        StringBuilder clause = new StringBuilder();

        try {

            /* Auto-commit */

            // The post won't be created unless there's no error modifying all related tables and generating its ID
            con.setAutoCommit(false);


            /* Statement */

            // If the message will display some kind of media, it gets inserted if it does not exist in the database
            if (multimediaExists) {
                clause.append("INSERT INTO multimedia(hash, resolution, uri) SELECT ?, ?, ? WHERE NOT EXISTS" +
                        " (SELECT * FROM multimedia WHERE hash = ? FOR UPDATE); ");

                String type = multimedia.getType().equals(MultimediaType.image) ? "multimediaimage " :
                        "multimediavideo ";
                clause.append("INSERT INTO ").append(type).append("SELECT ? WHERE NOT EXISTS (SELECT * " +
                        "FROM ").append(type).append("WHERE hash = ? FOR UPDATE); ");
            }

            // ID is autogenerated
            clause.append("INSERT INTO message(author, text, date, multimedia, ticket) VALUES(?, ?, ?");

            // Multimedia or ticket may be null
            if (multimediaExists) {
                clause.append(", ?");
            } else {
                clause.append(", NULL");
            }

            if (ticketExists) {
                clause.append(", ?) ");
            } else {
                clause.append(", NULL) ");
            }

            clause.append("RETURNING id");


            statement = con.prepareStatement(clause.toString());


            /* Clause's data insertion */

            int offset = 1;

            if (multimediaExists) {
                statement.setString(1, multimedia.getHash());
                statement.setString(2, multimedia.getResolution());
                statement.setString(3, multimedia.getUri());
                statement.setString(4, multimedia.getHash());

                statement.setString(5, multimedia.getHash());
                statement.setString(6, multimedia.getHash());

                offset += 6;
            }

            statement.setString(offset++, message.getSender().getPK());
            statement.setString(offset++, message.getText());
            statement.setTimestamp(offset++, message.getDate());

            if (multimediaExists) {
                statement.setString(offset++, multimedia.getHash());
            }

            if (ticketExists) {
                statement.setInt(offset, message.getTicket().getId());
            }


            /* Execution and key retrieval */

            ResultSet keys = statement.executeQuery();

            // ID generation successful
            if (keys.next()) {
                completeMessage.setId(keys.getString("id"));
            } else {
                throw new PiikDatabaseException("Message ID generation failed");
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

        return completeMessage;
    }
// =====================================================================================================================

// ============================================ Tickets ================================================================

    /**
     * A new ticket, created by a user, is inserted into the database
     *
     * @param ticket ticket to insert
     * @return ticket containing the given data and its generated ID
     */
    public Ticket newTicket(Ticket ticket) throws PiikDatabaseException {

        if (ticket == null || !ticket.checkPrimaryKey(true)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("ticket"));
        }

        // It will be returned when the method executes successfully
        Ticket completeTicket = new Ticket(ticket);

        // Connection to the database
        Connection con = getConnection();
        // SQL clause
        PreparedStatement statement = null;

        // Built clause
        StringBuilder clause = new StringBuilder();

        try {

            /* Auto-commit */

            // The ticket won't be created unless there's no error generating its ID
            con.setAutoCommit(false);


            /* Statement */

            // "closeDate" and "adminClosing" are inserted as NULL because they are intended to be stored when closing
            // a ticket; "creationDate" has "NOW()" as the default value; ticket's IDs autoincrement as each ticket is
            // created in the database
            clause.append("INSERT INTO ticket(usr, section, text, closedate, adminclosing) VALUES (?, ?, ?, NULL, " +
                    "NULL) RETURNING id");

            statement = con.prepareStatement(clause.toString());


            /* Clause's data insertion */

            statement.setString(1, ticket.getUser().getPK());
            statement.setString(2, ticket.getSection());
            statement.setString(3, ticket.getTextProblem());


            /* Execution and key retrieval */

            ResultSet keys = statement.executeQuery();

            // ID generation successful
            if (keys.next()) {
                completeTicket.setId(keys.getInt("id"));
            } else {
                throw new PiikDatabaseException("Ticket ID generation failed");
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

        return (completeTicket);
    }

    /**
     * The admin closes a ticket, marking it as "resolved"
     *
     * @param ticket the ticket which is going to be closed
     */

    public void closeTicket(Ticket ticket) throws PiikDatabaseException {

        if (ticket == null || !ticket.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("ticket"));
        }

        new UpdateMapper<Ticket>(super.getConnection()).createUpdate("UPDATE ticket SET closedate = NOW(), " +
                "admindlosing = ? WHERE id = ?").defineParameters(ticket.getAdminClosing().getPK(),
                ticket.getId()).executeUpdate();
    }

    /**
     * This function allows the admins to retrieve the current unresolved tickets
     *
     * @param limit maximum number of tickets to retrieve
     * @return the list of all the tickets which haven't been closed
     */
    public List<Ticket> getAdminTickets(Integer limit) throws PiikDatabaseException, PiikInvalidParameters {

        if (limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }

        return new QueryMapper<Ticket>(super.getConnection()).createQuery("SELECT * FROM ticket WHERE deadline is " +
                "NULL LIMIT ?").defineClass(Ticket.class).defineParameters(limit).list();
    }
// =====================================================================================================================
}
