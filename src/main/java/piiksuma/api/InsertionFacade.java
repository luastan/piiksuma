package piiksuma.api;

import piiksuma.User;
import piiksuma.exceptions.PiikDatabaseException;

import java.sql.Connection;

public class InsertionFacade {
    private ApiFacade parentFacade;

    public InsertionFacade(ApiFacade parentFacade) {
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


    /**
     * Adds a new user to the database
     *
     * @param newUser User to be inserted into the database
     * @throws PiikDatabaseException User already exists or It has invalid
     *                               parameters such as null values or non unique values on primary keys
     */
    public void createUser(User newUser) throws PiikDatabaseException {

    }



    /* MLTIMEDIA related methods */



    /* POST related methods */



    /* MESSAGE related methods */



    /* INTERACTION related methods */

}
