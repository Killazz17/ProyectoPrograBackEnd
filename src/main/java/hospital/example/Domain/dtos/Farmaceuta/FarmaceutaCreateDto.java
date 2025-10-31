package hospital.example.Domain.dtos.Farmaceuta;

import hospital.example.Utilities.Rol;

public class FarmaceutaCreateDto {
    private int id;
    private String nombre;
    private Rol rol;

    public FarmaceutaCreateDto() {}

    public FarmaceutaCreateDto(int id, String nombre, Rol rol) {
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