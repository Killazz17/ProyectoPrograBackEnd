package hospital.example.API.controllers;

import com.google.gson.Gson;
import hospital.example.DataAccess.services.AuthService;
import hospital.example.Domain.dtos.RequestDto;
import hospital.example.Domain.dtos.ResponseDto;
import hospital.example.Domain.dtos.auth.LoginRequestDto;
import hospital.example.Domain.dtos.auth.UserResponseDto;
import hospital.example.Domain.models.Usuario;

public class AuthController {
    private final AuthService authService;
    private final Gson gson = new Gson();

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public ResponseDto route(RequestDto request) {
        try {
            switch (request.getRequest()) {
                case "login":
                    return handleLogin(request);
                default:
                    return new ResponseDto(false, "Comando no reconocido en AuthController", null);
            }
        } catch (Exception e) {
            return new ResponseDto(false, "Error en AuthController: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleLogin(RequestDto request) {
        LoginRequestDto dto = gson.fromJson(request.getData(), LoginRequestDto.class);
        Usuario usuario = authService.login(dto.getId(), dto.getClave());

        if (usuario == null) {
            return new ResponseDto(false, "Credenciales inválidas", null);
        }

        UserResponseDto userDto = new UserResponseDto(usuario.getId(), usuario.getNombre(), usuario.getRol().toString());
        return new ResponseDto(true, "Login exitoso", gson.toJson(userDto));
    }
}