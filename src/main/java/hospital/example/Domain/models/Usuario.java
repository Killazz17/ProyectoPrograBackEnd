package hospital.example.Domain.models;

import jakarta.persistence.*;
import hospital.example.Utilities.Rol;

@MappedSuperclass
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // Identificador único del usuario

    @Column(nullable = false, length = 100)
    private String clave;  // Contraseña para acceder al sistema

    @Column(nullable = false, length = 100)
    private String nombre;  // Nombre completo del usuario

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;  // Rol del usuario en el sistema (MEDICO, PACIENTE, FARMACEUTA, ADMIN)

    public Usuario() {}

    public Usuario(int id, String clave, String nombre, Rol rol) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.rol = rol;
    }

    // -----------------
    // Getters & Setters
    // -----------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", clave='" + clave + '\'' +
                ", nombre='" + nombre + '\'' +
                ", rol=" + rol +
                '}';
    }
}