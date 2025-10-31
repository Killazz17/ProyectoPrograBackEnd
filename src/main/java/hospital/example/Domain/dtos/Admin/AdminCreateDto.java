package hospital.example.Domain.dtos.Admin;

import hospital.example.Utilities.Rol;

public class AdminCreateDto {
    private int id;
    private String nombre;
    private Rol rol;

    public AdminCreateDto() {}

    public AdminCreateDto(int id, String nombre, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Rol getRol() {
        return rol;
    }
}