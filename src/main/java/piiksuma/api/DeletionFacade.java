package piiksuma.api;

import piiksuma.*;

import java.sql.Connection;

public class DeletionFacade {
    private ApiFacade parentFacade;

    public DeletionFacade(ApiFacade parentFacade) {
        this.parentFacade = parentFacade;
    }

    // Getters and Setters are used in Test methods
    public Connection getConnection() {
        return parentFacade.getConnection();
    }

    public void setConnection(Connection connection) {
        parentFacade.setConnection(connection);
    }


    /* USER related methods */

    public void removeUser(User user, User currentUser){

    }

    /* MLTIMEDIA related methods */

    public void removeMultimedia(Multimedia multimedia, User currentUser){

    }

    /* POST related methods */

    public void removePost(Post post, User currentUser){

    }

    public void removeRePost(Post repost, User currentUser){

    }

    /* MESSAGE related methods */

    public void deleteMessage(Message message, User currentUser){

    }

    /* INTERACTION related methods */

    public void removeEvent(Event e, User current) {

    }

    public void removeReaction(Reaction reaction, User current) {

    }

}
