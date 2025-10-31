package hospital.example.Domain.dtos.Farmaceuta;

public class FarmaceutaResponseDto {
    private int id;
    private String nombre;

    public FarmaceutaResponseDto() {}

    public FarmaceutaResponseDto(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}