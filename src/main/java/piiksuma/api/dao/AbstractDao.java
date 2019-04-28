package piiksuma.api.dao;

import java.sql.Connection;

public class AbstractDao {
    private java.sql.Connection connection;


    /*Constructor*/

    public AbstractDao(Connection connection) {
        this.connection = connection;
    }

    /*Methods*/

    /**
     * This function connects the app to the database
     *
     * @return the connection to the database
     */
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
