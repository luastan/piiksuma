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

//===================================================== MULTIMEDIA =====================================================

    /*******************************************************************************************************************
     * Add a new multimedia content to the db
     *
     * @param multimedia content to add
     * @throws PiikDatabaseException Thrown if multimedia or its primary key are null
     */
    public void addMultimedia(Multimedia multimedia) throws PiikDatabaseException {
        // Check if multimedia or its primary key is null
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

            /* Isolation level */

            // Default in PostgreSQL
            super.getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);


            /* Statement */

            clause.append("INSERT INTO multimedia(hash, resolution, uri) SELECT ?, ?, ? WHERE NOT EXISTS" +
                    " (SELECT * FROM multimedia WHERE hash = ? FOR UPDATE); ");

            String type = multimedia.getType().equals(MultimediaType.image) ? "multimediaimage " :
                    "multimediavideo ";
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
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Function to remove the given multimedia from the database
     *
     * @param multimedia Multimedia to remove
     * @throws PiikDatabaseException Thrown if multimedia or its primary key are null
     */
    public void removeMultimedia(Multimedia multimedia) throws PiikDatabaseException {
        // Check if multimedia or its primary key is null
        if (multimedia == null || !multimedia.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }
        // Delete multimedia
        new DeleteMapper<Multimedia>(super.getConnection()).add(multimedia).defineClass(Multimedia.class).delete();
    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Function to return the multimedia from the database
     *
     * @param multimedia Given multimedia to search into database
     * @return Returns the multimedia with all it's values if it exists, othwerwise it will return null
     * @throws PiikDatabaseException Thrown if multimedia or its primary key are null
     */
    public Multimedia getMultimedia(Multimedia multimedia) throws PiikDatabaseException {
        // Check if multimedia or its primary key is null
        if (multimedia == null || !multimedia.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }

        // Return multimedia
        return new QueryMapper<Multimedia>(super.getConnection()).createQuery("SELECT * FROM multimedia " +
                "WHERE hash=?").defineClass(Multimedia.class).defineParameters(multimedia.getPK()).findFirst();
    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Check if multimedia exists on the db
     *
     * @param multimedia Multimedia to be checked
     * @return Returns the multimedia if it exists, othwerwise it will return null
     * @throws PiikDatabaseException Thrown if multimedia or its primary key are null
     */
    public boolean existsMultimedia(Multimedia multimedia) throws PiikDatabaseException {
        // Check if multimedia or its primary key is null
        if (multimedia == null || !multimedia.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }

        // Return multimedia
        return (getMultimedia(multimedia) != null);
    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Count the number of post which contains the given multimedia
     *
     * @param multimedia Multimedia we want to count
     * @return Number of post containing multimedia
     * @throws PiikDatabaseException Thrown if multimedia or its primary key are null
     */
    public Long numPostMultimedia(Multimedia multimedia) throws PiikDatabaseException {
        // Check if multimedia or its primary key is null
        if (multimedia == null || !multimedia.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }

        List<Map<String, Object>> count = new QueryMapper<>(super.getConnection()).createQuery("SELECT COUNT(*) " +
                "as numPostMultimedia FROM post WHERE multimedia = ?").defineClass(Object.class)
                .defineParameters(multimedia.getPK()).mapList();
        // Return count
        return (Long) count.get(0).get("numPostMultimedia");
    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Gets all post containing multimedia
     *
     * @param multimedia Multimedia we are looking for on posts
     * @return List of post containing multimedia
     * @throws PiikDatabaseException Thrown if multimedia or its primary key are null
     */
    public List<Post> postWithMultimedia(Multimedia multimedia) throws PiikDatabaseException {
        // Check if multimedia or its primary key is null
        if (multimedia == null || !multimedia.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }

        // Return list of posts
        return new QueryMapper<Post>(getConnection()).defineClass(Post.class).createQuery(
                "SELECT * FROM post WHERE multimedia = ?").defineParameters(multimedia.getPK()).list();
    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Get the image from multimedia
     *
     * @param multimedia Multimedia that contains the information of the image
     * @return the image of the multimedia
     * @throws PiikDatabaseException Thrown if multimedia or its primary key are null
     * @throws PiikInvalidParameters Thrown if the type of multimedia isn't an image
     */
    public Image getImage(Multimedia multimedia) throws PiikDatabaseException, PiikInvalidParameters {
        // Check if multimedia or its primary key is null
        if (multimedia == null || !multimedia.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("multimedia"));
        }
        // Check if the type of multimedia is image
        if (multimedia.getType() != MultimediaType.image) {
            throw new PiikInvalidParameters("The multimedia type is not 'photo'");
        }
        // Return the image
        return new Image(getClass().getResource("/imagenes/" + multimedia.getUri()).toString());
    }
    //******************************************************************************************************************
//======================================================================================================================
}
