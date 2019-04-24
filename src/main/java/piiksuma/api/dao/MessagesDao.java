package piiksuma.api.dao;

import piiksuma.Message;
import piiksuma.Multimedia;
import piiksuma.Ticket;
import piiksuma.User;
import piiksuma.api.ErrorMessage;
import piiksuma.api.MultimediaType;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;
import piiksuma.database.UpdateMapper;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MessagesDao extends AbstractDao {

    public MessagesDao(Connection connection) {
        super(connection);
    }
//====================================================== Message =======================================================

    /**
     * This function allows deleting a message:
     * - An admin decides to delete it because the sender has used unappropriated words
     * - The sender wants to delete it
     *
     * @param message message to delete
     */
    public void deleteMessage(Message message) throws PiikDatabaseException {

        if (message == null || !message.checkNotNull()) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("message"));
        }

        // We delete the message from the system
        new DeleteMapper<Message>(super.getConnection()).add(message).defineClass(Message.class).delete();
    }

    /**
     * This function replaces the content of a message stored in the database because the user wants to modify it or
     * because an admin wants to censor its content
     *
     * @param newMessage message to be updated
     */
    public void modifyMessage(Message newMessage) throws PiikDatabaseException {

        if (newMessage == null || !newMessage.checkNotNull()) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("newMessage"));
        }

        Multimedia multimedia = newMessage.getMultimedia();

        // If multimedia will be inserted
        boolean multimediaExists = multimedia != null && multimedia.checkNotNull();
        // If ticket will be inserted
        boolean ticketExists = newMessage.getTicket() != null && newMessage.getTicket().checkNotNull();

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
                clause.append("INSERT INTO multimedia(hash, resolution, uri) SELECT '?', '?', '?' WHERE NOT EXISTS" +
                        " (SELECT * FROM multimedia WHERE hash = '?' FOR UPDATE); ");

                String type = multimedia.getType().equals(MultimediaType.image) ? "multimediaImage " :
                        "multimediaVideo ";
                clause.append("INSERT INTO ").append(type).append("SELECT '?' WHERE NOT EXISTS (SELECT * " +
                        "FROM ").append(type).append("WHERE hash = '?' FOR UPDATE); ");
            }

            // TODO date may need to be between ''
            clause.append("UPDATE message SET id = '?', sender = '?', text = '?', date = '?', multimedia = ");

            // Multimedia or ticket may be null
            if (multimediaExists) {
                clause.append("'?'");
            } else {
                clause.append("NULL");
            }

            clause.append(", ticket = ");

            if(ticketExists) {
                clause.append("'?'");
            } else {
                clause.append("NULL");
            }

            clause.append(" WHERE id = '?'");


            statement = con.prepareStatement(clause.toString());


            /* Clause's data insertion */

            int offset = 1;

            if(multimediaExists) {
                statement.setString(1, multimedia.getHash());
                statement.setString(2, multimedia.getResolution());
                statement.setString(3, multimedia.getUri());
                statement.setString(4, multimedia.getHash());

                statement.setString(5, multimedia.getHash());
                statement.setString(6, multimedia.getHash());

                offset += 6;
            }

            statement.setString(offset++, newMessage.getId());
            statement.setString(offset++, newMessage.getSender().getPK());
            statement.setString(offset++, newMessage.getText());
            statement.setTimestamp(offset++, newMessage.getDate());

            if(multimediaExists) {
                statement.setString(offset++, multimedia.getHash());
            }

            if(ticketExists) {
                statement.setString(offset++, newMessage.getTicket().getId());
            }

            statement.setString(offset, newMessage.getId());


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

        if (user == null || !user.checkNotNull()) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        return new QueryMapper<Message>(super.getConnection()).createQuery("SELECT * FROM recievemessage WHERE reciever = ? ").
                defineClass(Message.class).defineParameters(user.getPK()).list();
    }

    /**
     * This function creates a private message to be sent to another users in the social network
     *
     * @param message    message to be sent
     */
    public void createMessage(Message message) throws PiikDatabaseException {

        if (message == null || !message.checkNotNull()) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("message"));
        }

        Multimedia multimedia = message.getMultimedia();

        // If multimedia will be inserted
        boolean multimediaExists = multimedia != null && multimedia.checkNotNull();
        // If ticket will be inserted
        boolean ticketExists = message.getTicket() != null && message.getTicket().checkNotNull();

        // Connection to the database
        Connection con = getConnection();
        // SQL clause
        PreparedStatement statement = null;

        // Built clause
        StringBuilder clause = new StringBuilder();

        try {

            /* Statement */

            // If the message will display some kind of media, it gets inserted if it does not exist in the database
            if (multimediaExists) {
                clause.append("INSERT INTO multimedia(hash, resolution, uri) SELECT '?', '?', '?' WHERE NOT EXISTS" +
                        " (SELECT * FROM multimedia WHERE hash = '?' FOR UPDATE); ");

                String type = multimedia.getType().equals(MultimediaType.image) ? "multimediaImage " :
                        "multimediaVideo ";
                clause.append("INSERT INTO ").append(type).append("SELECT '?' WHERE NOT EXISTS (SELECT * " +
                        "FROM ").append(type).append("WHERE hash = '?' FOR UPDATE); ");
            }

            // TODO date may need to be between ''
            clause.append("INSERT INTO message(id, sender, text, date, multimedia, ticket) VALUES('?', '?', '?', ?");

            // Multimedia or ticket may be null
            if (multimediaExists) {
                clause.append(", '?'");
            } else {
                clause.append(", NULL");
            }

            if(ticketExists) {
                clause.append(", '?') ");
            } else {
                clause.append(", NULL) ");
            }


            statement = con.prepareStatement(clause.toString());


            /* Clause's data insertion */

            int offset = 1;

            if(multimediaExists) {
                statement.setString(1, multimedia.getHash());
                statement.setString(2, multimedia.getResolution());
                statement.setString(3, multimedia.getUri());
                statement.setString(4, multimedia.getHash());

                statement.setString(5, multimedia.getHash());
                statement.setString(6, multimedia.getHash());

                offset += 6;
            }

            statement.setString(offset++, message.getId());
            statement.setString(offset++, message.getSender().getPK());
            statement.setString(offset++, message.getText());
            statement.setTimestamp(offset++, message.getDate());

            if(multimediaExists) {
                statement.setString(offset++, multimedia.getHash());
            }

            if(ticketExists) {
                statement.setString(offset, message.getTicket().getId());
            }


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
// =====================================================================================================================

// ============================================ Tickets ================================================================

    /**
     * A new ticket, created by a user, is inserted into the database
     *
     * @param ticket ticket to insert
     */
    public void newTicket(Ticket ticket) throws PiikDatabaseException {

        if (ticket == null || !ticket.checkNotNull()) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("ticket"));
        }

        new InsertionMapper<Ticket>(super.getConnection()).add(ticket).defineClass(Ticket.class).insert();
    }

    /**
     * An user replies a ticket by creating a message that will be associated to it; therefore, message.getTicket()
     * cannot be null
     *
     * @param ticket  the ticket is not necessary actually, we just have to check if the ticket is in the message
     * @param message reply to be added to the ticket
     */
    public void replyTicket(Ticket ticket, Message message) throws PiikDatabaseException {

        if (ticket == null || !ticket.checkNotNull()) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("ticket"));
        }

        if (message == null || !message.checkNotNull()) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("message"));
        }

        if (!ticket.getId().equals(message.getTicket())) {
            return;
        }

        new InsertionMapper<Message>(super.getConnection()).add(message).defineClass(Message.class).insert();
    }

    /**
     * The admin closes a ticket, marking it as "resolved"
     *
     * @param ticket the ticket which is going to be closed
     */

    public void closeTicket(Ticket ticket) throws PiikDatabaseException {

        if (ticket == null || !ticket.checkNotNull()) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("ticket"));
        }

        new UpdateMapper<Ticket>(super.getConnection()).createUpdate("UPDATE ticket SET closeDate = NOW() WHERE id = " +
                "?").defineParameters(ticket.getId()).executeUpdate();
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
