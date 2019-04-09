package piiksuma.database;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DeleteMapper<T> extends Mapper<T>{
    private List<T> elementsDelete;
    private String deleteUpdate;
    private ArrayList<String> columnsName;
    private HashMap<String, Field> attributes;

    /**
     * @param connection Conexión a la base de datos
     */
    public DeleteMapper(Connection connection) {
        super(connection);
        elementsDelete = new ArrayList<>();
        deleteUpdate = "";
        columnsName = new ArrayList<>();
        attributes = new HashMap<>();
    }

    /**
     * Define la clase de los elementos que se están borrando
     * @param clase Clase de los elementos que se van a insertar
     * @return instancia del propio DeleteMapper
     */
    @Override
    public DeleteMapper<T> defineClass(Class<? extends T> clase){
        super.defineClass(clase);
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

        // StringBuilder para la eliminación. Se obtiene el nombre de la relación de la base de datos asociada
        // a la clase mapeada
        StringBuilder deleteBuilder = new StringBuilder("DELETE FROM ")
                .append(mappedClass.getAnnotation(MapperTable.class).nombre()).append(" WHERE ");

        // Se recorren los atributos de la clase mapeada
        for (Field field : mappedClass.getDeclaredFields()) {
            // Se hace el atributo accesible
            field.setAccessible(true);
            // Se comprueba que haya una anotación del Mapper en ese atributo y que sea una primary key
            if(field.isAnnotationPresent(MapperColumn.class) && field.getAnnotation(MapperColumn.class).pkey()){
                // Se obtiene el nombre de la columna
                columnName = field.getAnnotation(MapperColumn.class).columna();

                // En caso de que el nombre de la columna esté vacío se utiliza el del atributo
                columnName = columnName.equals("") ? field.getName() : columnName;

                // Se añade el nombre de la columna y se iguala al parámetro indicado
                deleteBuilder.append(columnName).append(" = ? and ");

                // Se añade el nombre de la columna al ArrayList de columnas
                this.columnsName.add(columnName);

                // Se añade el field al HashMap en función del nombre de la columna
                this.attributes.put(columnName, field);
            }
        }

        // Borra el último and añadido
        deleteBuilder.delete(deleteBuilder.length()-4, deleteBuilder.length());

        // Se añade al atributo deleteUpdate la consulta de eliminación
        deleteUpdate = deleteBuilder.toString();

        try{
            this.statement = super.connection.prepareStatement(deleteUpdate);
        } catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Elimina todos los objetos indicados
     */
    public void delete(){

        prepareDelete();
        // Se llama a la función para preparar el borrado
        try {
            // Se recorren todos los objetos que se han añadido para el borrado
            for(T object : this.elementsDelete){
                // Se prepara el borrado
                this.statement = connection.prepareStatement(this.deleteUpdate);

                // Se recorre el nombre de los atributos que son claves primaria
                for(int i = 0; i < this.columnsName.size(); i++){
                    // Se obtiene la Field correspondiente del HashMap
                    statement.setObject(i + 1, this.attributes.get(this.columnsName.get(i)).get(object));
                }

                this.statement.executeUpdate();
            }
        } catch(SQLException | IllegalAccessException e){
            e.printStackTrace();
        }
    }



}
