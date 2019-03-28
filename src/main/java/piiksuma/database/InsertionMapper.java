package myCoolApp.database;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InsertionMapper<E> {
    private Connection conexion;
    private Class<E> clase;
    private List<E> inserciones;
    private String query;
    private ArrayList<String> columnas;
    private HashMap<String, Field> atributos;

    public InsertionMapper(Connection conexion) {
        this.conexion = conexion;
        inserciones = new ArrayList<>();
        query = "";
        columnas = new ArrayList<>();
        atributos = new HashMap<>();
    }

    public InsertionMapper<E> definirClase(Class<E> clase) {
        this.clase = clase;
        return this;
    }

    public InsertionMapper<E> add(E objeto) {
        inserciones.add(objeto);
        return this;
    }

    public InsertionMapper<E> addAll(E... objetos) {
        inserciones.addAll(Arrays.asList(objetos));
        return this;
    }

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

    public void insertar() {
        PreparedStatement statement = null;
        // Peparar la sentencia
        prepareQuery();
        try {
            for (E insercion : this.inserciones) {
                statement = conexion.prepareStatement(this.query);  // Construir PreparedStatement
                for (int i = 0; i < this.columnas.size(); i++) {  // Insertar datos
                    statement.setObject(i, this.atributos.get(this.columnas.get(i)).get(insercion));
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
}
