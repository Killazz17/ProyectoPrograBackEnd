package hospital.example.Domain.dtos.Receta;

import hospital.example.Domain.dtos.MedicamentoPrescrito.MedicamentoPrescritoDto;

import java.util.List;

public class RecetaResponseDto {
    private int id;
    private String estado;
    private String fechaConfeccion;
    private String fechaRetiro;
    private String pacienteNombre;
    private List<MedicamentoPrescritoDto> medicamentos;

    public RecetaResponseDto() {}

    public RecetaResponseDto(int id, String estado, String fechaConfeccion, String fechaRetiro,
                             String pacienteNombre, List<MedicamentoPrescritoDto> medicamentos) {
        this.id = id;
        this.estado = estado;
        this.fechaConfeccion = fechaConfeccion;
        this.fechaRetiro = fechaRetiro;
        this.pacienteNombre = pacienteNombre;
        this.medicamentos = medicamentos;
    }

    public int getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }

    public String getFechaConfeccion() {
        return fechaConfeccion;
    }

    public String getFechaRetiro() {
        return fechaRetiro;
    }

    public String getPacienteNombre() {
        return pacienteNombre;
    }

    public List<MedicamentoPrescritoDto> getMedicamentos() {
        return medicamentos;
    }
}