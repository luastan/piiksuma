package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

import java.util.Objects;

@MapperTable(nombre = "ticket")

public class Ticket extends PiikObject{

    /* Attributes */
    @MapperColumn(pkey = true)
    private String id;
    @MapperColumn(columna = "closeDate")
    private String closeDate;
    @MapperColumn(fKeys = "usr", targetClass = User.class, notNull = true)
    private User user;
    @MapperColumn(notNull = true)
    private String section;
    @MapperColumn(columna = "text", notNull = true)
    private String textProblem;
    @MapperColumn
    private String creationDate;
    @MapperColumn(fKeys = "adminClosing", targetClass = User.class)
    private User adminClosing;

    /* Constructors */

    public Ticket() {
    }

    public Ticket(String section) {

        if (section != null) {
            this.section = section;
        } else {
            this.section = "";
        }

        closeDate = "";
    }


    /* Getters and setters */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public User getAdminClosing() {
        return adminClosing;
    }

    public void setAdminClosing(User adminClosing) {
        this.adminClosing = adminClosing;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getTextProblem() {
        return textProblem;
    }

    public void setTextProblem(String textProblem) {
        this.textProblem = textProblem;
    }

    /**
     * This function checks if the values of the primary keys are not null or are not empty
     *
     * @return the function return "true" if the primary keys are not null, otherwise return "false"
     */
    /*public boolean checkPrimaryKey() {
        // TODO Eliminar esto una vez se compruebe el funcionamiento del PiikObject
        return id != null;
    }*/

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

        if (getTextProblem() == null || getTextProblem().isEmpty()) {
            return false;
        }

        if (getSection() == null || getSection().isEmpty()) {
            return false;
        }

        // TODO Eliminar esto una vez se compruebe el funcionamiento del PiikObject

        return getUser() != null && getUser().checkPrimaryKey();
    }*/


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return id.equals(ticket.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
