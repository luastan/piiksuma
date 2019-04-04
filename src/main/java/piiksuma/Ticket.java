package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

@MapperTable(nombre = "tickets")

public class Ticket extends Message {

    /* Attributes */

    @MapperColumn
    private String seccion;


    /* Constructors */

    public Ticket() {
    }

    public Ticket(String seccion) {

        // todo add message fields to constructor
        super();

        if (seccion != null) {
            this.seccion = seccion;
        } else {
            this.seccion = "";
        }
    }


    /* Getters and setters */

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }
}
