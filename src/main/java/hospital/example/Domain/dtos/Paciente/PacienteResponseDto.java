package hospital.example.Domain.dtos.Paciente;

import java.util.Date;

public class PacienteResponseDto {
    private int id;
    private String nombre;
    private Date fechaNacimiento;
    private String numeroTelefono;

    public PacienteResponseDto() {}

    public PacienteResponseDto(int id, String nombre, Date fechaNacimiento, String numeroTelefono) {
        this.id = id;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.numeroTelefono = numeroTelefono;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }
}