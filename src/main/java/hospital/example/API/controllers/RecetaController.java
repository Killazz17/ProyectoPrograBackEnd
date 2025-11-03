package hospital.example.API.controllers;

import com.google.gson.Gson;
import hospital.example.DataAccess.services.MedicamentoService;
import hospital.example.DataAccess.services.PacienteService;
import hospital.example.DataAccess.services.RecetaService;
import hospital.example.Domain.dtos.MedicamentoPrescrito.MedicamentoPrescritoDto;
import hospital.example.Domain.dtos.MedicamentoPrescrito.MedicamentoPrescritoResponseDto;
import hospital.example.Domain.dtos.Receta.RecetaCreateDto;
import hospital.example.Domain.dtos.Receta.RecetaDetalladaResponseDto;
import hospital.example.Domain.dtos.RequestDto;
import hospital.example.Domain.dtos.ResponseDto;
import hospital.example.Domain.models.Medicamento;
import hospital.example.Domain.models.MedicamentoPrescrito;
import hospital.example.Domain.models.Paciente;
import hospital.example.Domain.models.Receta;
import hospital.example.Utilities.EstadoReceta;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecetaController {
    private final RecetaService recetaService;
    private final PacienteService pacienteService;
    private final MedicamentoService medicamentoService;
    private final Gson gson = new Gson();

    public RecetaController(RecetaService recetaService, PacienteService pacienteService,
                            MedicamentoService medicamentoService) {
        this.recetaService = recetaService;
        this.pacienteService = pacienteService;
        this.medicamentoService = medicamentoService;
    }

    public ResponseDto route(RequestDto request) {
        try {
            switch (request.getRequest()) {
                case "createReceta":
                    return handleCreate(request);
                case "getAllRecetasDetalladas":
                    return handleGetAllDetalladas();
                case "getAllRecetasDespacho":
                    return handleGetAllDespacho();
                case "getRecetasByPaciente":
                    return handleGetByPaciente(request);
                case "updateEstado":
                    return handleUpdateEstado(request);
                case "getRecetaById":
                    return handleGetById(request);
                default:
                    return new ResponseDto(false, "Comando no reconocido en RecetaController", null);
            }
        } catch (Exception e) {
            return new ResponseDto(false, "Error en RecetaController: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleCreate(RequestDto request) {
        try {
            RecetaCreateDto dto = gson.fromJson(request.getData(), RecetaCreateDto.class);

            Paciente paciente = pacienteService.findById(dto.getPacienteId());
            if (paciente == null) {
                System.err.println("[RecetaController] Paciente no encontrado: " + dto.getPacienteId());
                return new ResponseDto(false, "Paciente no encontrado", null);
            }

            Receta receta = new Receta();
            receta.setPaciente(paciente);
            receta.setEstado(EstadoReceta.confeccionada);
            receta.setFechaConfeccion(LocalDate.now());
            receta.setFechaRetiro(dto.getFechaRetiro().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate());

            for (MedicamentoPrescritoDto medDto : dto.getMedicamentos()) {
                Medicamento base = medicamentoService.findByCodigo(medDto.getCodigo());

                if (base == null) {
                    System.err.println("[RecetaController]  Medicamento no encontrado: " + medDto.getCodigo());
                    continue;
                }

                MedicamentoPrescrito mp = new MedicamentoPrescrito(
                        base.getCodigo(),
                        medDto.getCantidad(),
                        medDto.getDuracion(),
                        medDto.getIndicaciones()
                );

                receta.addMedicamento(mp);

            }

            if (receta.getMedicamentos().isEmpty()) {
                System.err.println("[RecetaController] No hay medicamentos válidos en la receta");
                return new ResponseDto(false, "No se encontraron medicamentos válidos", null);
            }

            boolean success = recetaService.createReceta(receta);

            if (success) {
                System.out.println("[RecetaController] RECETA CREADA EXITOSAMENTE - ID: " + receta.getId());
            } else {
                System.err.println("[RecetaController] ERROR AL GUARDAR RECETA");
            }

            return new ResponseDto(success,
                    success ? "Receta creada correctamente" : "Error al guardar receta",
                    null);

        } catch (Exception e) {
            System.err.println("[RecetaController] EXCEPCIÓN: " + e.getMessage());
            e.printStackTrace();
            return new ResponseDto(false, "Error al procesar receta: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleGetAllDetalladas() {
        try {
            List<Receta> recetas = recetaService.findAllWithMedicamentos();

            if (recetas == null || recetas.isEmpty()) {
                System.out.println("[RecetaController] No hay recetas disponibles");
                return new ResponseDto(true, "No hay recetas", gson.toJson(new ArrayList<>()));
            }

            System.out.println("[RecetaController] Recetas encontradas: " + recetas.size());

            List<RecetaDetalladaResponseDto> dtos = new ArrayList<>();

            for (Receta receta : recetas) {
                try {
                    RecetaDetalladaResponseDto dto = convertirADto(receta);
                    dtos.add(dto);

                } catch (Exception e) {
                    System.err.println("[RecetaController] Error procesando receta #" +
                            receta.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            String json = gson.toJson(dtos);

            return new ResponseDto(true, "Recetas obtenidas correctamente", json);

        } catch (Exception e) {
            System.err.println("[RecetaController] Error al obtener recetas: " + e.getMessage());
            e.printStackTrace();
            return new ResponseDto(false, "Error al obtener recetas: " + e.getMessage(),
                    gson.toJson(new ArrayList<>()));
        }
    }

    private ResponseDto handleGetAllDespacho() {
        try {
            List<Receta> recetas = recetaService.findAllWithMedicamentos();

            if (recetas == null || recetas.isEmpty()) {
                return new ResponseDto(true, "No hay recetas", gson.toJson(new ArrayList<>()));
            }

            List<DespachoDto> dtos = recetas.stream()
                    .map(this::convertirADespachoDto)
                    .collect(Collectors.toList());

            return new ResponseDto(true, "Recetas obtenidas correctamente", gson.toJson(dtos));

        } catch (Exception e) {
            System.err.println("[RecetaController] Error al obtener recetas para despacho: " + e.getMessage());
            e.printStackTrace();
            return new ResponseDto(false, "Error: " + e.getMessage(), gson.toJson(new ArrayList<>()));
        }
    }

    private ResponseDto handleGetByPaciente(RequestDto request) {
        try {
            int pacienteId = gson.fromJson(request.getData(), Integer.class);
            System.out.println("[RecetaController] Obteniendo recetas del paciente " + pacienteId);

            List<Receta> todasRecetas = recetaService.findAllWithMedicamentos();

            List<Receta> recetasPaciente = todasRecetas.stream()
                    .filter(r -> r.getPaciente().getId() == pacienteId)
                    .collect(Collectors.toList());

            List<DespachoDto> dtos = recetasPaciente.stream()
                    .map(this::convertirADespachoDto)
                    .collect(Collectors.toList());

            System.out.println("[RecetaController] Encontradas " + dtos.size() + " recetas del paciente");

            return new ResponseDto(true, "Recetas del paciente obtenidas", gson.toJson(dtos));

        } catch (Exception e) {
            System.err.println("[RecetaController] Error al obtener recetas por paciente: " + e.getMessage());
            e.printStackTrace();
            return new ResponseDto(false, "Error: " + e.getMessage(), gson.toJson(new ArrayList<>()));
        }
    }

    private ResponseDto handleUpdateEstado(RequestDto request) {
        try {
            com.google.gson.JsonObject data = gson.fromJson(request.getData(), com.google.gson.JsonObject.class);
            int idReceta = data.get("idReceta").getAsInt();
            String nuevoEstado = data.get("estado").getAsString();

            Receta receta = recetaService.findById(idReceta);

            if (receta == null) {
                return new ResponseDto(false, "Receta no encontrada", null);
            }

            // Convertir string a enum
            EstadoReceta estado;
            try {
                estado = EstadoReceta.valueOf(nuevoEstado.toLowerCase());
            } catch (IllegalArgumentException e) {
                return new ResponseDto(false, "Estado invalido: " + nuevoEstado, null);
            }

            boolean success = recetaService.updateEstado(idReceta, estado);

            if (success) {
                System.out.println("[RecetaController] Estado actualizado correctamente");
            }

            return new ResponseDto(success,
                    success ? "Estado actualizado correctamente" : "Error al actualizar estado",
                    null);

        } catch (Exception e) {
            System.err.println("[RecetaController] Error al actualizar estado: " + e.getMessage());
            e.printStackTrace();
            return new ResponseDto(false, "Error: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleGetById(RequestDto request) {
        try {
            int id = gson.fromJson(request.getData(), Integer.class);

            Receta receta = recetaService.findByIdWithMedicamentos(id);

            if (receta == null) {
                return new ResponseDto(false, "Receta no encontrada", null);
            }

            DespachoDto dto = convertirADespachoDto(receta);

            return new ResponseDto(true, "Receta encontrada", gson.toJson(dto));

        } catch (Exception e) {
            System.err.println("[RecetaController] Error al buscar receta: " + e.getMessage());
            e.printStackTrace();
            return new ResponseDto(false, "Error: " + e.getMessage(), null);
        }
    }

    private RecetaDetalladaResponseDto convertirADto(Receta receta) {
        List<MedicamentoPrescritoResponseDto> medicamentosDto = new ArrayList<>();

        if (receta.getMedicamentos() != null) {

            for (MedicamentoPrescrito mp : receta.getMedicamentos()) {
                try {
                    Medicamento medicamento = medicamentoService.findByCodigo(mp.getMedicamentoCodigo());

                    String nombreMed = "Desconocido";
                    String presentacionMed = "N/A";

                    if (medicamento != null) {
                        nombreMed = medicamento.getNombre();
                        presentacionMed = medicamento.getPresentacion();
                    } else {
                        System.err.println("[RecetaController]  ️ No se encontro medicamento: " +
                                mp.getMedicamentoCodigo());
                    }

                    MedicamentoPrescritoResponseDto medDto = new MedicamentoPrescritoResponseDto(
                            mp.getMedicamentoCodigo(),
                            nombreMed,
                            presentacionMed,
                            mp.getCantidad(),
                            mp.getDuracion(),
                            mp.getIndicaciones()
                    );

                    medicamentosDto.add(medDto);

                } catch (Exception e) {
                    System.err.println("[RecetaController]  Error procesando medicamento: " +
                            e.getMessage());
                }
            }
        }

        return new RecetaDetalladaResponseDto(
                receta.getId(),
                receta.getPaciente().getId(),
                0,
                receta.getFechaConfeccion().toString(),
                receta.getFechaRetiro().toString(),
                receta.getEstado().toString(),
                medicamentosDto
        );
    }

    private DespachoDto convertirADespachoDto(Receta receta) {
        return new DespachoDto(
                receta.getId(),
                receta.getPaciente().getId(),
                receta.getPaciente().getNombre(),
                receta.getFechaConfeccion().toString(),
                receta.getFechaRetiro().toString(),
                receta.getEstado().toString(),
                receta.getMedicamentos() != null ? receta.getMedicamentos().size() : 0
        );
    }

    // DTO interno para despacho
    private static class DespachoDto {
        private int id;
        private int idPaciente;
        private String nombrePaciente;
        private String fechaConfeccion;
        private String fechaRetiro;
        private String estado;
        private int cantidadMedicamentos;

        public DespachoDto(int id, int idPaciente, String nombrePaciente,
                           String fechaConfeccion, String fechaRetiro,
                           String estado, int cantidadMedicamentos) {
            this.id = id;
            this.idPaciente = idPaciente;
            this.nombrePaciente = nombrePaciente;
            this.fechaConfeccion = fechaConfeccion;
            this.fechaRetiro = fechaRetiro;
            this.estado = estado;
            this.cantidadMedicamentos = cantidadMedicamentos;
        }
    }
}