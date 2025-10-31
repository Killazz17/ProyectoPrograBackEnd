package hospital.example.Domain.dtos.MedicamentoPrescrito;

public class MedicamentoPrescritoResponseDto {
    private String codigo;
    private String nombre;
    private String presentacion;
    private int cantidad;
    private int duracion;
    private String indicaciones;

    public MedicamentoPrescritoResponseDto() {}

    public MedicamentoPrescritoResponseDto(String codigo, String nombre, String presentacion,
                                           int cantidad, int duracion, String indicaciones) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.presentacion = presentacion;
        this.cantidad = cantidad;
        this.duracion = duracion;
        this.indicaciones = indicaciones;
    }

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

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }
}