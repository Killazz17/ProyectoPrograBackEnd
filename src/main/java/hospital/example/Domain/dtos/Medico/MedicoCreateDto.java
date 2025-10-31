package hospital.example.Domain.dtos.Medico;

import hospital.example.Utilities.Rol;

public class MedicoCreateDto {
    private int id;
    private String nombre;
    private Rol rol;
    private String especialidad;

    public MedicoCreateDto() {}

    public MedicoCreateDto(int id, String nombre, Rol rol, String especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
        this.especialidad = especialidad;
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

    public String getEspecialidad() {
        return especialidad;
    }
}