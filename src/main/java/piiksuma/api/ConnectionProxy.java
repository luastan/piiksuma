package piiksuma.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Proxy around the Database connection. It creates the connection with the database
 */
public class ConnectionProxy {
    private Connection connection;


    /**
     * Basic and unique constructor. Instantiates the connection reading the parameters from a .properties file
     *
     * @param connectionPath Path where the properties file can be located
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
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
