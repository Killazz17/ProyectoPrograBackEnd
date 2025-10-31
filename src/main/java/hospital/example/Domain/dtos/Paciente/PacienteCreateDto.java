package hospital.example.Domain.dtos.Paciente;

import hospital.example.Utilities.Rol;

import java.util.Date;

public class PacienteCreateDto {
    private int id;
    private String nombre;
    private Rol rol;
    private Date fechaNacimiento;
    private String numeroTelefono;

    public PacienteCreateDto() {}

    public PacienteCreateDto(int id, String nombre, Rol rol, Date fechaNacimiento, String numeroTelefono) {
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
        this.fechaNacimiento = fechaNacimiento;
        this.numeroTelefono = numeroTelefono;
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

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }
}