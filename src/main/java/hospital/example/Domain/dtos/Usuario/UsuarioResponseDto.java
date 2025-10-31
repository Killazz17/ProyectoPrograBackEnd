package hospital.example.Domain.dtos.Usuario;

public class UsuarioResponseDto {
    private int id;
    private String nombre;
    private String rol;

    public UsuarioResponseDto() {}

    public UsuarioResponseDto(int id, String nombre, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }
}