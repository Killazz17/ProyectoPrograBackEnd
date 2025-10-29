package hospital.example.Domain.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import hospital.example.Utilities.EstadoReceta;
import hospital.example.Domain.models.Paciente;

@Entity
@Table(name = "recetas")
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_receta")
    private int idReceta;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "receta_id") // FK en MedicamentoPrescrito
    private List<MedicamentoPrescrito> medicamentoPrescritos = new ArrayList<>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false, foreignKey = @ForeignKey(name = "fk_receta_paciente"))
    private Paciente paciente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReceta estado = EstadoReceta.confeccionada;

    @Column(name = "fecha_confeccion", nullable = false)
    private LocalDate fechaConfeccion;

    @Column(name = "fecha_retiro", nullable = false)
    private LocalDate fechaRetiro;

    public Receta() {}

    public Receta(List<MedicamentoPrescrito> medicamentoPrescritos, Paciente paciente, LocalDate fechaConfeccion, LocalDate fechaRetiro) {
        this.medicamentoPrescritos = medicamentoPrescritos;
        this.paciente = paciente;
        this.estado = EstadoReceta.confeccionada;
        this.fechaConfeccion = fechaConfeccion;
        this.fechaRetiro = fechaRetiro;
    }

    public void finalizarReceta() {
        this.estado = EstadoReceta.lista;
    }

    // -----------------
    // Getters & Setters
    // -----------------

    public int getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(int idReceta) {
        this.idReceta = idReceta;
    }

    public List<MedicamentoPrescrito> getMedicamentos() {
        return medicamentoPrescritos;
    }

    public void setMedicamentos(List<MedicamentoPrescrito> medicamentoPrescritos) {
        this.medicamentoPrescritos = medicamentoPrescritos;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public EstadoReceta getEstado() {
        return estado;
    }

    public void setEstado(EstadoReceta estado) {
        this.estado = estado;
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

    // -----------------
    // Lógica de medicamentos
    // -----------------

    public void agregarMed(MedicamentoPrescrito med) {
        medicamentoPrescritos.add(med);
    }

    public int buscarMed(MedicamentoPrescrito med) {
        for (int i = 0; i < medicamentoPrescritos.size(); i++) {
            if (medicamentoPrescritos.get(i).getCodigo().equals(med.getCodigo())) {
                return i;
            }
        }
        return -1;
    }

    public int buscarXCodigoMed(String codigo) {
        for (int i = 0; i < medicamentoPrescritos.size(); i++) {
            if (medicamentoPrescritos.get(i).getCodigo().equals(codigo)) {
                return i;
            }
        }
        return -1;
    }

    public void actualizarMed(MedicamentoPrescrito med) {
        int pos = buscarMed(med);
        if (pos >= 0)
            medicamentoPrescritos.set(pos, med);
    }

    public void borrarMed(String codigo) {
        int pos = buscarXCodigoMed(codigo);
        if (pos >= 0)
            medicamentoPrescritos.remove(pos);
    }

    @Override
    public String toString() {
        return "Receta{" +
                "id=" + idReceta +
                ", paciente=" + (paciente != null ? paciente.getId() : "null") +
                ", estado=" + estado +
                ", fechaConfeccion=" + fechaConfeccion +
                ", fechaRetiro=" + fechaRetiro +
                '}';
    }
}