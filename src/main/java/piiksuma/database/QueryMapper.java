package piiksuma.database;

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
public class QueryMapper<T> extends Mapper{
    private PreparedStatement statement;
    private Class<? extends T> mappedClass;


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
    public QueryMapper<T> crearConsulta(String consulta) {
        try {
            statement = super.connection.prepareStatement(consulta);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return this;
    }


    /**
     * Imprescindible definir la clase a la que pertenecen los objetos
     * que devuelve la consulta si no es de escritura/modificacion
     *
     * @param mappedClass Clase a la que se mapea el resultado
     * @return El propio mapper porque es un builder
     */
    public QueryMapper<T> definirEntidad(Class<? extends T> mappedClass) {
        this.mappedClass = mappedClass;
        return this;
    }

    /**
     * Permite definir parametros en caso de que la consulta los requiera
     *
     * @param parametros Lista de parametros que se pasan al prepared statement
     *                   en orden
     * @return El propio mapper
     */
    public QueryMapper<T> definirParametros(Object... parametros) {
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


    /**
     * Busca de forma automatica una clave foranea y la mapea
     *
     * @param clase   Clase correspondiente a la clave foranea
     * @param pkObject Objeto que tienen un
     * @return Clase mapeada correspondiente a la tupla resultado de la clave
     * foranea. Si la clase indicada no tiene las anotaciones necesarias
     * devuelve un null
     */
    private Object getFK(Class<?> clase, Object pkObject) {
        if (!clase.isAnnotationPresent(MapperTable.class)) {
            return null;
        }
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ");
        // Se crea la base de la consulta:
        // SELECT * FROM [TABLA] WHERE [PRIMARIA_TABLA]=pkObject;
        queryBuilder.append(clase.getAnnotation(MapperTable.class).nombre().equals("") ?
                clase.getName() : clase.getAnnotation(MapperTable.class).nombre()).append(" ? WHERE ");

        // Consigue el Field que corresponde con la clave primaria
        Field fkField = Arrays.stream(clase.getDeclaredFields())
                .filter(field ->
                        field.isAnnotationPresent(MapperColumn.class) && field.getAnnotation(MapperColumn.class).pkey())
                .findAny().orElseThrow(RuntimeException::new);

        queryBuilder.append(
                fkField.getAnnotation(MapperColumn.class).columna().equals("") ?
                        fkField.getName() : fkField.getAnnotation(MapperColumn.class).columna()
        ).append("=?");  // Para usar la clave primaria en el WHERE

        // Devuelve
        return new QueryMapper<>(super.connection)
                .crearConsulta(queryBuilder.toString()).definirEntidad(clase).definirParametros(pkObject)
                .findFirst(false);
    }


    /* Métodos de finalización */


    /**
     * Lista con el resultado de la consulta
     *
     * @return Lista de objetos mapeados con el tipo indicado
     */
    public List<T> list() {
        return this.list(true);
    }


    /**
     * Genera una lista con el resultado de la consulta mapeado
     *
     * @param useForeignKeys Booleano que indica si se deben guardar las claves foraneas o no
     * @return Lista de objetos mapeados
     */
    public List<T> list(boolean useForeignKeys) {
        ArrayList<T> resultado = new ArrayList<>();
        String nombreColumna;
        HashSet<String> columnas = new HashSet<>();
        T elemento;
        Class<?> foreignClass;
        try {
            /* Mapeado */
            statement.executeQuery();
            ResultSet set = statement.getResultSet();
            // Metadata parsing
            for (int i = 1; i <= set.getMetaData().getColumnCount(); i++) {
                columnas.add(set.getMetaData().getColumnName(i));
            }
            while (set.next()) {
                // Constructor y atributos con reflection
                elemento = mappedClass.getConstructor(new Class[]{}).newInstance();
                for (Field field : mappedClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(MapperColumn.class)) {
                        nombreColumna = field.getAnnotation(MapperColumn.class).columna();
                        nombreColumna = nombreColumna.equals("") ? field.getName() : nombreColumna;
                        if (columnas.contains(nombreColumna)) {
                            foreignClass = field.getAnnotation(MapperColumn.class).targetClass();
                            // Comprueba si el objeto es una clase Custom
                            if (useForeignKeys && foreignClass != Object.class &&
                                    foreignClass.isAnnotationPresent(MapperColumn.class)) {
                                field.set(elemento, getFK(foreignClass, set.getObject(nombreColumna)));
                            } else {
                                field.set(elemento, set.getObject(nombreColumna));
                            }
                        }
                    }
                }
                resultado.add(elemento);
            }
            statement.close();

            /* Excepciones */
        } catch (SQLException e) {
            // TODO: Tratar excepciones SQL
            System.out.println("SQL MOVIDA");
            e.printStackTrace();
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            // TODO: Tratar excepciones Reflection
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
