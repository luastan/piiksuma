package piiksuma.api.dao;

import piiksuma.Usuario;

import java.sql.Connection;

public class MessagesDao extends AbstractDao {

    public MessagesDao(Connection connection) {
        super(connection);
    }

    public void deleteMessage(Message privateMessage, Usuario currentUser) {

    }

    public void modifyMessage(Message privateMessage, Usuario currentUser) {

    }

    public void sendMessage(Message privateMessage, Usuario currentUser) {

    }

    public Ticket newTicket(Ticket ticker, Usuario currentUser) {

    }

    public void replyTicket(Ticket ticker, Usuario currentUser) {

    }

    public void closeTicket(Ticket ticket, Usuario currentUser) {

    }

}
