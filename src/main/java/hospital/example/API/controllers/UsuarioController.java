package hospital.example.API.controllers;

import com.google.gson.Gson;
import hospital.example.DataAccess.services.*;
import hospital.example.Domain.dtos.RequestDto;
import hospital.example.Domain.dtos.ResponseDto;
import hospital.example.Domain.dtos.Usuario.UsuarioCreateDto;
import hospital.example.Domain.dtos.Usuario.UsuarioResponseDto;
import hospital.example.Domain.models.*;
import hospital.example.Utilities.Rol;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioController {
    private final AuthService authService;
    private final AdminService adminService;
    private final MedicoService medicoService;
    private final PacienteService pacienteService;
    private final FarmaceutaService farmaceutaService;
    private final Gson gson = new Gson();

    public UsuarioController(AuthService authService,
                             AdminService adminService,
                             MedicoService medicoService,
                             PacienteService pacienteService,
                             FarmaceutaService farmaceutaService) {
        this.authService = authService;
        this.adminService = adminService;
        this.medicoService = medicoService;
        this.pacienteService = pacienteService;
        this.farmaceutaService = farmaceutaService;
    }

    public ResponseDto route(RequestDto request) {
        try {
            switch (request.getRequest()) {
                case "createUsuario":
                    return handleCreateUsuario(request);
                default:
                    return new ResponseDto(false, "Comando no reconocido en UsuarioController", null);
            }
        } catch (Exception e) {
            return new ResponseDto(false, "Error en UsuarioController: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleCreateUsuario(RequestDto request) {
        try {
            UsuarioCreateDto dto = gson.fromJson(request.getData(), UsuarioCreateDto.class);
            Usuario usuario;

            switch (dto.getRol().toUpperCase()) {
                case "ADMIN":
                    usuario = new Admin(dto.getId(), "", dto.getNombre(), Rol.ADMINISTRADOR);
                    adminService.save((Admin) usuario);
                    break;

                case "MEDICO":
                    usuario = new Medico(dto.getId(), "", dto.getNombre(), dto.getEspecialidad());
                    medicoService.save((Medico) usuario);
                    break;

                case "PACIENTE":
                    usuario = new Paciente(dto.getId(), "", dto.getNombre(), dto.getFechaNacimiento(), dto.getNumeroTelefono());
                    pacienteService.save((Paciente) usuario);
                    break;

                case "FARMACEUTA":
                    usuario = new Farmaceuta(dto.getId(), "", dto.getNombre());
                    farmaceutaService.save((Farmaceuta) usuario);
                    break;

                default:
                    return new ResponseDto(false, "Rol no reconocido: " + dto.getRol(), null);
            }

            authService.asignarClaveHasheada(usuario, dto.getClave());

            UsuarioResponseDto responseDto = new UsuarioResponseDto(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getRol().toString()
            );

            return new ResponseDto(true, "Usuario creado exitosamente", gson.toJson(responseDto));
        } catch (Exception e) {
            return new ResponseDto(false, "Error al crear usuario: " + e.getMessage(), null);
        }
    }
}