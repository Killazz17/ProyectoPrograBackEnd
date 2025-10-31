package hospital.example.Domain.dtos.Medicamento;

public class MedicamentoResponseDto {
    private String codigo;
    private String nombre;
    private String presentacion;

    public MedicamentoResponseDto() {}

    public MedicamentoResponseDto(String codigo, String nombre, String presentacion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.presentacion = presentacion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPresentacion() {
        return presentacion;
    }
}