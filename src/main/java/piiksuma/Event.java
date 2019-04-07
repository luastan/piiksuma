package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

@MapperTable
public class Event {
    @MapperColumn(pkey = true)
    private String id;
    @MapperColumn
    private String description;
    @MapperColumn
    private String location;
    @MapperColumn
    private String date;
    @MapperColumn
    private String name;
    @MapperColumn
    private String creator;


    public Event(String id, String nombre) {
        if (id == null) {
            this.id = "";
        } else {
            this.id = id;
        }
        if (nombre == null) {
            this.name = "";
        } else {
            this.name = nombre;
        }
    }

    public Event(String id, String description, String location, String date, String nombre) {
        if (id == null) {
            this.id = "";
        } else {
            this.id = id;
        }
        if (description == null) {
            this.description = "";
        } else {
            this.description = description;
        }
        if (location == null) {
            this.location = "";
        } else {
            this.location = location;
        }
        if (date == null) {
            this.date = "";
        } else {
            this.date = date;
        }
        if (nombre == null) {
            this.name = "";
        } else {
            this.name = nombre;
        }
    }

    public Event(){

    }

    /*GETTERS*/

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    /*SETTERS*/

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
