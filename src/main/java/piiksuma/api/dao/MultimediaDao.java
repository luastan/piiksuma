package piiksuma.api.dao;

import piiksuma.Multimedia;
import piiksuma.Post;
import piiksuma.api.ErrorMessage;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;
import piiksuma.exceptions.PiikDatabaseException;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class MultimediaDao extends AbstractDao {

    public MultimediaDao(Connection connection) {
        super(connection);
    }

    public void addMultimedia(Multimedia multimedia) throws PiikDatabaseException {
        if (multimedia == null || !multimedia.checkPrimaryKey()) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }

        new InsertionMapper<Multimedia>(super.getConnection()).add(multimedia).defineClass(Multimedia.class).insert();
    }

    /**
     * Function to return the multimedia from the database to check if it exists
     *
     * @param multimedia Given multimedia to check if it is saved in the database
     * @return Returns the multimedia with all it's values if it exists, othwerwise it will return null
     * @throws PiikDatabaseException
     */
    public Multimedia existsMultimedia(Multimedia multimedia) throws PiikDatabaseException {
        if (multimedia == null || !multimedia.checkPrimaryKey()) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }

        return new QueryMapper<Multimedia>(super.getConnection()).createQuery("SELECT * FROM multimedia" +
                "WHERE hash=?").defineClass(Multimedia.class).defineParameters(multimedia.getHash()).findFirst();
    }

    /**
     * Function to count the number of post which contains the given multimedia
     *
     * @param multimedia Multimedia about which we want to know on how many posts is included
     * @return Number of post which contains the multimedia
     */
    public Long numPostMultimedia(Multimedia multimedia) throws PiikDatabaseException {
        if (multimedia == null || !multimedia.checkPrimaryKey()) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }

        List<Map<String, Object>> count = new QueryMapper<Object>(super.getConnection()).createQuery("SELECT COUNT(*) "+
                "as numPostMultimedia FROM post WHERE multimedia = ?").defineClass(Object.class)
                .defineParameters(multimedia.getHash()).mapList();

        return (Long) count.get(0).get("numPostMultimedia");
    }

    public List<Post> postWithMultimedia(Multimedia multimedia) throws PiikDatabaseException {

        if (multimedia == null || !multimedia.checkPrimaryKey()) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }

        return null;
    }

    /**
     * Function to remove the given multimedia from the database
     *
     * @param multimedia Multimedia to remove
     * @throws PiikDatabaseException
     */
    public void removeMultimedia(Multimedia multimedia) throws PiikDatabaseException {

        if (multimedia == null || !multimedia.checkPrimaryKey()) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }

        new DeleteMapper<Multimedia>(super.getConnection()).add(multimedia).defineClass(Multimedia.class).delete();
    }

}
