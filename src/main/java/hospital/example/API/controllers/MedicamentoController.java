package hospital.example.API.controllers;

import com.google.gson.Gson;
import hospital.example.DataAccess.services.MedicamentoService;
import hospital.example.Domain.dtos.Medicamento.MedicamentoCreateDto;
import hospital.example.Domain.dtos.Medicamento.MedicamentoResponseDto;
import hospital.example.Domain.dtos.RequestDto;
import hospital.example.Domain.dtos.ResponseDto;
import hospital.example.Domain.models.Medicamento;

import java.util.List;
import java.util.stream.Collectors;

public class MedicamentoController {
    private final MedicamentoService medicamentoService;
    private final Gson gson = new Gson();

    public MedicamentoController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    public ResponseDto route(RequestDto request) {
        try {
            switch (request.getRequest()) {
                case "getAllMedicamentos":
                    return handleGetAll();
                case "createMedicamento":
                    return handleCreate(request);
                case "deleteMedicamento":
                    return handleDelete(request);
                default:
                    return new ResponseDto(false, "Comando no reconocido en MedicamentoController", null);
            }
        } catch (Exception e) {
            return new ResponseDto(false, "Error en MedicamentoController: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleGetAll() {
        List<Medicamento> medicamentos = medicamentoService.findAll();

        List<MedicamentoResponseDto> dtos = medicamentos.stream()
                .map(m -> new MedicamentoResponseDto(m.getCodigo(), m.getNombre(), m.getPresentacion()))
                .collect(Collectors.toList());

        return new ResponseDto(true, "Medicamentos cargados correctamente", gson.toJson(dtos));
    }

    private ResponseDto handleCreate(RequestDto request) {
        try {
            MedicamentoCreateDto dto = gson.fromJson(request.getData(), MedicamentoCreateDto.class);
            Medicamento medicamento = new Medicamento(dto.getCodigo(), dto.getNombre(), dto.getPresentacion());

            boolean success = medicamentoService.save(medicamento);
            return new ResponseDto(success, success ? "Medicamento creado correctamente" : "Error al guardar", null);

        } catch (Exception e) {
            return new ResponseDto(false, "Error al crear medicamento: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleDelete(RequestDto request) {
        String codigo = request.getData();
        boolean success = medicamentoService.delete(codigo);
        return new ResponseDto(success, success ? "Medicamento eliminado" : "No se encontró el medicamento", null);
    }
}