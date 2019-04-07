package piiksuma.database;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;


/**
 * Wrapper de conexion a la base de datos. Permite insertar datos sin necesidad
 * de construir sentencias SQL a mano.
 *
 * @param <E> Clase de los objetos que se van a insertar
 */
public class InsertionMapper<E> extends Mapper {
    private Class<E> clase;
    private List<E> inserciones;
    private String query;
    private ArrayList<String> columnas;
    private HashMap<String, Field> atributos;


    public InsertionMapper(Connection conexion) {
        super(conexion);
        inserciones = new ArrayList<>();
        query = "";
        columnas = new ArrayList<>();
        atributos = new HashMap<>();
    }


    /**
     * Define la clase de los elementos que se están insertando
     * @param clase Clase de los elementos que se van a insertar
     * @return Instancia del propio IsertionMApper
     */
    public InsertionMapper<E> definirClase(Class<E> clase) {
        this.clase = clase;
        return this;
    }


    /**
     * Añade un objeto para insertarlo
     *
     * @param objeto Objeto que se quiere insertar
     * @return El propio insertionMapper
     */
    public InsertionMapper<E> add(E objeto) {
        inserciones.add(objeto);
        return this;
    }


    /**
     * Añade múltiples objetos para insertarlos
     *
     * @param objetos Objetos que se quieren insertar
     * @return El propio insertionMapper
     */
    public InsertionMapper<E> addAll(E... objetos) {
        inserciones.addAll(Arrays.asList(objetos));
        return this;
    }


    /**
     * Extrae los atributos que serán insertados en la base de datos y genera
     * el esqueleto de la sentencia SQL correspondiente para insertarlos
     */
    private void prepareQuery() {
        String nombreColumna;
        StringBuilder queryBuilder = new StringBuilder("INSERT INTO ")
                .append(clase.getAnnotation(MapperTable.class).nombre()).append("(");

        // Fetching de atributos a insertar
        for (Field field : clase.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(MapperColumn.class)) {
                nombreColumna = field.getAnnotation(MapperColumn.class).columna();
                nombreColumna = nombreColumna.equals("") ? field.getName() : nombreColumna;
                queryBuilder.append(nombreColumna).append(",");
                this.columnas.add(nombreColumna);
                this.atributos.put(nombreColumna, field);
            }
        }
        queryBuilder.deleteCharAt(queryBuilder.length() - 1);  // Borra la última coma
        queryBuilder.append(") VALUES (?");
        for (int i = 0; i < this.atributos.size() - 1; i++) {  // Ya se pone un ? antes por eso se resta 1
            queryBuilder.append(",?");
        }
        queryBuilder.append(");");
        query = queryBuilder.toString();
    }




    /**
     * Realiza la insercion de todos los elementos que se añadieron al mapper
     */
    public void insertar() {
        PreparedStatement statement = null;
        // Peparar la sentencia
        prepareQuery();
        try {
            for (E insercion : this.inserciones) {
                statement = connection.prepareStatement(this.query);  // Construir PreparedStatement
                for (int i = 0; i < this.columnas.size(); i++) {  // Insertar datos
                    if (this.atributos.get(this.columnas.get(i)).get(insercion).getClass().isAnnotationPresent(MapperTable.class)) {
                        statement.setObject(i + 1, fkValue(this.atributos.get(this.columnas.get(i)).get(insercion)));
                    } else {
                        statement.setObject(i + 1, this.atributos.get(this.columnas.get(i)).get(insercion));
                    }

                }
                // Ejecutar
                statement.execute();
            }
        } catch (SQLException e) {
            System.out.println("SQL movida");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * Devuelve un Map que sirve como plantilla para s
     * @return Map con la estructura para insertar datos
     */
    public static Map<String, Object> customInsertionTemplate() {
        HashMap<String, Object> insertionTemplate = new HashMap<>();

        insertionTemplate.put("table", "");
        insertionTemplate.put("set", new ArrayList<Map<String, Object>>());
        insertionTemplate.put("where", new ArrayList<Map<String, Object>>());

        return insertionTemplate;
    }

    /**
     * Genera las sentencias necesarias para insertar los elementos colocados
     * en un mapa con la siguiente extructura:
     *
     * {
     *     "table" : "Nombre de la tabla",
     *     "set" : [
     *          {
     *              "nombre columna" : "valor"
     *          },
     *          {
     *              "nombre columna" : "valor puede ser un numero y no String"
     *          }
     *     ],
     *     "where" : [
     *          {
     *              "nombre columna" : "valor"
     *          },
     *          {
     *              "nombre columna" : "valor"
     *          }
     *     ]
     * }
     *
     *
     * @param insertions Lista de hashmaps con la estructura exeplicada
     */
    public void customInsertion(List<Map<String, Object>> insertions) {

    }


}
