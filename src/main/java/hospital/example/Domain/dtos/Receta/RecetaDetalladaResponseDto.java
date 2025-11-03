// src/main/java/hospital/example/Domain/dtos/Receta/RecetaDetalladaResponseDto.java
package hospital.example.Domain.dtos.Receta;

import hospital.example.Domain.dtos.MedicamentoPrescrito.MedicamentoPrescritoResponseDto;

import java.util.List;

public class RecetaDetalladaResponseDto {
    private int id;
    private int idPaciente;
    private int idMedico;
    private String fechaConfeccion; // formato: yyyy-MM-dd
    private String fechaRetiro;
    private String estado;
    private List<MedicamentoPrescritoResponseDto> medicamentos;

    public RecetaDetalladaResponseDto() {}

    public RecetaDetalladaResponseDto(int id, int idPaciente, int idMedico,
                                      String fechaConfeccion, String fechaRetiro,
                                      String estado,
                                      List<MedicamentoPrescritoResponseDto> medicamentos) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.fechaConfeccion = fechaConfeccion;
        this.fechaRetiro = fechaRetiro;
        this.estado = estado;
        this.medicamentos = medicamentos;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public String getFechaConfeccion() { return fechaConfeccion; }
    public void setFechaConfeccion(String fechaConfeccion) {
        this.fechaConfeccion = fechaConfeccion;
    }

    public String getFechaRetiro() { return fechaRetiro; }
    public void setFechaRetiro(String fechaRetiro) { this.fechaRetiro = fechaRetiro; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<MedicamentoPrescritoResponseDto> getMedicamentos() {
        return medicamentos;
    }
    public void setMedicamentos(List<MedicamentoPrescritoResponseDto> medicamentos) {
        this.medicamentos = medicamentos;
    }
}