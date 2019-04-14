package piiksuma.api.dao;

import piiksuma.Multimedia;
import piiksuma.Post;
import piiksuma.User;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.exceptions.PiikDatabaseException;

import java.sql.Connection;
import java.util.List;

public class MultimediaDao extends AbstractDao{

    public MultimediaDao(Connection connection) {
        super(connection);
    }

    public void addMultimedia(Multimedia multimedia) throws PiikDatabaseException {
        if (multimedia == null || !multimedia.checkPrimaryKey()) {
            throw new PiikDatabaseException("(multimedia) Primary key constraints failed");
        }

        new InsertionMapper<Multimedia>(super.getConnection()).add(multimedia).defineClass(Multimedia.class).insert();
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
        if (multimedia == null || !multimedia.checkPrimaryKey()) {
            throw new PiikDatabaseException("(multimedia) Primary key constraints failed");
        }

        new DeleteMapper<Multimedia>(super.getConnection()).add(multimedia).defineClass(Multimedia.class).delete();
    }

}
