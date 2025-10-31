package hospital.example.Domain.models;

import hospital.example.Utilities.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.Date;

@Entity
@DiscriminatorValue("PACIENTE")
public class Paciente extends Usuario {

    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    @Column(name = "numero_telefono", length = 20)
    private String numeroTelefono;

    public Paciente() {
        super();
    }

    public Paciente(int id, String claveHash, String nombre, Date fechaNacimiento, String numeroTelefono) {
        super(id, claveHash, nombre, Rol.PACIENTE);
        this.fechaNacimiento = fechaNacimiento;
        this.numeroTelefono = numeroTelefono;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }
}