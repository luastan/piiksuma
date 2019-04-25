package piiksuma.api.dao;

import javafx.scene.image.Image;
import piiksuma.Multimedia;
import piiksuma.Post;
import piiksuma.api.ErrorMessage;
import piiksuma.api.MultimediaType;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MultimediaDao extends AbstractDao {

    public MultimediaDao(Connection connection) {
        super(connection);
    }

    public void addMultimedia(Multimedia multimedia) throws PiikDatabaseException {
        if (multimedia == null || !multimedia.checkPrimaryKey(true)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }

        // Connection to the database
        Connection con = getConnection();
        // SQL clause
        PreparedStatement statement = null;

        // Built clause
        StringBuilder clause = new StringBuilder();

        try {

            /* Statement */

            clause.append("INSERT INTO multimedia(hash, resolution, uri) SELECT ?, ?, ? WHERE NOT EXISTS" +
                    " (SELECT * FROM multimedia WHERE hash = ? FOR UPDATE); ");

            String type = multimedia.getType().equals(MultimediaType.image) ? "multimediaImage " :
                    "multimediaVideo ";
            clause.append("INSERT INTO ").append(type).append("SELECT ? WHERE NOT EXISTS (SELECT * " +
                    "FROM ").append(type).append("WHERE hash = ? FOR UPDATE); ");


            statement = con.prepareStatement(clause.toString());


            /* Clause's data insertion */

            statement.setString(1, multimedia.getHash());
            statement.setString(2, multimedia.getResolution());
            statement.setString(3, multimedia.getUri());
            statement.setString(4, multimedia.getHash());

            statement.setString(5, multimedia.getHash());
            statement.setString(6, multimedia.getHash());


            /* Execution */

            statement.executeUpdate();


        } catch (SQLException e) {
            throw new PiikDatabaseException(e.getMessage());

        } finally {

            try {
                if (statement != null) {
                    statement.close();
                }

            } catch (SQLException e) {
                throw new PiikDatabaseException(e.getMessage());
            }
        }
    }
    
    /**
     * Function to return the multimedia from the database
     *
     * @param multimedia Given multimedia to search into database
     * @return Returns the multimedia with all it's values if it exists, othwerwise it will return null
     * @throws PiikDatabaseException
     */
    public Multimedia getMultimedia(Multimedia multimedia) throws PiikDatabaseException {
        if (multimedia == null || !multimedia.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }

        return new QueryMapper<Multimedia>(super.getConnection()).createQuery("SELECT * FROM multimedia" +
                "WHERE hash=?").defineClass(Multimedia.class).defineParameters(multimedia.getPK()).findFirst();
    }

    /**
     * Function to return the multimedia from the database to check if it exists
     *
     * @param multimedia Given multimedia to check if it is saved in the database
     * @return Returns the multimedia with all it's values if it exists, othwerwise it will return null
     * @throws PiikDatabaseException
     */
    public boolean existsMultimedia(Multimedia multimedia) throws PiikDatabaseException {
        return(getMultimedia(multimedia) != null);
    }

    /**
     * Function to count the number of post which contains the given multimedia
     *
     * @param multimedia Multimedia about which we want to know on how many posts is included
     * @return Number of post which contains the multimedia
     */
    public Long numPostMultimedia(Multimedia multimedia) throws PiikDatabaseException {
        if (multimedia == null || !multimedia.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }

        List<Map<String, Object>> count = new QueryMapper<>(super.getConnection()).createQuery("SELECT COUNT(*) "+
                "as numPostMultimedia FROM post WHERE multimedia = ?").defineClass(Object.class)
                .defineParameters(multimedia.getPK()).mapList();

        return (Long) count.get(0).get("numPostMultimedia");
    }

    public List<Post> postWithMultimedia(Multimedia multimedia) throws PiikDatabaseException {

        if (multimedia == null || !multimedia.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }

        return new QueryMapper<Post>(getConnection()).defineClass(Post.class).createQuery(
                "SELECT * FROM post WHERE multimedia = ?").defineParameters(multimedia.getPK()).list();
    }

    /**
     * Function to remove the given multimedia from the database
     *
     * @param multimedia Multimedia to remove
     * @throws PiikDatabaseException
     */
    public void removeMultimedia(Multimedia multimedia) throws PiikDatabaseException {

        if (multimedia == null || !multimedia.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }

        new DeleteMapper<Multimedia>(super.getConnection()).add(multimedia).defineClass(Multimedia.class).delete();
    }

    /**
     * Function to get the image of the multimedia
     *
     * @param multimedia Multimedia that contains the information of the image
     * @return the image of the multimedia
     * @throws PiikDatabaseException
     */
    public Image getImage(Multimedia multimedia) throws PiikDatabaseException, PiikInvalidParameters {
        if(multimedia == null || !multimedia.checkPrimaryKey(false)){
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }

        if(multimedia.getType() != MultimediaType.image){
            throw new PiikInvalidParameters("The multimedia type is not 'photo'");
        }

        return new Image(getClass().getResource("/imagenes/" + multimedia.getUri()).toString());
    }

}
