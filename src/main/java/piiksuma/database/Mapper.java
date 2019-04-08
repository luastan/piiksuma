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
            statement.execute();
            ResultSet set = statement.getResultSet();

            // Metadata parsing
            if(set != null) {
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

    public void executeUpdate() {
        ArrayList<T> resultado = new ArrayList<>();
        String nombreColumna;
        HashSet<String> columnas = new HashSet<>();
        T elemento;
        Class<?> foreignClass;
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
     * Lista con el resultado de la consulta
     *
     * @return Lista de objetos mapeados con el tipo indicado
     */
    public List<T> list() {
        return this.list(true);
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
        return new QueryMapper<>(connection)
                .createQuery(queryBuilder.toString()).defineClass(clase).defineParameters(pkObject)
                .findFirst(false);
    }

    public Mapper<T> createQuery(String query){
        try {
            statement = connection.prepareStatement(query);
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
