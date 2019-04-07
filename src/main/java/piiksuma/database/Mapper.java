package piiksuma.database;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Arrays;

public abstract class Mapper {
    protected Connection connection;


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
}
