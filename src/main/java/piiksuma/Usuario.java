package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

@MapperTable(nombre = "usuarios")
public class Usuario {

    @MapperColumn
    private String nombre;
    @MapperColumn(pkey = true, columna = "id")
    private String idUsuario;
    @MapperColumn
    private String email;

    public Usuario(String nombre, String idUsuario, String email) {
        this.nombre = nombre;
        this.idUsuario = idUsuario;
        this.email = email;
    }

    /**
     * Necesario constructor vacío para que se pueda crear con reflección
     */
    public Usuario() {
        //super();
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", idUsuario='" + idUsuario + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
