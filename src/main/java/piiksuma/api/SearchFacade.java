package piiksuma.api;

import java.sql.Connection;

public class SearchFacade {
    private ApiFacade parentFacade;

    public SearchFacade(ApiFacade parentFacade) {
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



    /* MLTIMEDIA related methods */



    /* POST related methods */



    /* MESSAGE related methods */



    /* INTERACTION related methods */


}
