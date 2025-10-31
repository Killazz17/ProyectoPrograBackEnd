package hospital.example.Domain.models;

import hospital.example.Utilities.Rol;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("MEDICO")
public class Medico extends Usuario {

    @Column(length = 100)
    private String especialidad;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Medico() {
        super();
    }

    public Medico(int id, String claveHash, String nombre, String especialidad) {
        super(id, claveHash, nombre, Rol.MEDICO);
        this.especialidad = especialidad;
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
    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
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
}