package hospital.example.Domain.models;

import hospital.example.Utilities.Rol;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {

    @Id
    private int id;

    @Column(name = "clave_hash", length = 255)
    private String claveHash;

    @Column(name = "salt", length = 255)
    private String salt;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // --- Constructores ---
    public Usuario() {}

    public Usuario(int id, String claveHash, String nombre, Rol rol) {
        this.id = id;
        this.claveHash = claveHash;
        this.nombre = nombre;
        this.rol = rol;
    }

    // --- Getters y Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getClaveHash() { return claveHash; }
    public void setClaveHash(String claveHash) { this.claveHash = claveHash; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}