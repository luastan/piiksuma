package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

@MapperTable(nombre = "ticket")

public class Ticket extends Message {

    /* Attributes */
    private String closeDate;
    private String section;
    private String textProblem;
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

        closeDate = "";
    }


    /* Getters and setters */

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
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
}
