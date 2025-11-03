package hospital.example.API.controllers;

import com.google.gson.Gson;
import hospital.example.DataAccess.services.MedicamentoService;
import hospital.example.DataAccess.services.RecetaService;
import hospital.example.Domain.dtos.RequestDto;
import hospital.example.Domain.dtos.ResponseDto;
import hospital.example.Domain.models.Medicamento;
import hospital.example.Domain.models.MedicamentoPrescrito;
import hospital.example.Domain.models.Receta;

import java.util.ArrayList;
import java.util.List;

public class MedicamentoPrescritoController {
    private final RecetaService recetaService;
    private final MedicamentoService medicamentoService;
    private final Gson gson = new Gson();

    public MedicamentoPrescritoController(RecetaService recetaService,
                                          MedicamentoService medicamentoService) {
        this.recetaService = recetaService;
        this.medicamentoService = medicamentoService;
    }

    public ResponseDto route(RequestDto request) {
        try {
            switch (request.getRequest()) {
                case "getAllDetallados":
                    return handleGetAllDetallados();
                default:
                    return new ResponseDto(false,
                            "Comando no reconocido en MedicamentoPrescritoController", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false,
                    "Error en MedicamentoPrescritoController: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleGetAllDetallados() {
        try {
            System.out.println("[MedicamentoPrescritoController] Obteniendo medicamentos prescritos...");

            List<Receta> recetas = recetaService.findAllWithMedicamentos();

            if (recetas == null || recetas.isEmpty()) {
                return new ResponseDto(true, "No hay medicamentos prescritos",
                        gson.toJson(new ArrayList<>()));
            }

            List<MedicamentoPrescritoDetalladoDto> resultado = new ArrayList<>();

            for (Receta receta : recetas) {
                if (receta.getMedicamentos() == null) continue;

                String fechaConfeccion = receta.getFechaConfeccion().toString();
                String estado = receta.getEstado().toString();

                for (MedicamentoPrescrito mp : receta.getMedicamentos()) {
                    Medicamento med = medicamentoService.findByCodigo(mp.getMedicamentoCodigo());

                    String nombre = "Desconocido";
                    String presentacion = "N/A";

                    if (med != null) {
                        nombre = med.getNombre();
                        presentacion = med.getPresentacion();
                    }

                    MedicamentoPrescritoDetalladoDto dto = new MedicamentoPrescritoDetalladoDto(
                            mp.getId(),
                            mp.getMedicamentoCodigo(),
                            nombre,
                            presentacion,
                            mp.getCantidad(),
                            mp.getDuracion(),
                            mp.getIndicaciones(),
                            fechaConfeccion,
                            estado
                    );

                    resultado.add(dto);
                }
            }

            return new ResponseDto(true, "Medicamentos prescritos obtenidos correctamente",
                    gson.toJson(resultado));

        } catch (Exception e) {
            System.err.println("[MedicamentoPrescritoController] Error: " + e.getMessage());
            e.printStackTrace();
            return new ResponseDto(false, "Error al obtener medicamentos prescritos: "
                    + e.getMessage(), gson.toJson(new ArrayList<>()));
        }
    }

    private static class MedicamentoPrescritoDetalladoDto {
        private int id;
        private String medicamentoCodigo;
        private String medicamentoNombre;
        private String medicamentoPresentacion;
        private int cantidad;
        private int duracion;
        private String indicaciones;
        private String fechaConfeccion;
        private String estado;

        public MedicamentoPrescritoDetalladoDto(int id, String medicamentoCodigo,
                                                String medicamentoNombre, String medicamentoPresentacion,
                                                int cantidad, int duracion, String indicaciones,
                                                String fechaConfeccion, String estado) {
            this.id = id;
            this.medicamentoCodigo = medicamentoCodigo;
            this.medicamentoNombre = medicamentoNombre;
            this.medicamentoPresentacion = medicamentoPresentacion;
            this.cantidad = cantidad;
            this.duracion = duracion;
            this.indicaciones = indicaciones;
            this.fechaConfeccion = fechaConfeccion;
            this.estado = estado;
        }
    }
}