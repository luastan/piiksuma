package piiksuma.database;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UpdateMapper<T> extends Mapper<T> {
    private Class<T> clase;
    private List<T> elementsUpdate;
    String update;
    private ArrayList<String> columnNames;
    private HashMap<String, Field> attributes;

    public UpdateMapper(Connection connection) {
        super(connection);
        this.elementsUpdate = new ArrayList<>();
        this.update = "";
        this.columnNames = new ArrayList<>();
        this.attributes = new HashMap<>();
    }

    /**
     * Define la clase de los elementos que se están actualizando
     * @param clase clase de los elementos que se van a actualizar
     * @return instancia del propio UpdateMapper
     */
    @Override
    public UpdateMapper<T> defineClass(Class<? extends T> clase){
        super.defineClass(clase);
        return this;
    }

    /**
     * Añade un objeto para actualizarlo
     * @param object objeto que se quiere actualizar
     * @return instancia del propio UpdateMapper
     */
    public UpdateMapper<T> add(T object){
        this.elementsUpdate.add(object);
        return this;
    }

    /**
     * Añade múltiples objetos para actualizarlos
     * @param objects objetos que se quieren actualizar
     * @return instancia del propio UpdateMapper
     */
    public UpdateMapper<T> addAll(T... objects){
        this.elementsUpdate.addAll(Arrays.asList(objects));
        return this;
    }

    @Override
    public UpdateMapper<T> defineParameters(Object... parametros) {
        super.defineParameters(parametros);
        return this;
    }

    public void update(){
        PreparedStatement statement;
        String columnName;
        StringBuilder updateBuilder = new StringBuilder("UPDATE ").append(mappedClass.getAnnotation(MapperTable.class)
                .nombre()).append(" SET ");

        // Se recorren los objetos a actualizar
        for(T objectUpdate : this.elementsUpdate) {

            // Se recorren los atributos a actualizar
            for (Field field : mappedClass.getDeclaredFields()) {
                // Se hace accesible
                field.setAccessible(true);

                // Se comprueba que el atributo tiene @MapperColumn y que no es null
                try {
                    if (field.isAnnotationPresent(MapperColumn.class) && field.get(objectUpdate) != null) {
                        // Se obtiene el nombre de la columna de la field actual
                        columnName = field.getAnnotation(MapperColumn.class).columna();

                        // Se escoge o entre el nombre del atributo o el nombre indicado en el Mapper
                        columnName = columnName.equals("") ? field.getName() : columnName;

                        // Se añade el valor de la columna a modificar y el interrogante para el valor
                        updateBuilder.append(columnName).append(" = ?,");

                        // Se añade la columna al Array
                        this.columnNames.add(columnName);

                        // Se añade la field al HashMap
                        this.attributes.put(columnName, field);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            // Se borra la última coma añadida en el SET
            updateBuilder.deleteCharAt(updateBuilder.length() - 1);

            // Se añade la condición del where con las claves primarias
            updateBuilder.append(" WHERE ");
            for(Field field : mappedClass.getDeclaredFields()){
                // Se hace accesible el atributo
                field.setAccessible(true);

                // Se comprueba que el atributo esté mapeado y sea primary key
                if(field.isAnnotationPresent(MapperColumn.class) && field.getAnnotation(MapperColumn.class).pkey()){
                    // Se obtiene el nombre de la columna de la field actual
                    columnName = field.getAnnotation(MapperColumn.class).columna();

                    // Se escoge o entre el nombre del atributo o el nombre indicado en el Mapper
                    columnName = columnName.equals("") ? field.getName() : columnName;

                    // Se añade el valor de la columna a modificar y el interrogante para el valor
                    updateBuilder.append(columnName).append(" = ?,");

                    // Se añade la columna al Array
                    this.columnNames.add(columnName);

                    // Se añade la field al HashMap
                    this.attributes.put(columnName, field);
                }
            }

            // Se borra la última coma añadida del WHERE
            updateBuilder.deleteCharAt(updateBuilder.length() - 1);

            try{
                statement = connection.prepareStatement(updateBuilder.toString());

                // Se recorren todas las columnas que se van a insertar
                for(int i = 0; i < this.columnNames.size(); i++){
                    // Se inserta el valor del atributo
                    statement.setObject(i + 1, this.attributes.get(this.columnNames.get(i)).get(objectUpdate));
                }

                // Se ejecuta la actualización
                statement.executeUpdate();
                statement.close();
            } catch (SQLException | IllegalAccessException e) {
                e.printStackTrace();
            }

            // Se vacía el array de las columnas
            this.columnNames.clear();

            // Se vacía el HashMap
            this.attributes.clear();
        }
    }

}
