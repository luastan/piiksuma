package piiksuma.api.dao;

import piiksuma.Message;
import piiksuma.Ticket;
import piiksuma.User;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;
import piiksuma.database.UpdateMapper;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.sql.Connection;
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
        // Check new message
        if (message == null || !message.checkNotNull()) {
            throw new PiikDatabaseException("(newMessage) Primary key constraints failed");
        }

        //We delete the message from the system
        new DeleteMapper<Message>(super.getConnection()).add(message).defineClass(Message.class).delete();
    }

    /**
     * This function replaces the oldMessage saved in the database by the new one because the user wants
     * to modify it or because an admin wants to censor the content
     *
     * @param oldMessage message to be replaced
     * @param newMessage message to be inserted
     */
    public void modifyMessage(Message oldMessage, Message newMessage) throws PiikDatabaseException {

        new UpdateMapper<Message>(super.getConnection()).add(newMessage).defineClass(Message.class).update();

    }

    /**
     * Allows the user to read his messages
     *
     * @param currentUser user in the apliccation
     * @return list of messages for the user
     */
    public List<Message> readMessages(User currentUser) {
        return new QueryMapper<Message>(super.getConnection()).createQuery("SELECT * FROM recievemessage WHERE reciever = ? ").
                defineClass(Message.class).defineParameters(currentUser.getEmail()).list();
    }

    /**
     * º
     * This function sends a private message to another user in the app
     *
     * @param message message to be sent
     */

    public void sendMessage(Message message) throws PiikDatabaseException {

        if (message == null || !message.checkNotNull()) {
            throw new PiikDatabaseException("(privateMessage) Primary key constraints failed");
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
            throw new PiikDatabaseException("(ticket) Primary key constraints failed");
        }

        //  if (!ticket.getUser().equals(currentUser)) {
        //  }

        new InsertionMapper<Ticket>(super.getConnection()).add(ticket);

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
            throw new PiikDatabaseException("(ticket) Primary key constraints failed");
        }

        if (message == null || !message.checkNotNull()) {
            throw new PiikDatabaseException("(message) Primary key constraints failed");
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
            throw new PiikDatabaseException("(ticket) Primary key constraints failed");
        }
    }

    /**
     * This function allows the admins to retrieve the current unresolved tickets
     *
     * @param limit maximum number of tickets to retrieve
     * @return the list of all the tickets which haven't been closed
     */
    public List<Ticket> getAdminTickets(Integer limit) throws PiikInvalidParameters {

        // if (!currentUser.getType().equals(UserType.administrator)) {

        // }

        if (limit == null || limit <= 0) {
            throw new PiikInvalidParameters("(limit) must be greater than 0");
        }

        return new QueryMapper<Ticket>(super.getConnection()).createQuery("SELECT * FROM ticket WHERE deadline is " +
                "NULL LIMIT ?").defineClass(Ticket.class).defineParameters(limit).list();
    }
// =====================================================================================================================
}
