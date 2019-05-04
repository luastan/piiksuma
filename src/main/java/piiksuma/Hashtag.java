package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

import java.util.Objects;

@MapperTable(nombre = "hashtag")
public class Hashtag extends PiikObject{

    /* Attributes */
    @MapperColumn(pkey = true)
    private String name;


    /* Constructors */

    public Hashtag(){
    }

    public Hashtag(String name) {
        if (name != null) {
            this.name = name;
        } else {
            this.name = "";
        }
    }


    /* Getters and setters */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Function to check that the attributes with restriction 'not null' are not null
     *
     * @return the function return "true" if the attributes are not null, otherwise return "false"
     */
    /*public boolean checkNotNull() {
        // TODO Eliminar esto una vez se compruebe el funcionamiento del PiikObject
        // Check that the primary keys are not null
        return checkPrimaryKey();

    }*/

    /**
     * Function to check that the primary keys are not null
     *
     * @return the function return "true" if the primary keys are not null, otherwise return "false"
     */
    /*public boolean checkPrimaryKey() {
        // Check that the primary keys are not null
        return getName() != null && !getName().isEmpty();

        // TODO Eliminar esto una vez se compruebe el funcionamiento del PiikObject

    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hashtag hashtag = (Hashtag) o;
        return name.equals(hashtag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
