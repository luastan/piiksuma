package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;


@MapperTable(nombre = "tickets")
public class Ticket {
    @MapperColumn
    private String id;


    public Ticket(String id) {
        this.id = id;
    }

    public Ticket() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
