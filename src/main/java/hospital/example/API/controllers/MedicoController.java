package hospital.example.API.controllers;

import com.google.gson.Gson;
import hospital.example.DataAccess.services.MedicoService;
import hospital.example.Domain.dtos.Medico.MedicoCreateDto;
import hospital.example.Domain.dtos.Medico.MedicoResponseDto;
import hospital.example.Domain.dtos.RequestDto;
import hospital.example.Domain.dtos.ResponseDto;
import hospital.example.Domain.models.Medico;

import java.util.List;
import java.util.stream.Collectors;

public class MedicoController {
    private final MedicoService medicoService;
    private final Gson gson = new Gson();

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    public ResponseDto route(RequestDto request) {
        try {
            switch (request.getRequest()) {
                case "getAllMedicos":
                    return handleGetAll();
                case "createMedico":
                    return handleCreate(request);
                case "deleteMedico":
                    return handleDelete(request);
                default:
                    return new ResponseDto(false, "Comando no reconocido en MedicoController", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "Error en MedicoController: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleGetAll() {
        List<Medico> medicos = medicoService.findAll();

        List<MedicoResponseDto> dtos = medicos.stream()
                .map(m -> new MedicoResponseDto(m.getId(), m.getNombre(), m.getEspecialidad()))
                .collect(Collectors.toList());

        return new ResponseDto(true, "Medicos cargados correctamente", gson.toJson(dtos));
    }

    private ResponseDto handleCreate(RequestDto request) {
        try {
            MedicoCreateDto dto = gson.fromJson(request.getData(), MedicoCreateDto.class);
            Medico m = new Medico(dto.getId(), "", dto.getNombre(), dto.getEspecialidad());
            boolean success = medicoService.save(m);

            return new ResponseDto(success, success ? "Medico creado correctamente" : "Error al guardar", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "Error al crear medico: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleDelete(RequestDto request) {
        int id = Integer.parseInt(request.getData());
        boolean success = medicoService.delete(id);
        return new ResponseDto(success, success ? "Medico eliminado" : "No se encontro el médico", null);
    }
}
