package hospital.example.Domain.dtos.Usuario;

public class UsuarioUpdateDto {
    private int id;
    private String nombre;
    private String clave;

    public UsuarioUpdateDto() {
    }

    public UsuarioUpdateDto(int id, String nombre, String clave) {
        this.id = id;
        this.nombre = nombre;
        this.clave = clave;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}