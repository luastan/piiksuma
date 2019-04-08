package piiksuma.database;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UpdateMapper<T> extends Mapper<T> {
    private Class<T> clase;
    private List<T> elementsUpdate;
    String update;
    private ArrayList<String> columnsName;
    private HashMap<String, Field> attributes;

    public UpdateMapper(Connection connection) {
        super(connection);
        this.elementsUpdate = new ArrayList<>();
        this.update = "";
        this.columnsName = new ArrayList<>();
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

}
