package hospital.example.Domain.models;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("MEDICAMENTO_PRESCRITO") // opcional, para diferenciar tipos
public class MedicamentoPrescrito extends Medicamento {

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false)
    private int duracion;

    @Column(nullable = false, length = 300)
    private String indicaciones;

    @Column(name = "receta_id", nullable = false)
    private int recetaId;

    public MedicamentoPrescrito() {}

    public MedicamentoPrescrito(String codigo, String nombre, String presentacion,
                                int cantidad, int duracion, String indicaciones) {
        super(codigo, nombre, presentacion);
        this.cantidad = cantidad;
        this.duracion = duracion;
        this.indicaciones = indicaciones;
    }

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