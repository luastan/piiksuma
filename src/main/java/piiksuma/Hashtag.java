package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

@MapperTable
public class Hashtag {

    /* Attributes */
    @MapperColumn(pkey = true)
    private String name;


    /* Constructors */

    public Hashtag() {
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
     * @return the function return "true" if the attributes are not null, otherwise return "false"
     */
    public boolean checkNotNull(){
        // Check that the primary keys are not null
        if(!checkPrimaryKey()) {
            return false;
        }

        return true;
    }

    /**
     * Function to check that the primary keys are not null
     * @return the function return "true" if the primary keys are not null, otherwise return "false"
     */
    public boolean checkPrimaryKey(){
        // Check that the primary keys are not null
        if(getName() == null || getName().isEmpty()){
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hashtag)) return false;

        Hashtag hashtag = (Hashtag) o;

        return getName().equals(hashtag.getName());

    }
}
