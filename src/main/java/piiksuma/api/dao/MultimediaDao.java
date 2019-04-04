package piiksuma.api.dao;

import java.sql.Connection;

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
        return null;
    }

}
