package piiksuma.api.dao;

import piiksuma.Message;
import piiksuma.Ticket;
import piiksuma.User;
import piiksuma.UserType;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;
import piiksuma.database.UpdateMapper;

import java.sql.Connection;
import java.util.List;

public class MessagesDao extends AbstractDao {

    public MessagesDao(Connection connection) {
        super(connection);
    }

    /**
     * This function allows deleting a message:
     *  - An admin decides to delete it because the sender has used unappropriated words
     *  - The sender wants to delete it
     *
     * @param message     message to delete
     * @param currentUser current user logged into the app
     */
    public void deleteMessage(Message message, User currentUser) {
        if (message == null) {
            return;
        }
        if (currentUser == null) {
            return;
        }

        if (currentUser.getType().equals(UserType.administrator)) {
            return;
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
     * @param currentUser current user logged into the app
     */
    public void modifyMessage(Message oldMessage, Message newMessage, User currentUser) {

        if (currentUser.getEmail().compareTo(oldMessage.getSender()) == 0) {
            new UpdateMapper<Message>(super.getConnection()).add(newMessage).defineClass(Message.class).update();
        }
    }

    /**
     * This function sends a private message to another user in the app
     *
     * @param privateMessage message to be sent
     * @param currentUser current user logged into the app
     */

    public void sendMessage(Message privateMessage, User currentUser) {

    }

    /**
     * A new ticket, created by a user, is inserted into the database
     *
     * @param ticket      ticket to insert
     * @param currentUser current user logged
     */
    public void newTicket(Ticket ticket, User currentUser) {

        if (ticket == null) {
            //TODO handle with exceptions
            return;
        }

        if (!ticket.checkPrimaryKey()) {
            return;
        }

        if (!ticket.getUser().equals(currentUser)){
            return;
        }

        new InsertionMapper<Ticket>(super.getConnection()).add(ticket);

    }

    /**
     * An user replies a ticket by creating a message that will be associated to it; therefore, message.getTicket()
     * cannot be null
     *
     * @param ticket      the ticket is not necessary actually, we just have to check if the ticket is in the message
     * @param message     reply to be added to the ticket
     * @param currentUser current user logged into the app
     */
    public void replyTicket(Ticket ticket, Message message, User currentUser) {

        if (message == null) {
            return;
        }
        if (!message.checkPrimaryKey() || message.getTicket() == null) {
            return;
        }
        if(!ticket.getId().equals(message.getTicket())){
            return;
        }

        new InsertionMapper<Message>(super.getConnection()).add(message).defineClass(Message.class).insert();
    }

    /**
     * The admin closes, marking it as "resolved"
     *
     * @param ticket      the ticket which is going to be closed
     * @param currentUser current user logged into the app
     */

    public void closeTicket(Ticket ticket, User currentUser) {

    }

    /**
     * This function allows the admins to retrieve the current unresolved tickets
     *
     * @param currentUser current user logged in the app
     * @return the list of all the tickets which haven't been closed
     */
    public List<Ticket> getAdminTickets(User currentUser) {

        if (!currentUser.getType().equals(UserType.administrator)){
            return null;
        }

        return new QueryMapper<Ticket>(super.getConnection()).createQuery("SELECT * FROM ticket WHERE deadline is NULL").defineClass(Ticket.class).list();
    }
}
