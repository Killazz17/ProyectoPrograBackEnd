package hospital.example.Domain.models;

import jakarta.persistence.*;
import hospital.example.Utilities.Rol;
import java.time.LocalDateTime;

@Entity
@Table(name = "farmaceutas")
public class Farmaceuta extends Usuario {

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Farmaceuta() {
        super();
    }

    public Farmaceuta(int id, String clave, String nombre, Rol rol) {
        super(id, clave, nombre, rol);
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
        return "Farmaceuta{" +
                "id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", rol=" + getRol() +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}