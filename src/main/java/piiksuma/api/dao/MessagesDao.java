package piiksuma.api.dao;

import piiksuma.Message;
import piiksuma.Ticket;
import piiksuma.User;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;

import java.sql.Connection;
import java.util.List;

public class MessagesDao extends AbstractDao {

    public MessagesDao(Connection connection) {
        super(connection);
    }

    /**
     * This function allows to delete a message by an admin because the sender used unapropiated words or
     * by the sender because he wants to do it
     * @param message message to delete
     * @param currentUser current user logged in the app
     */
    public void deleteMessage(Message message, User currentUser) {
        if(message==null){
            return;
        }
        if(currentUser==null){
            return;
        }
        //We delete the message from the system
        new DeleteMapper<Message>(super.getConnection()).add(message).defineClass(Message.class).delete();
    }

    public void modifyMessage(Message privateMessage, User currentUser) {

    }

    public void sendMessage(Message privateMessage, User currentUser) {

    }

    /**
     *
     * @param ticket ticket to insert
     * @param currentUser current user logged
     * @return the ticket which has been inserted
     */
    public void newTicket(Ticket ticket, User currentUser) {

        if (ticket == null) {
            //TODO handle with exceptions
            return;
        }

        if(!ticket.checkPrimaryKey()){
            return;
        }

        new InsertionMapper<Ticket>(super.getConnection()).add(ticket);

    }

    public void replyTicket(Ticket ticket, User currentUser) {

        //TODO para insertar en ticketAnswer con el mapper no se como hacerlo
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

        return new QueryMapper<Ticket>(super.getConnection()).createQuery("SELECT * FROM ticket WHERE deadline is NULL").defineClass(Ticket.class).list();
    }
}
