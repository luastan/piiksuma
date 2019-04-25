package piiksuma;

import piiksuma.database.MapperColumn;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class PiikObject {

    public Map<String, Object> getPKs() {
        // HashMap with the primary keys indexed by the name of the column
        HashMap<String, Object> pKeys = new HashMap<>();

        // Loops over all the fields from the object
        for (Field field : this.getClass().getDeclaredFields()) {

            // Performs the mapping only of the annotated class and if the field is a primary key
            if (field.isAnnotationPresent(MapperColumn.class) && field.getAnnotation(MapperColumn.class).pkey()) {

                // Insert the field
                insertion(pKeys, field);
            }
        }

        return pKeys;
    }

    public Map<String, Object> getNotNulls(boolean isInsertion) {
        // HashMap with the not nulls indexed by the name of the column
        HashMap<String, Object> notNulls = new HashMap<>();

        // Loops over all the fields from the object
        for (Field field : this.getClass().getDeclaredFields()) {

            // Insertion -> Performs the mapping only of the annotated class and if the field is a notNull or is a
            // primary key and hasn't got a default value
            if (isInsertion) {
                if (field.isAnnotationPresent(MapperColumn.class) &&
                        (field.getAnnotation(MapperColumn.class).notNull() ||
                                field.getAnnotation(MapperColumn.class).pkey()) &&
                        !(field.getAnnotation(MapperColumn.class).hasDefault())) {

                    // Inserts the field
                    insertion(notNulls, field);
                }
            }

            // Iterated field must also have a value if it is default
            else {
                if (field.isAnnotationPresent(MapperColumn.class) &&
                        (field.getAnnotation(MapperColumn.class).notNull() ||
                                field.getAnnotation(MapperColumn.class).pkey())) {

                    // Inserts the field
                    insertion(notNulls, field);
                }
            }

        }

        return notNulls;
    }

    /**
     * Function to insert in the HashMap the object of the field
     *
     * @param contain contain of the object
     * @param field   field to insert
     */
    private void insertion(HashMap<String, Object> contain, Field field) {
        String columnName;

        // Column name extraction
        // On empty / default column name specification uses the field name
        columnName = field.getAnnotation(MapperColumn.class).columna();
        columnName = columnName.equals("") ? field.getName() : columnName;

        field.setAccessible(true);
        try {
            Object toInsert = field.get(this);
            // If the toInsert is a String and is empty, the object is null
            if (toInsert instanceof String) {
                if (((String) toInsert).isEmpty())
                    toInsert = null;
            }
            contain.put(columnName, toInsert);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to check that the primary keys are not null
     *
     * @return the function return "true" if the primary keys are not null, otherwise return "false"
     */
    public boolean checkPrimaryKey() {
        for (Object pKey : getPKs().values()) {
            if (pKey == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Function to check that the attributes with restriction 'not null' are not null
     *
     * @param isInsertion if TRUE, fields that have a default value are not considered
     * @return the function return "true" if the attributes are not null, otherwise return "false"
     */
    public boolean checkNotNull(boolean isInsertion) {

        for (Object notNull : getNotNulls(isInsertion).values()) {
            if (notNull == null) {
                return false;
            }

            // If the object is a PiikObject, this has to meet the checkPrimaryKey
            if (notNull instanceof PiikObject) {
                if (!((PiikObject) notNull).checkPrimaryKey()) {
                    return false;
                }
            }
        }
        return true;
    }
}
