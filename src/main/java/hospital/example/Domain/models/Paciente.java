package hospital.example.Domain.models;

import jakarta.persistence.*;
import hospital.example.Utilities.Rol;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pacientes")
public class Paciente extends Usuario {

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaDeNacimiento;

    @Column(name = "numero_telefono", nullable = false, length = 20)
    private String numeroTelefono;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Paciente() {
        super();
    }

    public Paciente(int id, String clave, String nombre, Rol rol, LocalDate fechaDeNacimiento, String numeroTelefono) {
        super(id, clave, nombre, rol);
        this.fechaDeNacimiento = fechaDeNacimiento;
        this.numeroTelefono = numeroTelefono;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // -----------------
    // Getters & Setters
    // -----------------

    public LocalDate getFechaDeNacimiento() {
        return fechaDeNacimiento;
    }

    public void setFechaDeNacimiento(LocalDate fechaDeNacimiento) {
        this.fechaDeNacimiento = fechaDeNacimiento;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Paciente{" +
                "id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", fechaNacimiento=" + fechaDeNacimiento +
                ", numeroTelefono='" + numeroTelefono + '\'' +
                ", rol=" + getRol() +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}