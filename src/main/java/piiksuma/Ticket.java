package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

@MapperTable(nombre = "tickets")

public class Ticket extends Messages {

    /* Attributes */

    @MapperColumn
    private String seccion;


    /* Constructors */

    public Ticket() {
    }

    public Ticket(String seccion) {

        if(seccion != null ) {
            this.seccion = seccion;
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
