package piiksuma.api.dao;

import piiksuma.Message;
import piiksuma.Ticket;
import piiksuma.User;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;

import java.sql.Connection;
import java.util.List;

public class MessagesDao extends AbstractDao {

    public MessagesDao(Connection connection) {
        super(connection);
    }

    public void deleteMessage(Message privateMessage, User currentUser) {

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
    public Ticket newTicket(Ticket ticket, User currentUser) {

        if (ticket == null) {
            //TODO handle with exceptions
            return null;
        }

        new InsertionMapper<Ticket>(super.getConnection()).add(ticket);

        Ticket ticketInserted = new QueryMapper<Ticket>(super.getConnection()).crearConsulta(" SELECT * FROM ticket WHERE id=?").definirEntidad(Ticket.class).definirParametros(ticket.getId()).findFirst();

        return ticketInserted;
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

        //We check for the tickets which haven´t been already closed
        List<Ticket> tickets = new QueryMapper<Ticket>(super.getConnection()).crearConsulta("SELECT * FROM ticket WHERE deadline is NULL").definirEntidad(Ticket.class).list();

        return tickets;
    }
}
