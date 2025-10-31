package hospital.example.Domain.dtos.Admin;

public class AdminResponseDto {
    private int id;
    private String nombre;

    public AdminResponseDto() {}

    public AdminResponseDto(int id, String nombre) {
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