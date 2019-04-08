package piiksuma.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Un builder para mapear la base de datos en memoria
 *
 * @param <T>
 */
public class QueryMapper<T> extends Mapper<T>{

    /**
     * @param conexion Conexion a la base de datos
     */
    public QueryMapper(Connection conexion) {
        super(conexion);
    }

    /**
     * Define la consulta que se hará a la base de datos
     *
     * @param consulta String con la consulta a realizar
     * @return El propio mapper
     */
    @Override
    public QueryMapper<T> createQuery(String query) {
        super.createQuery(query);
        return this;
    }


    /**
     * Imprescindible definir la clase a la que pertenecen los objetos
     * que devuelve la consulta si no es de escritura/modificacion
     *
     * @param mappedClass Clase a la que se mapea el resultado
     * @return El propio mapper porque es un builder
     */
    @Override
    public QueryMapper<T> defineClass(Class<? extends T> mappedClass) {
        super.defineClass(mappedClass);
        return this;
    }

    /**
     * Permite definir parametros en caso de que la consulta los requiera
     *
     * @param parametros Lista de parametros que se pasan al prepared statement
     *                   en orden
     * @return El propio mapper
     */
    public QueryMapper<T> defineParameters(Object... parametros) {
        super.defineParameters(parametros);
        return this;
    }

    /* Métodos de finalización */

    /**
     * Crea un Hashmap con los nombres de las columnas como claves y los atribs como valores
     *
     * @return Lista de Hashmaps todos iguales con los resultados de una consulta
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
            // TODO: Tratar excepciones
            e.printStackTrace();
        }
        return resultadosMapeados;
    }

    /**
     * Devuelve un unico elemento esperado en la consulta
     *
     * @param useForeignkeys Activa el uso de la consulta recursiva y mapeado
     *                       de claves foraneas
     * @return Primer elemento devuelto por el ResultSet
     */
    public T findFirst(boolean useForeignkeys) {
        List<T> result = list(useForeignkeys);

        if(result == null || result.isEmpty()){
            return null;
        }

        return result.get(0);
    }


    /**
     * Devuelve un unico elemento esperado en la consulta
     * Recorre claves foraneas
     *
     * @return Primer elemento devuelto por el ResultSet
     */
    public T findFirst() {
        return findFirst(true);
    }
}
