package hospital.example.Domain.dtos.Medico;

public class MedicoResponseDto {
    private int id;
    private String nombre;
    private String especialidad;

    public MedicoResponseDto() {}

    public MedicoResponseDto(int id, String nombre, String especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }
}