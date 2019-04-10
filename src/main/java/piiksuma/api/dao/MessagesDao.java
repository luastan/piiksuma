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
     * This function allows to delete a message by an admin because the sender used unappropiated words or
     * by the sender because he wants to do it
     *
     * @param message     message to delete
     * @param currentUser current user logged in the app
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

    public void modifyMessage(Message oldMessage, Message newMessage, User currentUser) {

        if (currentUser.getEmail().compareTo(oldMessage.getSender()) == 0) {
            new UpdateMapper<Message>(super.getConnection()).add(newMessage).defineClass(Message.class).update();
        }
    }

    public void sendMessage(Message privateMessage, User currentUser) {

    }

    /**
     * A new ticket, created by a user, is insersted into the database
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
     * The admin replies a ticket which has to be on the message, it means that message.getTicket() cant be null
     *
     * @param ticket      the ticket is not necessary actually, we have to check if the ticket is in the message          el ticket no haría falta la verdad, si ya lo tenemos en mensaje hay que mirarlo
     * @param message     reply from the admin to the user who create the ticket
     * @param currentUser current user loged in the app
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

    public void closeTicket(Ticket ticket, User currentUser) {

    }

    /**
     * This function is for giving the admins the tickets to reply
     *
     * @param currentUser current user logged in the app
     * @return the list of all the tickets which havent been closed
     */
    public List<Ticket> getAdminTickets(User currentUser) {

        if (!currentUser.getType().equals(UserType.administrator)){
            return null;
        }

        return new QueryMapper<Ticket>(super.getConnection()).createQuery("SELECT * FROM ticket WHERE deadline is NULL").defineClass(Ticket.class).list();
    }
}
