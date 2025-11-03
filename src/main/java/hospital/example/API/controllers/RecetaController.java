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
                case "buscarRecetas":
                    return handleBuscarRecetas(request);
                default:
                    return new ResponseDto(false, "Comando no reconocido en RecetaController", null);
            }
        } catch (Exception e) {
            return new ResponseDto(false, "Error en RecetaController: " + e.getMessage(), null);
        }
    }

    // ========== MÉTODOS EXISTENTES ==========

    private ResponseDto handleCreate(RequestDto request) {
        try {
            System.out.println("[RecetaController] === INICIANDO CREACIÓN DE RECETA ===");
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

            System.out.println("[RecetaController] Procesando " + dto.getMedicamentos().size() + " medicamentos...");

            for (MedicamentoPrescritoDto medDto : dto.getMedicamentos()) {
                Medicamento base = medicamentoService.findByCodigo(medDto.getCodigo());

                if (base == null) {
                    System.err.println("[RecetaController] ⚠️  Medicamento no encontrado: " + medDto.getCodigo());
                    continue;
                }

                MedicamentoPrescrito mp = new MedicamentoPrescrito(
                        base.getCodigo(),
                        medDto.getCantidad(),
                        medDto.getDuracion(),
                        medDto.getIndicaciones()
                );

                receta.addMedicamento(mp);

                System.out.println("[RecetaController] ✓ Agregado: " + base.getCodigo() +
                        " - " + base.getNombre() + " (Cant: " + medDto.getCantidad() + ")");
            }

            if (receta.getMedicamentos().isEmpty()) {
                System.err.println("[RecetaController] No hay medicamentos válidos en la receta");
                return new ResponseDto(false, "No se encontraron medicamentos válidos", null);
            }

            System.out.println("[RecetaController] Guardando receta con " +
                    receta.getMedicamentos().size() + " medicamentos...");

            boolean success = recetaService.createReceta(receta);

            if (success) {
                System.out.println("[RecetaController] ✅ RECETA CREADA EXITOSAMENTE - ID: " + receta.getId());
            } else {
                System.err.println("[RecetaController] ❌ ERROR AL GUARDAR RECETA");
            }

            return new ResponseDto(success,
                    success ? "Receta creada correctamente" : "Error al guardar receta",
                    null);

        } catch (Exception e) {
            System.err.println("[RecetaController] ❌ EXCEPCIÓN: " + e.getMessage());
            e.printStackTrace();
            return new ResponseDto(false, "Error al procesar receta: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleGetAllDetalladas() {
        try {
            System.out.println("[RecetaController] ====== OBTENIENDO RECETAS DETALLADAS ======");

            List<Receta> recetas = recetaService.findAllWithMedicamentos();

            if (recetas == null || recetas.isEmpty()) {
                System.out.println("[RecetaController] ⚠️ No hay recetas disponibles");
                return new ResponseDto(true, "No hay recetas", gson.toJson(new ArrayList<>()));
            }

            System.out.println("[RecetaController] ✓ Recetas encontradas: " + recetas.size());

            List<RecetaDetalladaResponseDto> dtos = new ArrayList<>();

            for (Receta receta : recetas) {
                try {
                    System.out.println("[RecetaController] Procesando receta ID: " + receta.getId());
                    System.out.println("[RecetaController]   Medicamentos: " + receta.getMedicamentos().size());

                    RecetaDetalladaResponseDto dto = convertirADto(receta);
                    dtos.add(dto);

                    System.out.println("[RecetaController]   ✓ DTO creado con " +
                            dto.getMedicamentos().size() + " medicamentos");

                } catch (Exception e) {
                    System.err.println("[RecetaController] ⚠️ Error procesando receta #" +
                            receta.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("[RecetaController] ✅ Total DTOs creados: " + dtos.size());

            String json = gson.toJson(dtos);
            System.out.println("[RecetaController] JSON generado (primeros 300 chars): " +
                    json.substring(0, Math.min(300, json.length())));

            return new ResponseDto(true, "Recetas obtenidas correctamente", json);

        } catch (Exception e) {
            System.err.println("[RecetaController] ❌ Error al obtener recetas: " + e.getMessage());
            e.printStackTrace();
            return new ResponseDto(false, "Error al obtener recetas: " + e.getMessage(),
                    gson.toJson(new ArrayList<>()));
        }
    }

    // ========== NUEVOS MÉTODOS PARA DESPACHO ==========

    private ResponseDto handleGetAllDespacho() {
        try {
            System.out.println("[RecetaController] Obteniendo todas las recetas para despacho");

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
            // Parsear datos: {idReceta, estado}
            com.google.gson.JsonObject data = gson.fromJson(request.getData(), com.google.gson.JsonObject.class);
            int idReceta = data.get("idReceta").getAsInt();
            String nuevoEstado = data.get("estado").getAsString();

            System.out.println("[RecetaController] Actualizando estado de receta " + idReceta + " a " + nuevoEstado);

            Receta receta = recetaService.findById(idReceta);

            if (receta == null) {
                return new ResponseDto(false, "Receta no encontrada", null);
            }

            // Convertir string a enum
            EstadoReceta estado;
            try {
                estado = EstadoReceta.valueOf(nuevoEstado.toLowerCase());
            } catch (IllegalArgumentException e) {
                return new ResponseDto(false, "Estado inválido: " + nuevoEstado, null);
            }

            boolean success = recetaService.updateEstado(idReceta, estado);

            if (success) {
                System.out.println("[RecetaController] ✅ Estado actualizado correctamente");
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
            System.out.println("[RecetaController] Buscando receta con ID " + id);

            Receta receta = recetaService.findByIdWithMedicamentos(id);

            if (receta == null) {
                return new ResponseDto(false, "Receta no encontrada", null);
            }

            RecetaDetalladaResponseDto dto = convertirADto(receta);

            return new ResponseDto(true, "Receta encontrada", gson.toJson(dto));

        } catch (Exception e) {
            System.err.println("[RecetaController] Error al buscar receta: " + e.getMessage());
            e.printStackTrace();
            return new ResponseDto(false, "Error: " + e.getMessage(), null);
        }
    }

    // ========== MÉTODOS HELPER ==========

    private RecetaDetalladaResponseDto convertirADto(Receta receta) {
        List<MedicamentoPrescritoResponseDto> medicamentosDto = new ArrayList<>();

        if (receta.getMedicamentos() != null) {
            System.out.println("[RecetaController]   Convirtiendo " +
                    receta.getMedicamentos().size() + " medicamentos a DTOs");

            for (MedicamentoPrescrito mp : receta.getMedicamentos()) {
                try {
                    Medicamento medicamento = medicamentoService.findByCodigo(mp.getMedicamentoCodigo());

                    String nombreMed = "Desconocido";
                    String presentacionMed = "N/A";

                    if (medicamento != null) {
                        nombreMed = medicamento.getNombre();
                        presentacionMed = medicamento.getPresentacion();
                    } else {
                        System.err.println("[RecetaController]     ⚠️ No se encontró medicamento: " +
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
                    System.out.println("[RecetaController]     ✓ " + nombreMed);

                } catch (Exception e) {
                    System.err.println("[RecetaController]     ❌ Error procesando medicamento: " +
                            e.getMessage());
                }
            }
        } else {
            System.out.println("[RecetaController]   ⚠️ Lista de medicamentos es NULL");
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

// ========== MÉTODO DE BÚSQUEDA DE HISTORICO ==========

    private ResponseDto handleBuscarRecetas(RequestDto request) {
        try {
            // El filtro viene como JSON: {"tipo": "id" o "nombre", "valor": "texto"}
            com.google.gson.JsonObject filtro = gson.fromJson(request.getData(), com.google.gson.JsonObject.class);
            String tipo = filtro.has("tipo") ? filtro.get("tipo").getAsString().toLowerCase() : "all";
            String valor = filtro.has("valor") ? filtro.get("valor").getAsString().trim() : "";

            System.out.println("[RecetaController] 🔍 Buscando recetas - Tipo: '" + tipo + "', Valor: '" + valor + "'");

            List<Receta> todasRecetas = recetaService.findAllWithMedicamentos();

            if (todasRecetas == null || todasRecetas.isEmpty()) {
                return new ResponseDto(true, "No hay recetas", gson.toJson(new ArrayList<>()));
            }

            List<Receta> recetasFiltradas = new ArrayList<>();

            // Si el valor está vacío, devolver todas
            if (valor.isEmpty()) {
                recetasFiltradas = todasRecetas;
            } else {
                System.out.println("=== [RecetaController] BÚSQUEDA RECIBIDA ===");
                System.out.println("  tipo: '" + tipo + "'");
                System.out.println("  valor: '" + valor + "'");
                System.out.println("  valor.trim(): '" + valor.trim() + "'");
                System.out.println("  valor.isEmpty(): " + valor.isEmpty());
                // Filtrar según el tipo
                switch (tipo) {
                    case "id":
                        String valorIdStr = valor.trim();
                        if (valorIdStr.isEmpty()) {
                            recetasFiltradas = todasRecetas;
                            break;
                        }

                        try {
                            int idBuscado = Integer.parseInt(valorIdStr);
                            System.out.println("[RecetaController] Buscando recetas del paciente con ID: " + idBuscado);

                            // Usa el nuevo método del servicio
                            recetasFiltradas = recetaService.findByPacienteId(idBuscado);

                            System.out.println("[RecetaController] Resultado: " + recetasFiltradas.size() + " recetas");

                        } catch (NumberFormatException e) {
                            System.err.println("[RecetaController] ID no es número: '" + valorIdStr + "'");
                            recetasFiltradas = List.of();
                        }
                        break;
                    case "nombre":
                        // Buscar por nombre de paciente (parcial, insensible a mayúsculas)
                        String valorLower = valor.toLowerCase();
                        recetasFiltradas = todasRecetas.stream()
                                .filter(r -> {
                                    boolean coincide = r.getPaciente().getNombre().toLowerCase().contains(valorLower);
                                    System.out.println("[RecetaController]   Receta #" + r.getId() +
                                            " - Paciente: " + r.getPaciente().getNombre() +
                                            " - ¿Coincide con '" + valor + "'? " + coincide);
                                    return coincide;
                                })
                                .collect(Collectors.toList());
                        break;
                    case "id_receta":
                        try {
                            int idBuscado = Integer.parseInt(valor);
                            recetasFiltradas = todasRecetas.stream()
                                    .filter(r -> r.getId() == idBuscado)
                                    .collect(Collectors.toList());
                            System.out.println("[RecetaController] Búsqueda por ID receta " + idBuscado +
                                    " → " + recetasFiltradas.size() + " encontradas");
                        } catch (NumberFormatException e) {
                            System.err.println("[RecetaController] ID receta no es número: '" + valor + "'");
                            recetasFiltradas = List.of();
                        }
                        break;
                    case "all":
                    default:
                        // Sin filtro, devolver todas
                        recetasFiltradas = todasRecetas;
                        break;
                }
            }

            System.out.println("[RecetaController] ✅ Encontradas " + recetasFiltradas.size() + " recetas de " + todasRecetas.size() + " totales");

            // Convertir a DTOs simples para histórico
            List<HistoricoRecetaDto> dtos = recetasFiltradas.stream()
                    .map(this::convertirAHistoricoDto)
                    .collect(Collectors.toList());

            return new ResponseDto(true, "Recetas encontradas", gson.toJson(dtos));

        } catch (Exception e) {
            System.err.println("[RecetaController] ❌ Error en búsqueda: " + e.getMessage());
            e.printStackTrace();
            return new ResponseDto(false, "Error: " + e.getMessage(), gson.toJson(new ArrayList<>()));
        }
    }

// ========== DTO SIMPLE PARA HISTÓRICO (coincide con tu frontend) ==========

    private HistoricoRecetaDto convertirAHistoricoDto(Receta receta) {
        return new HistoricoRecetaDto(
                receta.getId(),
                receta.getPaciente().getNombre(),
                "Sin asignar", // Como no tienes médico en las recetas
                receta.getFechaConfeccion().toString(),
                receta.getEstado().toString()
        );
    }

    // DTO interno para histórico - coincide con tu estructura del frontend
    private static class HistoricoRecetaDto {
        private int id;
        private String paciente;
        private String medico;
        private String fecha;
        private String estado;

        public HistoricoRecetaDto(int id, String paciente, String medico,
                                        String fecha, String estado) {
            this.id = id;
            this.paciente = paciente;
            this.medico = medico;
            this.fecha = fecha;
            this.estado = estado;
        }

        // Getters
        public int getId() { return id; }
        public String getPaciente() { return paciente; }
        public String getMedico() { return medico; }
        public String getFecha() { return fecha; }
        public String getEstado() { return estado; }
    }
}