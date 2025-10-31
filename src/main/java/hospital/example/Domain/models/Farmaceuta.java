package hospital.example.Domain.models;

import hospital.example.Utilities.Rol;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("FARMACEUTA")
public class Farmaceuta extends Usuario {

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Farmaceuta() {
        super();
    }

    public Farmaceuta(int id, String claveHash, String nombre) {
        super(id, claveHash, nombre, Rol.FARMACEUTA);
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

    // Getters y Setters
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
}