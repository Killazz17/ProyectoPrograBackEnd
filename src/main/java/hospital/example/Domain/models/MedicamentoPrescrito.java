package hospital.example.Domain.models;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("MEDICAMENTO_PRESCRITO")
public class MedicamentoPrescrito extends Medicamento {

    @Column(name = "cantidad")
    private int cantidad;

    @Column(name = "duracion")
    private int duracion;

    @Column(name = "indicaciones", length = 300)
    private String indicaciones;

    // No necesitas el campo receta_id aquí, se maneja por la relación en Receta

    public MedicamentoPrescrito() {
        super();
    }

    public MedicamentoPrescrito(String codigo, String nombre, String presentacion,
                                int cantidad, int duracion, String indicaciones) {
        super(codigo, nombre, presentacion);
        this.cantidad = cantidad;
        this.duracion = duracion;
        this.indicaciones = indicaciones;
    }

    // Getters y Setters
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }
}