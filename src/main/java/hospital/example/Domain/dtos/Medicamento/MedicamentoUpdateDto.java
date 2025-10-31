package hospital.example.Domain.dtos.Medicamento;

public class MedicamentoUpdateDto {
    private String codigo;
    private String nombre;
    private String presentacion;

    public MedicamentoUpdateDto() {}

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }
}