package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

import java.sql.Timestamp;
import java.util.Objects;

@MapperTable
public class Notification extends PiikObject{
    @MapperColumn(pkey = true, hasDefault = true)
    private String id;
    @MapperColumn(columna = "creationdate")
    private Timestamp creationDate;
    @MapperColumn(notNull = true)
    private String content;

    public Notification() {

    }

    public Notification(Notification notification) {
        this.id = notification.getId();
        this.creationDate = notification.getCreationDate();
        this.content = notification.getContent();
    }

    public Notification(String id, Timestamp creationDate, String content) {
        this.id = id;
        this.creationDate = creationDate;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Function to check that the attributes with restriction 'not null' are not null
     *
     * @return the function return "true" if the attributes are not null, otherwise return "false"
     */
    /*public boolean checkNotNull() {
        // Check that the primary keys are not null
        if (!checkPrimaryKey()) {
            return false;
        }

        return getContent() != null && !getContent().isEmpty();
    }*/

    /**
     * Function to check that the primary keys are not null
     *
     * @return the function return "true" if the primary keys are not null, otherwise return "false"
     */
    /*public boolean checkPrimaryKey() {
        // Check that the primary keys are not null
        return (getId() != null && !getId().isEmpty());
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
