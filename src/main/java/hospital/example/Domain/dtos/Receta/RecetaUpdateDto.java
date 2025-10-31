package hospital.example.Domain.dtos.Receta;

import java.util.Date;

public class RecetaUpdateDto {
    private int idReceta;
    private String estado;
    private Date fechaRetiro;

    public RecetaUpdateDto() {
    }

    public RecetaUpdateDto(int idReceta, String estado, Date fechaRetiro) {
        this.idReceta = idReceta;
        this.estado = estado;
        this.fechaRetiro = fechaRetiro;
    }

    public int getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(int idReceta) {
        this.idReceta = idReceta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaRetiro() {
        return fechaRetiro;
    }

    public void setFechaRetiro(Date fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }
}