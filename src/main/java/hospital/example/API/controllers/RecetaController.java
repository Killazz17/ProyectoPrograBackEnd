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
            RecetaCreateDto dto = gson.fromJson(request.getData(), RecetaCreateDto.class);
            Paciente paciente = pacienteService.findById(dto.getPacienteId());

            if (paciente == null) {
                return new ResponseDto(false, "Paciente no encontrado", null);
            }

            Receta receta = new Receta();
            receta.setPaciente(paciente);
            receta.setEstado(EstadoReceta.confeccionada);
            receta.setFechaConfeccion(LocalDate.now());
            receta.setFechaRetiro(dto.getFechaRetiro().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            List<MedicamentoPrescrito> detalles = new ArrayList<>();
            for (MedicamentoPrescritoDto medDto : dto.getMedicamentos()) {
                Medicamento base = medicamentoService.findByCodigo(medDto.getCodigo());
                if (base == null) continue;

                MedicamentoPrescrito mp = new MedicamentoPrescrito(
                        base.getCodigo(),
                        base.getNombre(),
                        base.getPresentacion(),
                        medDto.getCantidad(),
                        medDto.getDuracion(),
                        medDto.getIndicaciones()
                );

                detalles.add(mp);
            }

            receta.setMedicamentos(detalles);

            boolean success = recetaService.createReceta(receta);
            return new ResponseDto(success, success ? "Receta creada correctamente" : "Error al guardar receta", null);

        } catch (Exception e) {
            return new ResponseDto(false, "Error al procesar receta: " + e.getMessage(), null);
        }
    }
}