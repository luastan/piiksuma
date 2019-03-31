package piiksuma.database;


import java.io.FileInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class FachadaBDD {
    private static FachadaBDD db;
    private Connection conexion;


    private FachadaBDD() {
        Properties configuracion = new Properties();
        FileInputStream arqConfiguracion;

        try {
            arqConfiguracion = new FileInputStream(getClass().getResource("/baseDatos.properties").getPath());
            configuracion.load(arqConfiguracion);
            arqConfiguracion.close();
            Properties usuario = new Properties();

            String gestor = configuracion.getProperty("gestor");

            usuario.setProperty("user", configuracion.getProperty("usuario"));
            usuario.setProperty("password", configuracion.getProperty("clave"));

            conexion = java.sql.DriverManager.getConnection("jdbc:"+gestor+"://"+
                            configuracion.getProperty("servidor")+":"+
                            configuracion.getProperty("puerto")+"/"+
                            configuracion.getProperty("baseDatos"),
                    usuario);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static {
        db = new FachadaBDD();
    }
    public static FachadaBDD getDb() {
        return db;
    }

    public static void setDb(FachadaBDD db) {
        FachadaBDD.db = db;
    }

    public List<Map<String, Object>> test() {


        return (new QueryMapper<Object>(this.conexion)).crearConsulta("SELECT * FROM usuarios;").mapList();
    }
/*

    public List<Usuario> usuarios() {
        return (new QueryMapper<Usuario>(this.conexion)).crearConsulta("SELECT nombre, tipo_usuario FROM usuario WHERE tipo_usuario=?").
                definirEntidad(Usuario.class).definirParametros("Normal").list();
    }


    public void nuevoUsuario(Usuario usuario) {
        //(new QueryMapper<Usuario>(this.conexion)).definirEntidad(Usuario.class).insertar("usuario", usuario);
        (new InsertionMapper<Usuario>(this.conexion)).definirClase(Usuario.class).add(usuario).insertar();
    }
    */

    public List<Integer> numList() {
        return new ArrayList<>();
    }
}
