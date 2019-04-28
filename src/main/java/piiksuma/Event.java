package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

import java.sql.Timestamp;
import java.util.Objects;

@MapperTable
public class Event extends PiikObject{
    @MapperColumn(pkey = true)
    private String id;
    @MapperColumn(pkey = true, fKeys = "author:id", targetClass = User.class)
    private User creator;
    @MapperColumn(hasDefault = true)
    private String description;
    @MapperColumn(hasDefault = true)
    private String location;
    @MapperColumn
    private Timestamp date;
    @MapperColumn(hasDefault = true)
    private String name;


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

    public Event(Event event) {
        this.id = event.getId();
        this.creator = event.getCreator();
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.date = event.getDate();
        this.name = event.getName();
    }

    public Event(String id, String description, String location, Timestamp date, String nombre) {
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
        this.date = date;
        if (nombre == null) {
            this.name = "";
        } else {
            this.name = nombre;
        }
    }

    public Event() {

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

    public Timestamp getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public User getCreator() {
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

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    /**
     * This function checks if the values of the primary keys are not null or are not empty
     *
     * @return the function return "true" if the primary keys are not null, otherwise return "false"
     */
    /*public boolean checkPrimaryKey() {
        return id != null && !id.isEmpty();
        // TODO Eliminar esto una vez se compruebe el funcionamiento del PiikObject
    }*/

    /*public boolean checkNotNull() {
        if (!checkPrimaryKey()) {
            return false;
        }

        if (creator == null || !creator.checkPrimaryKey()) {
            return false;
        }

        return this.name != null && !name.isEmpty();
        // TODO Eliminar esto una vez se compruebe el funcionamiento del PiikObject
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id.equals(event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
