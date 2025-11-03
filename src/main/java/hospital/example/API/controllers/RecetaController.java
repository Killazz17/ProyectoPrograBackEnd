package hospital.example.API.controllers;

import com.google.gson.Gson;
import hospital.example.DataAccess.services.MedicamentoService;
import hospital.example.DataAccess.services.PacienteService;
import hospital.example.DataAccess.services.RecetaService;
import hospital.example.Domain.dtos.MedicamentoPrescrito.MedicamentoPrescritoDto;
import hospital.example.Domain.dtos.Receta.RecetaCreateDto;
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

public class RecetaController {
    private final RecetaService recetaService;
    private final PacienteService pacienteService;
    private final MedicamentoService medicamentoService;
    private final Gson gson = new Gson();

    public RecetaController(RecetaService recetaService, PacienteService pacienteService, MedicamentoService medicamentoService) {
        this.recetaService = recetaService;
        this.pacienteService = pacienteService;
        this.medicamentoService = medicamentoService;
    }

    public ResponseDto route(RequestDto request) {
        try {
            switch (request.getRequest()) {
                case "createReceta":
                    return handleCreate(request);
                default:
                    return new ResponseDto(false, "Comando no reconocido en RecetaController", null);
            }
        } catch (Exception e) {
            return new ResponseDto(false, "Error en RecetaController: " + e.getMessage(), null);
        }
    }

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

                // ✅ CREAR medicamento prescrito
                MedicamentoPrescrito mp = new MedicamentoPrescrito(
                        base.getCodigo(),
                        medDto.getCantidad(),
                        medDto.getDuracion(),
                        medDto.getIndicaciones()
                );

                // ✅ CLAVE: Usar addMedicamento para sincronizar relación bidireccional
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
}