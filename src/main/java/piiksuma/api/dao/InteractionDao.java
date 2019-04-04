package piiksuma.api.dao;

import piiksuma.ReactionType;
import piiksuma.User;

import java.sql.Connection;
import java.util.HashMap;

public class InteractionDao extends AbstractDao {
    public InteractionDao(Connection connection) {
        super(connection);
    }

    public void removeEvent(Event e, User current){

    }

    public void removeReaction(Reaction reaction, User current){

    }

    public Event updateEvent(Event event, User current){
        return null;
    }

    public void react(Reaction reaction, User current){

    }

    public Event createEvent(Event event, User current){
        return null;
    }

    public List<Reaction> getPostReaction(Post post){
        return null;
    }

    public HashMap<ReactionType, Integer> getPostReaction(Post post, User current){
        return null;
    }
}
