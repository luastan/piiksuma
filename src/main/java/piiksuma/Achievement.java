package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

import java.sql.Timestamp;
import java.util.Objects;

@MapperTable
public class Achievement extends PiikObject{
    @MapperColumn(pkey = true)
    private String id;
    @MapperColumn(notNull = true)
    private String name;
    @MapperColumn
    private String description;
    // Needed to show unlock date in GUI
    private Timestamp unlockDate;

    public Achievement() {
    }

    public Achievement(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getUnlockDate() {
        return unlockDate;
    }

    public void setUnlockDate(Timestamp unlockDate) {
        this.unlockDate = unlockDate;
    }

    /**
     * This function checks if the values of the primary keys are not null or are not empty
     *
     * @return the function return "true" if the primary keys are not null, otherwise return "false"
     */
   /* public boolean checkPrimaryKey() {
        return id != null && !id.isEmpty();
        // TODO Eliminar esto una vez se compruebe el funcionamiento del PiikObject
    }*/

    /*public boolean checkNotNull() {
        if (!checkPrimaryKey()) {
            return false;
        }
        return name != null && !name.isEmpty();
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Achievement that = (Achievement) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
