package hospital.example.API.controllers;

import com.google.gson.Gson;
import hospital.example.DataAccess.services.PacienteService;
import hospital.example.Domain.dtos.Paciente.PacienteCreateDto;
import hospital.example.Domain.dtos.Paciente.PacienteResponseDto;
import hospital.example.Domain.dtos.RequestDto;
import hospital.example.Domain.dtos.ResponseDto;
import hospital.example.Domain.models.Paciente;

import java.util.List;
import java.util.stream.Collectors;

public class PacienteController {
    private final PacienteService pacienteService;
    private final Gson gson = new Gson();

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    public ResponseDto route(RequestDto request) {
        try {
            switch (request.getRequest()) {
                case "getAllPacientes":
                    return handleGetAll();
                case "createPaciente":
                    return handleCreate(request);
                case "deletePaciente":
                    return handleDelete(request);
                default:
                    return new ResponseDto(false, "Comando no reconocido en PacienteController", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "Error en PacienteController: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleGetAll() {
        List<Paciente> pacientes = pacienteService.findAll();

        List<PacienteResponseDto> dtos = pacientes.stream()
                .map(p -> new PacienteResponseDto(p.getId(), p.getNombre(), p.getFechaNacimiento(), p.getNumeroTelefono()))
                .collect(Collectors.toList());

        return new ResponseDto(true, "Pacientes cargados correctamente", gson.toJson(dtos));
    }

    private ResponseDto handleCreate(RequestDto request) {
        try {
            PacienteCreateDto dto = gson.fromJson(request.getData(), PacienteCreateDto.class);
            // Constructor Paciente: (id, claveHash, nombre, fechaNacimiento, numeroTelefono)
            Paciente paciente = new Paciente(
                    dto.getId(),
                    "",
                    dto.getNombre(),
                    dto.getFechaNacimiento(),
                    dto.getNumeroTelefono()
            );
            boolean success = pacienteService.save(paciente);
            return new ResponseDto(success, success ? "Paciente creado correctamente" : "Error al guardar", null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "Error al crear paciente: " + e.getMessage(), null);
        }
    }


    private ResponseDto handleDelete(RequestDto request) {
        int id = Integer.parseInt(request.getData());
        boolean success = pacienteService.delete(id);
        return new ResponseDto(success, success ? "Paciente eliminado" : "No se encontró el paciente", null);
    }
}
