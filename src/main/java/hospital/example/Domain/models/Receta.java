package hospital.example.Domain.models;

import hospital.example.Utilities.EstadoReceta;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "recetas")
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "fecha_confeccion", nullable = false)
    private LocalDate fechaConfeccion;

    @Column(name = "fecha_retiro", nullable = false)
    private LocalDate fechaRetiro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReceta estado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "receta_id") // FK en MedicamentoPrescrito
    private List<MedicamentoPrescrito> medicamentos;

    public Receta() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFechaConfeccion() {
        return fechaConfeccion;
    }

    public void setFechaConfeccion(LocalDate fechaConfeccion) {
        this.fechaConfeccion = fechaConfeccion;
    }

    public LocalDate getFechaRetiro() {
        return fechaRetiro;
    }

    public void setFechaRetiro(LocalDate fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }

    public EstadoReceta getEstado() {
        return estado;
    }

    public void setEstado(EstadoReceta estado) {
        this.estado = estado;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public List<MedicamentoPrescrito> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<MedicamentoPrescrito> medicamentos) {
        this.medicamentos = medicamentos;
    }
}