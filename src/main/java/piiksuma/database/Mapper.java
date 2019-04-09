package piiksuma.database;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public abstract class Mapper<T> {
    protected Connection connection;
    protected PreparedStatement statement;
    protected Class<? extends T> mappedClass;

    public Mapper(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Extrae el valor de la clave primaria de un objeto dado
     *
     * @param forKeyObject Objeto que corresponde con la clave foranea
     * @return Valor de la clave primaria del objeto
     * @throws IllegalAccessException No se pudo acceder al atributo
     * correspondiente a la clave primaria que referencia la clave foranea
     */
    protected Object fkValue(Object forKeyObject) throws IllegalAccessException {
        Field fkField = Arrays.stream(forKeyObject.getClass().getDeclaredFields())
                .filter(field ->
                        field.isAnnotationPresent(MapperColumn.class) && field.getAnnotation(MapperColumn.class).pkey())
                .findAny().orElseThrow(RuntimeException::new);
        fkField.setAccessible(true);
        return fkField.get(forKeyObject);
    };

    /**
     * Define la actualización que se hará a la base de datos
     *
     * @param update String con la actualización a realizar
     * @return El propio mapper
     */
    public Mapper<T> createUpdate(String update){
        try {
            statement = connection.prepareStatement(update);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return this;
    }

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
     * Busca de forma automatica una clave foranea y la mapea
     *
     * @param clase   Clase correspondiente a la clave foranea
     * @param pkObject Objeto que tienen un
     * @return Clase mapeada correspondiente a la tupla resultado de la clave
     * foranea. Si la clase indicada no tiene las anotaciones necesarias
     * devuelve un null
     */
    protected Object getFK(Class<?> clase, Object pkObject) {
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
        return new QueryMapper<>(connection)
                .createQuery(queryBuilder.toString()).defineClass(clase).defineParameters(pkObject)
                .findFirst(false);
    }

    /**
     * Imprescindible definir la clase a la que pertenecen los objetos
     * que devuelve la consulta si no es de escritura/modificacion
     *
     * @param mappedClass Clase a la que se mapea el resultado
     * @return El propio mapper porque es un builder
     */
    public Mapper<T> defineClass(Class<? extends T> mappedClass){
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
