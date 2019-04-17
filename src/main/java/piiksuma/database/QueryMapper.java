package piiksuma.database;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Database conection and data retrieving wrapper. Automatically maps retreved
 * data
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
public class QueryMapper<T> extends Mapper<T> {

    /**
     * @param conexion Database conection object
     */
    public QueryMapper(Connection conexion) {
        super(conexion);
    }

    /**
     * Defines the sentence to be queried to the database
     *
     * @param query String representing the query
     * @return Returns the Mapper instance
     */
    public QueryMapper<T> createQuery(String query) {
        try {
            statement = connection.prepareStatement(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    /**
     * Returns a list with the query results propperly mapped to the Class
     * defined at {@link QueryMapper#defineClass(Class)}
     *
     * @param useForeignKeys When true will attempt to find foreign keys when
     *                       the class representing the atributes has the
     *                       {@link MapperTable} annotation present
     * @return Mapped objects from the query
     */
    public List<T> list(boolean useForeignKeys) {
        ArrayList<T> resultado = new ArrayList<>();
        String nombreColumna;
        Matcher matcher;
        HashMap<String, Object> fkValues;
        HashSet<String> columnas = new HashSet<>();
        T elemento;
        Class<?> foreignClass;
        try {
            statement.execute();
            ResultSet set = statement.getResultSet();

            // Metadata parsing
            if (set != null) {
                for (int i = 1; i <= set.getMetaData().getColumnCount(); i++) {
                    columnas.add(set.getMetaData().getColumnName(i));
                }

                while (set.next()) {
                    // Extracts required empty constructor and Fields to map the resutls
                    elemento = mappedClass.getConstructor(new Class[]{}).newInstance();
                    for (Field field : mappedClass.getDeclaredFields()) {
                        field.setAccessible(true);
                        if (field.isAnnotationPresent(MapperColumn.class)) {
                            nombreColumna = field.getAnnotation(MapperColumn.class).columna();
                            nombreColumna = nombreColumna.equals("") ? field.getName() : nombreColumna;
                            if (columnas.contains(nombreColumna) || field.getAnnotation(MapperColumn.class).targetClass() != Object.class) {
                                foreignClass = field.getAnnotation(MapperColumn.class).targetClass();
                                // Checks if the Field class has the MapperTable anotation. This means that it's a
                                // foreign key and special actions are required
                                if (foreignClass != Object.class &&
                                        foreignClass.isAnnotationPresent(MapperTable.class)) {
                                    if (useForeignKeys) {
                                        // FKEYS
                                        if (field.getAnnotation(MapperColumn.class).fKeys().equals("")) {
                                            field.set(elemento, getFK(foreignClass, set.getObject(nombreColumna)));
                                        } else {
                                            fkValues = new HashMap<>();
                                            matcher = regexFKeys.matcher(field.getAnnotation(MapperColumn.class).fKeys());
                                            while (matcher.find()) {
                                                fkValues.put(matcher.group(2), set.getObject(matcher.group(1)));
                                            }
                                            field.set(elemento, getFK(foreignClass, fkValues));
                                        }

                                    }

                                } else {
                                    field.set(elemento, set.getObject(nombreColumna));
                                }
                            }
                        }
                    }
                    resultado.add(elemento);
                }
            }
            statement.close();

            // Exception handling
        } catch (SQLException e) {
            // TODO: SQL Exception Handling
            System.out.println("SQL EXCEPTION IN THE QUERY MAPPER");
            e.printStackTrace();
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            // TODO: Treat Reflection Exceptions
            e.printStackTrace();
        }
        return resultado;
    }

    /**
     * Does the same as {@link QueryMapper#list(boolean)} with the foreign keys
     * boolean as true
     *
     * @return Mapped objects list
     */
    public List<T> list() {
        return this.list(true);
    }


    /**
     * Defines the class to be used in the mapping process.
     *
     * @param mappedClass Class used to map the query results
     * @return The QueryMapper instance
     */
    @Override
    public QueryMapper<T> defineClass(Class<? extends T> mappedClass) {
        super.defineClass(mappedClass);
        return this;
    }

    /**
     * Defines the parameters used when executing the query. This parameters
     * are defined with ? in the {@link QueryMapper#createQuery(String)} String
     *
     * @param parametros Parameter list to be inserted into the {@link java.sql.PreparedStatement}
     *                   used to query the database
     * @return The QueryMapper instance
     */
    public QueryMapper<T> defineParameters(Object... parametros) {
        super.defineParameters(parametros);
        return this;
    }

    /* Closing methods */

    /**
     * When the Cass to be used wasn't defined with {@link QueryMapper#defineClass(Class)}
     * this method resturns the query results mapped into a list containing
     * {@link Map} instances with the column names used as keys in the Map, and
     * the values from the result tuples
     *
     * @return Map list with the query Results
     */
    public List<Map<String, Object>> mapList() {
        List<Map<String, Object>> resultadosMapeados = new ArrayList<>();
        Map<String, Object> element;
        ArrayList<String> columnas = new ArrayList<>();
        ResultSet set;
        try {
            statement.execute();
            set = statement.getResultSet();
            for (int i = 1; i <= set.getMetaData().getColumnCount(); i++) {
                columnas.add(set.getMetaData().getColumnName(i));
            }
            while (set.next()) {
                element = new HashMap<>();
                for (String columna : columnas) {
                    element.put(columna, set.getObject(columna));
                }
                resultadosMapeados.add(element);
            }
        } catch (SQLException e) {
            // TODO: Handle exceptions
            e.printStackTrace();
        }
        return resultadosMapeados;
    }

    /**
     * From the results, returns the first one. Usefull when querying a single
     * item. It performs the whole Mapping process which can be seen as
     * ineficient. For increased efficiency add limit(1) to de query.
     *
     * @param useForeignkeys Same atribute as in {@link QueryMapper#list(boolean)}
     * @return First element from the results
     */
    public T findFirst(boolean useForeignkeys) {
        List<T> result = list(useForeignkeys);

        if (result == null || result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }


    /**
     * Check {@link QueryMapper#findFirst(boolean)}. Does the same as findFirst(true)
     *
     * @return First element from the results
     */
    public T findFirst() {
        return findFirst(true);
    }
}
