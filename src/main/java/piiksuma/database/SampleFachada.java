package piiksuma.database;


import piiksuma.User;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SampleFachada {
    private static SampleFachada db;
    private Connection conexion;


    private SampleFachada() {
        Properties configuracion = new Properties();
        FileInputStream arqConfiguracion;

        try {
            System.out.println(getClass().getResource("/baseDatos.properties").getPath());
            arqConfiguracion = new FileInputStream(getClass().getResource("/baseDatos.properties").getPath());
            configuracion.load(arqConfiguracion);
            arqConfiguracion.close();
            Properties usuario = new Properties();

            String gestor = configuracion.getProperty("gestor");

            usuario.setProperty("user", configuracion.getProperty("usuario"));
            usuario.setProperty("password", configuracion.getProperty("clave"));

            conexion = java.sql.DriverManager.getConnection("jdbc:" + gestor + "://" +
                            configuracion.getProperty("servidor") + ":" +
                            configuracion.getProperty("puerto") + "/" +
                            configuracion.getProperty("baseDatos"),
                    usuario);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static {
        db = new SampleFachada();
    }

    public static SampleFachada getDb() {
        return db;
    }

    public static void setDb(SampleFachada db) {
        SampleFachada.db = db;
    }


    public List<User> usuarios() {
        return (new QueryMapper<User>(this.conexion)).createQuery("SELECT * FROM piiUser;").
                defineClass(User.class).list();
    }


    public List<Map<String, Object>> test() {


        return (new QueryMapper<Object>(this.conexion)).createQuery("SELECT * FROM piiUser;").mapList();
    }
/*

    public List<User> usuarios() {
        return (new QueryMapper<User>(this.conexion)).crearConsulta("SELECT nombre, tipo_usuario FROM usuario WHERE tipo_usuario=?").
                definirEntidad(User.class).definirParametros("Normal").list();
    }


    public void nuevoUsuario(User usuario) {
        //(new QueryMapper<User>(this.conexion)).definirEntidad(User.class).insertar("usuario", usuario);
        (new InsertionMapper<User>(this.conexion)).definirClase(User.class).add(usuario).insertar();
    }
*/

    public List<Integer> numList() {
        return new ArrayList<>();
    }

    /**
     * //TODO borrar esto cuando ya no sea necesario
     * Ejemplo para el uso de los mappers :)
     */
    public void meterUsuario() {

        User usuario = new QueryMapper<User>(this.conexion).createQuery("SELECT * FROM piiUser where email LIKE ?")
                .defineClass(User.class).defineParameters("email").findFirst();

        User newUsuario = new User("Fran", "Cardama", "francardama@gmail.com");
        newUsuario.setPass("hola1");
        newUsuario.setBirthday(Timestamp.from(Instant.now()));

        User newUsuario2 = new User("Alvaro", "Goldar", "alvarogoldard@gmail.com");
        newUsuario2.setPass("hola2");
        newUsuario2.setBirthday(Timestamp.from(Instant.now()));

        new InsertionMapper<User>(this.conexion).add(newUsuario).defineClass(User.class).insert();
        new InsertionMapper<User>(this.conexion).add(newUsuario2).defineClass(User.class).insert();

        new DeleteMapper<User>(this.conexion).add(newUsuario).defineClass(User.class).delete();

        new UpdateMapper<User>(this.conexion).add(newUsuario2).defineClass(User.class).createUpdate(
                "UPDATE piiUser SET id = ? WHERE id = ?").defineParameters("idNuevo", "Goldar").executeUpdate();

        List<User> usuarios = new QueryMapper<User>(this.conexion).createQuery("SELECT * FROM piiUser where email " +
                "LIKE ?").defineParameters("%gmail.com").defineClass(User.class).list();
        for (User user : usuarios) {
            System.out.println(user);
        }

        // Modificación nueva a partir de una clase
        // El mapper va a actualizar todos los atributos que no estén a null, esto es útil cuando se va a actualizar
        // todos los atributos. Si queréis actualizar algo en concreto podéis ejecutar la consulta con .executeUpdate()
        newUsuario2.setId("Goldar22");
        new UpdateMapper<User>(this.conexion).add(newUsuario2).defineClass(User.class).update();

        usuarios = new QueryMapper<User>(this.conexion).createQuery("SELECT * FROM piiUser where email " +
                "LIKE ?").defineParameters("%gmail.com").defineClass(User.class).list();
        for (User user : usuarios) {
            System.out.println(user);
        }

    }
}
