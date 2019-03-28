package myCoolApp.database;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Un builder para mapear la base de datos en memoria
 *
 * @param <T>
 */
class QueryMapper<T> {
    private Connection conexion;
    private PreparedStatement statement;
    private Class<T> classy;


    /**
     *
     * @param conexion Conexion a la base de datos
     */
    QueryMapper(Connection conexion) {
        this.conexion = conexion;
    }

    /**
     * Define la consulta que se hará a la base de datos
     *
     * @param consulta String con la consulta a realizar
     * @return El propio mapper
     */
    QueryMapper<T> crearConsulta(String consulta) {
        try {
            statement = conexion.prepareStatement(consulta);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return this;
    }



    /**
     * Imprescindible definir la clase a la que pertenecen los objetos
     * que devuelve la consulta si no es de escritura/modificacion
     *
     * @param classy Clase a la que se mapea el resultado
     * @return El propio mapper porque es un builder
     */
    QueryMapper<T> definirEntidad(Class<T> classy) {
        this.classy = classy;
        return this;
    }

    /**
     * Permite definir parametros en caso de que la consulta los requiera
     *
     * @param parametros Lista de parametros que se pasan al prepared statement
     *                   en orden
     * @return El propio mapper
     */
    QueryMapper<T> definirParametros(Object... parametros) {
        Object param;
        int index = 1;
        try {
            for (Object parametro : parametros) {
                statement.setObject(index++, parametro);
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }

        return this;
    }



    /* Métodos de finalización */


    /**
     * Lista con el resultado de la consulta
     *
     * @return Lista de objetos mapeados con el tipo indicado
     */
    List<T> list() {
        ArrayList<T> resultado = new ArrayList<>();
        String nombreColumna = "";
        HashSet<String> columnas = new HashSet<>();
        T elemento;
        try {
            /* Mapeado */
            statement.execute();
            ResultSet set = statement.getResultSet();
            // Metadata parsing
            for (int i = 1; i <= set.getMetaData().getColumnCount(); i++) {
                columnas.add(set.getMetaData().getColumnName(i));
            }
            while (set.next()) {
                // Constructor y atributos con reflection
                elemento = classy.getConstructor(new Class[]{}).newInstance();
                for (Field field : classy.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(MapperColumn.class)) {
                        nombreColumna = field.getAnnotation(MapperColumn.class).columna();
                        nombreColumna = nombreColumna.equals("") ? field.getName() : nombreColumna;
                        if (columnas.contains(nombreColumna)) {
                            field.set(elemento, set.getObject(nombreColumna));
                            // En caso de que un atributo no tenga una columna equivalente se ignora
                        }
                    }
                }
                resultado.add(elemento);
            }
            statement.close();

            /* Excepciones */
        } catch (SQLException e) {
            System.out.println("SQL MOVIDA");
            e.printStackTrace();
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return resultado;
    }


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
            for (int i = 0; i < set.getMetaData().getColumnCount(); i++) {
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
            e.printStackTrace();
        }

        return resultadosMapeados;
    }
}
