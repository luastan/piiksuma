package piiksuma;

public class Event {
    private String id;
    private String description;
    private String location;
    private String date;
    private String nombre;


    public Event(String id, String nombre) {
        if (id == null) {
            this.id = "";
        } else {
            this.id = id;
        }
        if (nombre == null) {
            this.nombre = "";
        } else {
            this.nombre = nombre;
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
            this.nombre = "";
        } else {
            this.nombre = nombre;
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

    public String getNombre() {
        return nombre;
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

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
