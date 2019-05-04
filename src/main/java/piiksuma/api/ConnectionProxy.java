package piiksuma.api;

import piiksuma.Utilities.PiikLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Proxy around the Database connection. It creates the connection with the database
 */
public class ConnectionProxy {
    private Connection connection;


    /**
     * Basic and unique constructor. Instantiates the connection reading the parameters from a .properties file
     *
     * @param connectionPath Path where the properties file is located
     */
    public ConnectionProxy(String connectionPath) {
        Properties config = new Properties();
        Properties userProperties = new Properties();
        FileInputStream confFile;
        try {
            confFile = new FileInputStream(getClass().getResource(connectionPath).getPath());
            config.load(confFile);
            confFile.close();
            userProperties.setProperty("user", config.getProperty("usuario"));
            userProperties.setProperty("password", config.getProperty("clave"));
            connection = DriverManager.getConnection(
                    "jdbc:" + config.get("gestor") + "://" +
                            config.getProperty("servidor") + ":" +
                            config.getProperty("puerto") + "/" +
                            config.getProperty("baseDatos"),
                    userProperties
            );

        } catch (IOException | SQLException e) {
            PiikLogger.getInstance().log(Level.SEVERE, "ApiFacade -> ConnectionProxy", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
