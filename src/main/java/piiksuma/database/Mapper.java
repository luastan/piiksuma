package piiksuma.database;

import piiksuma.PiikObject;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Common functionality between all the Mapper Classes.
 *
 * @param <T> Mapped class type. Used to check asigments when returning query
 *            results.
 * @author luastan
 * @author CardamaS99
 * @author danimf99
 * @author alvrogd
 * @author OswaldOswin1
 * @author Marcos-marpin
 */
public abstract class Mapper<T> {
    protected Connection connection;

    // Set with the atomic classes (String, Integer, etc...)
    protected Set<Class<?>> atomicClasses;
    protected PreparedStatement statement;
    protected Class<? extends T> mappedClass;
    protected static Pattern regexFKeys = Pattern.compile("(\\w+):(\\w+)");

    public Mapper(Connection connection) {
        this.connection = connection;
        this.atomicClasses = new HashSet<>();

        // Definition of the atomic classes
        this.atomicClasses.add(String.class);
        this.atomicClasses.add(Integer.class);
        this.atomicClasses.add(Double.class);
        this.atomicClasses.add(Float.class);
        this.atomicClasses.add(Timestamp.class);
        this.atomicClasses.add(Date.class);
        this.atomicClasses.add(Character.class);
    }

    public Connection getConnection() {
        return connection;
    }

    public class DEFAULT {
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Extracts the Primary Key value from a given object
     *
     * @param forKeyObject Object to be extracted the value from. Normally
     *                     corresponding to a foreign key object
     * @return Object's Primary Key value
     * @throws IllegalAccessException In case of being unable to grab the field
     *                                value
     */
    protected Object fkValue(Object forKeyObject) throws IllegalAccessException {
        Field fkField = Arrays.stream(forKeyObject.getClass().getDeclaredFields())
                .filter(field ->
                        field.isAnnotationPresent(MapperColumn.class) && field.getAnnotation(MapperColumn.class).pkey())
                .findAny().orElseThrow(RuntimeException::new);
        fkField.setAccessible(true);
        return fkField.get(forKeyObject);
    }

    /**
     * Allows an update to be specified whith SQL.
     *
     * @param update String with the SQL code to be used in the update
     * @return The current Mapper instance
     */
    public Mapper<T> createUpdate(String update) {
        try {
            statement = connection.prepareStatement(update);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    /**
     * Executes the update whith the previously asigned parameters.
     */
    public void executeUpdate() {
        try {
            /* Mapeado */
            statement.execute();
            statement.close();
            /* Excepciones */
        } catch (SQLException e) {
            // TODO: Tratar excepciones SQL
            System.out.println("SQL MOVIDA");
            e.printStackTrace();
        }
    }

    /**
     * Queries a foreign key and maps it automatically
     *
     * @param clase    Class to be looped over
     * @param pkObject Object used as a parameter within the query to find
     *                 the tuple in the database
     * @return Mapped instance from the query result set. When the class isn't
     * annotated with {@link MapperTable MapperTable} returns null.
     */
    protected Object getFK(Class<?> clase, Object pkObject) {
        if (!clase.isAnnotationPresent(MapperTable.class)) {
            return null;
        }

        // TODO: Check if pkObject is an instance of the class and throw
        // exceptions

        // Base query
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ");
        // Query will end up looking somewhat like this
        // SELECT * FROM [TABLE] WHERE [PRIMARY_KEY]=pkObject;
        queryBuilder.append(clase.getAnnotation(MapperTable.class).nombre().equals("") ?
                clase.getName() : clase.getAnnotation(MapperTable.class).nombre()).append(" WHERE ");
        // .append(" ? WHERE "); ?¿¿?¿

        // Finds the field which is anotaded as Primary Key
        Field fkField = Arrays.stream(clase.getDeclaredFields())
                .filter(field ->
                        field.isAnnotationPresent(MapperColumn.class) && field.getAnnotation(MapperColumn.class).pkey())
                .findAny().orElseThrow(RuntimeException::new);

        queryBuilder.append(
                fkField.getAnnotation(MapperColumn.class).columna().equals("") ?
                        fkField.getName() : fkField.getAnnotation(MapperColumn.class).columna()
        ).append("=?");  // ? used to insert it on the where clause

        // Returns the mapped instance with
        return new QueryMapper<>(connection)
                .createQuery(queryBuilder.toString()).defineClass(clase).defineParameters(pkObject)
                .findFirst(false);
    }

    /**
     * Queries a foreign key and maps it automatically.
     *
     * @param clase Class to be looped over
     * @param pkeys Object used as a parameter within the query to find
     *              the tuple in the database
     * @return Mapped instance from the query result set. When the class isn't
     * annotated with {@link MapperTable MapperTable} returns null.
     */
    protected Object getFK(Class<?> clase, Map<String, Object> pkeys) {
        if (!clase.isAnnotationPresent(MapperTable.class)) {
            return null;
        }
        // If any of the pkeys is null then the object should be null too
        if (pkeys.values().stream().anyMatch(Objects::isNull)) {
            return null;
        }
        String tmpColumn;
        QueryMapper<?> queryMapper = new QueryMapper<>(connection).defineClass(clase);
        ArrayList<Object> params = new ArrayList<>();

        // Base query
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ");
        queryBuilder.append(clase.getAnnotation(MapperTable.class).nombre().equals("") ?
                clase.getName() : clase.getAnnotation(MapperTable.class).nombre()).append(" WHERE ");

        for (Field field : clase.getDeclaredFields()) {
            if (field.isAnnotationPresent(MapperColumn.class) && field.getAnnotation(MapperColumn.class).pkey()) {
                tmpColumn = field.getAnnotation(MapperColumn.class).columna().equals("") ?
                        field.getName() : field.getAnnotation(MapperColumn.class).columna();
                queryBuilder.append(tmpColumn).append("=? and ");  // ? used to insert it on the where clause
                params.add(pkeys.get(tmpColumn));
            }
        }
        // Crops the queryBuilder in order to get rid of the residual "and" added after each WHERE condition
        queryBuilder.delete(queryBuilder.length() - 4, queryBuilder.length());

        return queryMapper.createQuery(queryBuilder.toString()).defineParameters(params.toArray())
                .findFirst(false);
    }

    /**
     * Function that returns the foreign keys of the object, of atomic form
     *
     * @param object object to get the foreign keys
     * @return the foreign keys indexed by the name of column
     */
    public Map<String, Object> getFKs(T object){

        // HashMap with the foreign keys indexed by the name of the column of the relation that contains the FKs
        HashMap<String, Object> containFK = new HashMap<>();
        try {

            // Se recorren las field de la mappedClass
            for(Field field : object.getClass().getDeclaredFields()){

                // Se hace accesible
                field.setAccessible(true);

                // Se obtiene el objeto de la field en concreto a partir del objeto del que se van a obtener las FK
                Object objFK = field.get(object);

                // Se comprueba que el objeto sea null (no se referencia a nada)
                // Que la field tenga una anotación de tipo MapperColumn
                // Que la targetClass no sea un Object
                if(objFK != null && field.isAnnotationPresent(MapperColumn.class) &&
                        field.getAnnotation(MapperColumn.class).targetClass() != Object.class){

                    // Se obtiene la targetClass de las claves foráneas
                    Class<?> targetClass = field.getAnnotation(MapperColumn.class).targetClass();

                    // Se obtienen las claves foráneas separándolas por un espacio, con la siguiente expresión:
                    // atributoRelación:atributoRelacionReferenciada
                    String[] foreignKeys = field.getAnnotation(MapperColumn.class).fKeys().split(" ");

                    // Se comprueba si la targetClass es de tipo atómico
                    if(isAtomicClass(targetClass)){
                        // En caso de que sea de tipo atómico, solo se referencia a un atributo de la otra relación y
                        // se añade directamente al HashMap
                        containFK.put(foreignKeys[0].split(":")[0], field.get(object));
                    } else {

                        // En caso de que no sea de tipo atómico, será necesario obtener las claves primarias de la
                        // clase foránea.
                        //
                        // Se crea un HashMap para tener los nombres de los atributos que son claves foráneas
                        // indexadas por el nombre de atributo al que está referenciando en la otra tabla
                        //
                        // Por ejemplo en sugarDaddy:id, la clave sería "id" y el valor "sugarDaddy"
                        HashMap<String, String> translateColumn = new HashMap<>();

                        // Se recorren todas las foreignKeys obtenidas anteriormente
                        for(String fk : foreignKeys){
                            // Se genera una tupla mediante el split(":") para separar el nombre del atributo en la
                            // relación y el nombre en la relación referenciada
                            String[] tuple = fk.split(":");

                            // Se añade la clave y el valor al HashMap para luego realizar la traducción en el nombre de
                            // columnas
                            translateColumn.put(tuple[1], tuple[0]);
                        }

                        // Se obtienen las claves primarias atómicas del objeto que contiene la field
                        Map<String, Object> atomicPks = getAtomicPK(field.get(object));

                        // Se recorren las claves primarias obtenidas
                        for(String column : atomicPks.keySet()){
                            // Se obtiene el nombre de la columna en la relación inicial a partir del nombre de
                            // columna de la tabla referenciada y se añade la clave foránea
                            containFK.put(translateColumn.get(column), atomicPks.get(column));
                        }
                    }
                }
            }
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return containFK;
    }

    /**
     * Function that returns the primary keys of an object in an atomic form, ready to be used in an SQL query
     *
     * @param object object from which the primary keys are obtained
     * @return a map with the primary keys indexed by the name of column
     */
    protected Map<String, Object> getAtomicPK(Object object){

        // HashMap en el que se devolverá al usuario las claves primarias en clases atómicas
        HashMap<String, Object> pKeys = new HashMap<>();

        // Mapa con las fields de las claves primarias del objeto
        Map<String, Field> fields = getPK(object.getClass());
        try {

            // Se recorren todas las fields que son clave primaria, donde su columna equivale al nombre
            // de la columna en la base de datos. En caso de que haya más de una clave primaria se separan
            // por ":"
            for(String columnName : fields.keySet()){

                // Se obtiene la field correspondiente a esta clave
                Field field = fields.get(columnName);
                field.setAccessible(true);

                // Se comprueba si la clase de lo que hay almacenado en la field es una clase atómica
                if (!isAtomicClass(field.get(object).getClass())) {

                    // Si no es una clase atómica se obtienen las claves primarias atómicas de esa clase
                    Map<String, Object> pAux = getAtomicPK(field.get(object));

                    // Se inicializa un contador a 0
                    int count = 0;

                    // Se separan el nombre de las columnas en la base de datos en caso de que la clave primaria
                    // esté separada en distintos atributos
                    String[] columnNames = columnName.split(":");

                    // El número de claves que se han devuelto en getAtomicPK debería ser el mismo número de columnas
                    // que se generó en el anterior split
                    for(String columnExtern : pAux.keySet()){
                        // La función getAtomicPK devuelve las claves primarias indexadas por su columna en dicha
                        // relación, por lo tanto hay que indexarlo en función de columnNames y no de columnExtern
                        pKeys.put(columnNames[count], pAux.get(columnExtern));

                        // Se incrementa cuenta
                        count+=2;
                    }
                } else {
                    // En caso de que la clase sea atómica se añade la clave primaria
                    pKeys.put(columnName, field.get(object));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return pKeys;
    }

    /**
     * Function to know if a class type is atomic or not
     *
     * @param classField type of class to analyze
     * @return true if the class is contained in the set of the atomic classes. In another case, false
     */
    public boolean isAtomicClass(Class<?> classField){
        return this.atomicClasses.contains(classField);
    }

    /**
     * Function to return the fields of the primary keys of the mappedClass
     *
     * @return the fields indexed by the name of the column
     */
    public Map<String, Field> getPK(){
        return getPK(mappedClass);
    }

    /**
     * Function to return the fields of the primary keys of the mappedClass
     *
     * @param mappedClass class from which to obtain the primary keys
     * @return the fields indexed by the name of the column
     */
    protected Map<String, Field> getPK(Class<?> mappedClass){

        // HashMap with the primary keys indexed by the name of the column
        HashMap<String, Field> pKeys = new HashMap<>();

        // String with the name of the column of a primary key
        String columnName;

        // Loops over all the fields from the object
        for(Field field : mappedClass.getDeclaredFields()){

            // Performs the mapping only of the annotated class and if the field is a primary key
            if(field.isAnnotationPresent(MapperColumn.class) && field.getAnnotation(MapperColumn.class).pkey()){

                // Column name extraction
                // On empty / default column name specification uses the field name
                columnName = field.getAnnotation(MapperColumn.class).columna();
                columnName = columnName.equals("") ? field.getName() : columnName;

                // Add the field of the PK in the HashMap
                pKeys.put(columnName, field);
            }
        }

        return pKeys;
    }

    /**
     * Defines the Class to be used when mapping the subsequent query results
     *
     * @param mappedClass Class for the results to be mapped to
     * @return The mapper instance
     */
    public Mapper<T> defineClass(Class<? extends T> mappedClass) {
        this.mappedClass = mappedClass;
        return this;
    }

    /**
     * Defines the parameters to be inserted in the {@link PreparedStatement PreparedStatement}
     *
     * @param parametros Parameter list to be inserted in the {@link Mapper#statement statement}
     *                   in the same order as the parametes where passed
     * @return The mapper instance
     */
    public Mapper<T> defineParameters(Object... parametros) {
        Object param;
        int index = 1;
        try {
            for (Object parametro : parametros) {
                statement.setObject(index++, parametro);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return this;
    }
}
