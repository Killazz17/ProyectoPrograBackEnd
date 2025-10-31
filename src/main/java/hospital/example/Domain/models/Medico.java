package hospital.example.Domain.models;

import hospital.example.Utilities.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MEDICO")
public class Medico extends Usuario {

    @Column(length = 100)
    private String especialidad;

    public Medico() {
        super();
    }

    public Medico(int id, String claveHash, String nombre, String especialidad) {
        super(id, claveHash, nombre, Rol.MEDICO);
        this.especialidad = especialidad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
}