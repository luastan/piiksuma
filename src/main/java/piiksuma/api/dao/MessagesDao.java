package piiksuma.api.dao;

import piiksuma.Ticket;
import piiksuma.User;

import java.sql.Connection;

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

    public Ticket newTicket(Ticket ticker, User currentUser) {

    }

    public void replyTicket(Ticket ticker, User currentUser) {

    }

    public void closeTicket(Ticket ticket, User currentUser) {

    }

}
