package piiksuma;

public class Event {
    private String id;
    private String description;
    private String location;
    private String date;
    private String nombre;


    public Event(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Event(String id, String description, String location, String date, String nombre) {
        this.id = id;
        this.description = description;
        this.location = location;
        this.date = date;
        this.nombre = nombre;
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
