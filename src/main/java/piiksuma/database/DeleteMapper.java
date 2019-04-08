package piiksuma.database;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeleteMapper<T> extends Mapper{
    private PreparedStatement statement;
    private List<T> elementsDelete;
    private Class<? extends T> mappedClass;

    /**
     * @param connection Conexión a la base de datos
     */
    public DeleteMapper(Connection connection) {
        super(connection);
    }

    /**
     * Define la clase de los elementos que se están borrando
     * @param clase Clase de los elementos que se van a insertar
     * @return instancia del propio DeleteMapper
     */
    public DeleteMapper<T> defineClass(Class<T> clase){
        this.mappedClass = clase;
        this.elementsDelete = new ArrayList<>();
        return this;
    }

    /**
     * Añade un objeto para borrarlo
     * @param object objeto que se quiere eliminar
     * @return instancia del propio DeleteMapper
     */
    public DeleteMapper<T> add(T object){
        this.elementsDelete.add(object);
        return this;
    }

    /**
     * Añade múltiples objetos para borrarlos
     * @param objects objetos que se quieren borrar
     * @return instancia del propio DeleteMapper
     */
    public DeleteMapper<T> addAll(T... objects){
        this.elementsDelete.addAll(Arrays.asList(objects));
        return this;
    }

    /**
     * Extrae las claves primarias y genera la sentencia SQL correspondiente de borrado
     */
    private void prepareDelete(){
        String columnName;
        StringBuilder deleteBuider = new StringBuilder("DELETE FROM ")
                .append(mappedClass.getAnnotation(MapperTable.class).nombre()).append(" WHERE ");

        // Atributos a insertar
        for (Field field : mappedClass.getDeclaredFields()) {
            field.setAccessible(true);
            if(field.isAnnotationPresent(MapperColumn.class) && field.getAnnotation(MapperColumn.class).pkey()){
                columnName = field.getAnnotation(MapperColumn.class).columna();
                columnName = columnName.equals("") ? field.getName() : columnName;
                deleteBuider.append(columnName);
            }
        }
    }



}
