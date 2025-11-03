package hospital.example.Domain.models;

import jakarta.persistence.*;

@Entity
@Table(name = "medicamentos_prescritos")
public class MedicamentoPrescrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // ✅ CAMBIO CRÍTICO: Relación bidireccional con Receta
    @ManyToOne(optional = false)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    @Column(name = "medicamento_codigo", nullable = false, length = 20)
    private String medicamentoCodigo;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "duracion", nullable = false)
    private int duracion;

    @Column(name = "indicaciones", length = 300)
    private String indicaciones;

    // Campos transitorios para UI (NO se guardan en BD)
    @Transient
    private String nombre;

    @Transient
    private String presentacion;

    public MedicamentoPrescrito() {}

    public MedicamentoPrescrito(String medicamentoCodigo, int cantidad, int duracion, String indicaciones) {
        this.medicamentoCodigo = medicamentoCodigo;
        this.cantidad = cantidad;
        this.duracion = duracion;
        this.indicaciones = indicaciones;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // ✅ NUEVO: Getter/Setter para receta
    public Receta getReceta() { return receta; }
    public void setReceta(Receta receta) { this.receta = receta; }

    public String getMedicamentoCodigo() { return medicamentoCodigo; }
    public void setMedicamentoCodigo(String medicamentoCodigo) {
        this.medicamentoCodigo = medicamentoCodigo;
    }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public int getDuracion() { return duracion; }
    public void setDuracion(int duracion) { this.duracion = duracion; }

    public String getIndicaciones() { return indicaciones; }
    public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPresentacion() { return presentacion; }
    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }
}