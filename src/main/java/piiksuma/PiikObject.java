package piiksuma;

import piiksuma.database.Mapper;
import piiksuma.database.MapperColumn;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class PiikObject {

    public Map<String, Object> getPKs(boolean isInsertion) {
        // HashMap with the primary keys indexed by the name of the column
        HashMap<String, Object> pKeys = new HashMap<>();

        // Loops over all the fields from the object
        for (Field field : this.getClass().getDeclaredFields()) {

            // Insertion -> Performs the mapping only of the annotated class and if the field is primary key and hasn't
            // got a default value
            if (isInsertion) {
                if (field.isAnnotationPresent(MapperColumn.class) && field.getAnnotation(MapperColumn.class).pkey()
                    && !field.getAnnotation(MapperColumn.class).hasDefault()) {
                    // Inserts the field
                    insertion(pKeys, field);
                }
            }

            // Iterated field must also have a value even if it is default
            else {
                if (field.isAnnotationPresent(MapperColumn.class) && field.getAnnotation(MapperColumn.class).pkey()) {
                    // Inserts the field
                    insertion(pKeys, field);
                }
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

            // Iterated field must also have a value even if it is default
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
     * Function to get the first primary key of the object if the insertion is false
     *
     * @return the first primary key
     */
    public Object getPK(){
        return getPK(false);
    }

    /**
     * Function to get the first primary key of the object
     *
     * @param isInsertion boolean to indicate that it is an insert
     * @return the first primary key
     */
    public Object getPK(boolean isInsertion){
        Map<String, Object> pks = getPKs(isInsertion);
        return pks.values().toArray()[0];
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
    public boolean checkPrimaryKey(boolean isInsertion) {
        for (Object pKey : getPKs(isInsertion).values()) {
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
                if (!((PiikObject) notNull).checkPrimaryKey(isInsertion)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Function to insert in the object all the information of the Map
     *
     * @param information information to add in the object
     */
    public void addInfo(Map<String, Object> information){
        try {

            for (Field field : this.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                if(field.isAnnotationPresent(MapperColumn.class)){

                    String columnName = Mapper.extractColumnName(field);

                    if(information.containsKey(columnName)){

                        field.set(this, information.get(columnName));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
