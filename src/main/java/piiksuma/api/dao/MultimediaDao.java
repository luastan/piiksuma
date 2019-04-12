package piiksuma.api.dao;

import piiksuma.Multimedia;
import piiksuma.Post;
import piiksuma.User;
import piiksuma.exceptions.PiikDatabaseException;

import java.sql.Connection;
import java.util.List;

public class MultimediaDao extends AbstractDao{

    public MultimediaDao(Connection connection) {
        super(connection);
    }

    public Multimedia addMultimedia(Multimedia multimedia){
        return null;
    }

    public Multimedia existsMultimedia(Multimedia multimedia){
        return null;
    }

    public Integer numPostMultimedia(Multimedia multimedia){
        return null;
    }

    public List<Post> postWithMultimedia(Multimedia multimedia){
        return null;
    }

    public void removeMultimedia(Multimedia multimedia) throws PiikDatabaseException {

    }

}
