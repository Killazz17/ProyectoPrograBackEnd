package hospital.example.Domain.models;

import hospital.example.Utilities.Rol;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FARMACEUTA")
public class Farmaceuta extends Usuario {

    public Farmaceuta() {
        super();
    }

    public Farmaceuta(int id, String claveHash, String nombre) {
        super(id, claveHash, nombre, Rol.FARMACEUTA);
    }
}