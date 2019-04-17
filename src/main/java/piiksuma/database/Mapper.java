package piiksuma.database;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
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
    protected PreparedStatement statement;
    protected Class<? extends T> mappedClass;
    protected static Pattern regexFKeys = Pattern.compile("(\\w+):(\\w+)");

    public Mapper(Connection connection) {
        this.connection = connection;
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

    ;

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

        return queryMapper.createQuery(queryBuilder.toString()).defineParameters(params.toArray()).findFirst(false);
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
