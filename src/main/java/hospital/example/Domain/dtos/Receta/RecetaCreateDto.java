package hospital.example.Domain.dtos.Receta;

import hospital.example.Domain.dtos.MedicamentoPrescrito.MedicamentoPrescritoDto;

import java.util.Date;
import java.util.List;

public class RecetaCreateDto {
    private int pacienteId;
    private Date fechaRetiro;
    private List<MedicamentoPrescritoDto> medicamentos;

    public RecetaCreateDto() {}

    public int getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(int pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Date getFechaRetiro() {
        return fechaRetiro;
    }

    public void setFechaRetiro(Date fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }

    public List<MedicamentoPrescritoDto> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<MedicamentoPrescritoDto> medicamentos) {
        this.medicamentos = medicamentos;
    }
}