package piiksuma.database;


import piiksuma.*;
import piiksuma.api.InsertionFacade;
import piiksuma.api.MultimediaType;
import piiksuma.api.dao.MessagesDao;
import piiksuma.api.dao.MultimediaDao;
import piiksuma.exceptions.PiikDatabaseException;

import javax.management.Query;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Used as an example while building the software
 */
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
        try {
            return (new QueryMapper<User>(this.conexion)).createQuery("SELECT * FROM piiUser;").
                    defineClass(User.class).list();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<Map<String, Object>> test() {

        try {
            return (new QueryMapper<Object>(this.conexion)).createQuery("SELECT * FROM piiUser;").mapList();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
            return null;
        }
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

    public void aTest() {
        try {
            List<Event> result = new QueryMapper<Event>(conexion).defineClass(Event.class).createQuery("SELECT * FROM event;").list();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * //TODO delete this when is not necessary anymore
     * Example of the use of the mappers
     */
    public void meterUsuario() throws PiikDatabaseException {

        User usuario = new QueryMapper<User>(this.conexion).createQuery("SELECT * FROM piiUser where email LIKE ?")
                .defineClass(User.class).defineParameters("email").findFirst();

        User newUsuario = new User("Fran", "Cardama2", "francardama@gmail.com");
        newUsuario.setPass("hola1");
        newUsuario.setBirthday(Timestamp.from(Instant.now()));

        User newUsuario2 = new User("Alvaro", "Goldar", "alvarogoldard@gmail.com");
        newUsuario2.setPass("hola2");
        newUsuario2.setBirthday(Timestamp.from(Instant.now()));

        // Se insertan ambos usuarios en la base de datos
        new InsertionMapper<User>(this.conexion).addAll(newUsuario).defineClass(User.class).insert();
        System.out.println("Se han insertado los siguientes usuarios: " + newUsuario.getId() + ", " +
                newUsuario2.getId());
        printUsers();

        //new DeleteMapper<User>(this.conexion).add(newUsuario).defineClass(User.class).delete();
        System.out.println("Se ha borrado el id: " + newUsuario.getId());
        printUsers();

        new UpdateMapper<User>(this.conexion).add(newUsuario2).defineClass(User.class).createUpdate(
                "UPDATE piiUser SET id = ? WHERE id = ?").defineParameters("idNuevo", "Goldar").executeUpdate();

        System.out.println("Se ha actualizado el usuario: " + newUsuario2.getId());
        printUsers();

        // Modificación nueva a partir de una clase
        // El mapper va a actualizar todos los atributos que no estén a null, esto es útil cuando se va a actualizar
        // todos los atributos. Si queréis actualizar algo en concreto podéis ejecutar la consulta con .executeUpdate()
        newUsuario2.setId("Goldar223");
        newUsuario2.setGender("M");
        new UpdateMapper<User>(this.conexion).add(newUsuario2).defineClass(User.class).update();
        System.out.println("Se ha actualizado el usuario: " + newUsuario2.getId());
        printUsers();

    }

    /**
     * //TODO Delete when it's not necessary anymore
     */
    public void ejemploGETFK(){

        User user = new User("Francisco Javier", "Cardama", "francardama@gmail.com");
        User user2 = new User("Álvaro Goldar", "alvrogd", "alvarogoldard@gmail.com");

        user.setBirthday(Timestamp.from(Instant.now()));
        user2.setBirthday(Timestamp.from(Instant.now()));
        user.setPass("pass1");
        user2.setPass("pass2");

        // El usuario Cardama va a crear un post cuyo id es "id2"
        Post postPadre = new Post(user, Timestamp.from(Instant.now()));
        postPadre.setId("id2");

        user.setPass("pass");
        user2.setPass("pass2");
        user.setBirthday(Timestamp.from(Instant.now()));
        user2.setBirthday(Timestamp.from(Instant.now()));

        // El usuario alvrogd va a responder al post anterior, el id de respuesta va a ser "id43"
        Post postHijo = new Post(user2, Timestamp.from(Instant.now()));
        postHijo.setId("id43");

        postHijo.setText("textohijo");
        postPadre.setText("textopadre");

        // Se establece el post padre
        postHijo.setFatherPost(postPadre);
        postHijo.setText("hola");

        postPadre.setText("hasdas");

        try {
            new InsertionMapper<User>(conexion).defineClass(User.class).addAll(user, user2).insert();
            new InsertionMapper<Post>(conexion).defineClass(Post.class).add(postPadre).insert();
            new InsertionMapper<Post>(conexion).defineClass(Post.class).add(postHijo).insert();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }

        // Se obtienen las claves primarias
        Map<String, Object> fks = null;
        try {
            fks = new QueryMapper<>(getConexion()).getFKs(postHijo);
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }

        /*try {
            new InsertionMapper<User>(conexion).defineClass(User.class).addAll(user, user2).insert();
            new InsertionMapper<Post>(conexion).defineClass(Post.class).add(postPadre).insert();
            new InsertionMapper<Post>(conexion).defineClass(Post.class).add(postHijo).insert();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }*/

        // Se imprimen
        for(String column : fks.keySet()){
            System.out.println(column + " " + fks.get(column).toString());
        }

        List<Post> posts = null;
        try {
            posts = new QueryMapper<Post>(conexion).defineClass(Post.class).createQuery("SELECT * FROM post").list();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }

        for(Post post : posts) {
            System.out.println(post);
        }

        Hashtag hashtag = new Hashtag("patata");
    }

    public void idGenerationTest() {
/*
        System.out.println(conexion);

        User user = new User("Fran", "Cardama", "francardama@gmail.com");
        user.setPass("pass");
        user.setBirthday(new java.sql.Timestamp(1));
        try {
            new InsertionMapper<User>(conexion).defineClass(User.class).addAll(user).insert();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setSection("section");
        ticket.setTextProblem("something");

        Ticket newTicket = null;

        try {
            newTicket = new MessagesDao(conexion).newTicket(ticket);
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }

        System.out.println(newTicket.getId() + "eeeeeeeeeeeeeeeeel id");

        /*User user = new User("Fran", "Cardama", "francardama@gmail.com");
        user.setPass("pass");
        user.setBirthday(new java.sql.Timestamp(1));
        try {
            new InsertionMapper<User>(conexion).defineClass(User.class).addAll(user).insert();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }

        Multimedia multimedia = new Multimedia();
        multimedia.setHash("12334");
        multimedia.setResolution("2323");
        multimedia.setUri("uri");
        multimedia.setType(MultimediaType.image);

        try {
            new MultimediaDao(conexion).addMultimedia(multimedia);
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }*/
    }

    public void pruebasCheck(){
        User user = new User("Fran", "Cardama", "francardama@gmail.com");
        User user2 = new User("Álvaro Goldar", null, "alvarogoldard@gmail.com");


        Post postPadre = new Post(user, Timestamp.from(Instant.now()));
        postPadre.setId("id2");

        // El usuario alvrogd va a responder al post anterior, el id de respuesta va a ser "id43"
        Post postHijo = new Post(user2, Timestamp.from(Instant.now()));
        postHijo.setId("id43");

        // Se establece el post padre
        postHijo.setFatherPost(postPadre);
        postHijo.setText("hola");
/*
        System.out.println(postPadre.checkNotNull() + "-> valor esperado: false | falta el text");
        System.out.println(postPadre.checkPrimaryKey()+ "-> valor esperado: true");

        System.out.println(user.checkPrimaryKey()+ "-> valor esperado: true");
        System.out.println(user.checkNotNull()+ "-> valor esperado: false | falta el cumpleaños");

        System.out.println(user2.checkPrimaryKey()+ "-> valor esperado: false | falta el id");
        System.out.println(user2.checkNotNull()+ "-> valor esperado: false | falta el id y el cumpleaños");

        System.out.println(postHijo.checkPrimaryKey()+ "-> valor esperado: true");
        System.out.println(postHijo.checkNotNull()+ "-> valor esperado: false | el autor no cumple el checkPrimaryKey");
        /*
 */
    }

    public void printUsers() {
        List<User> usuarios = null;
        try {
            usuarios = new QueryMapper<User>(this.conexion).createQuery("SELECT * FROM piiUser where email " +
                    "LIKE ?").defineParameters("%gmail.com").defineClass(User.class).list();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }
        for (User user : usuarios) {
            System.out.println(user);
        }
    }

    /**
     *
     */
    public void mapaSample() {
        List<Map<String, Object>> usuarios = null;
        try {
            usuarios = new QueryMapper<User>(this.conexion).createQuery("SELECT * FROM piiUser where email " +
                    "LIKE ?").defineParameters("%@gmail.com").mapList();
        } catch (PiikDatabaseException e) {
            e.printStackTrace();
        }
        usuarios.forEach(System.out::println);
    }

    public Connection getConexion() {
        return conexion;
    }
}
