package piiksuma.api.dao;

import piiksuma.Multimedia;
import piiksuma.Post;
import piiksuma.User;

import java.sql.Connection;
import java.util.List;

public class MultimediaDao extends AbstractDao{

    public MultimediaDao(Connection connection) {
        super(connection);
    }

    public Multimedia addMultimedia(Multimedia multimedia, User current){
        return null;
    }

    public Multimedia existsMultimedia(Multimedia multimedia, User current){
        return null;
    }

    public Integer numPostMultimedia(Multimedia multimedia, User current){
        return null;
    }

    public List<Post> postWithMultimedia(Multimedia multimedia, User current){
        return null;
    }

    public void removeMultimedia(Multimedia multimedia, User current){

    }

}
