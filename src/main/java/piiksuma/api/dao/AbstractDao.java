package piiksuma.api.dao;

import java.sql.Connection;

public class AbstractDao {
    private Connection connection;


    public AbstractDao(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
