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
                case "loginByNombre":
                    return handleLoginByNombre(request);
                case "changePassword":
                    return handleChangePassword(request);
                default:
                    return new ResponseDto(false, "Comando no reconocido en AuthController: " + request.getRequest(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "Error en AuthController: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleLogin(RequestDto request) {
        try {
            LoginRequestDto dto = gson.fromJson(request.getData(), LoginRequestDto.class);
            Usuario usuario = authService.login(dto.getId(), dto.getClave());

            if (usuario == null) {
                return new ResponseDto(false, "Credenciales invalidas", null);
            }

            UserResponseDto userDto = new UserResponseDto(usuario.getId(), usuario.getNombre(), usuario.getRol().toString());
            return new ResponseDto(true, "Login exitoso", gson.toJson(userDto));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "Error en login: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleLoginByNombre(RequestDto request) {
        try {

            com.google.gson.JsonObject jsonObj = gson.fromJson(request.getData(), com.google.gson.JsonObject.class);
            String nombre = jsonObj.get("nombre").getAsString();
            String clave = jsonObj.get("clave").getAsString();

            System.out.println("[AuthController] Intentando login con nombre: " + nombre);

            Usuario usuario = authService.loginByNombre(nombre, clave);

            if (usuario == null) {
                return new ResponseDto(false, "Credenciales invalidas", null);
            }

            UserResponseDto userDto = new UserResponseDto(usuario.getId(), usuario.getNombre(), usuario.getRol().toString());
            return new ResponseDto(true, "Login exitoso", gson.toJson(userDto));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "Error en loginByNombre: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleChangePassword(RequestDto request) {
        try {

            com.google.gson.JsonObject jsonObj = gson.fromJson(request.getData(), com.google.gson.JsonObject.class);
            String nombreUsuario = jsonObj.get("nombreUsuario").getAsString();
            String claveActual = jsonObj.get("claveActual").getAsString();
            String claveNueva = jsonObj.get("claveNueva").getAsString();

            Usuario usuario = authService.loginByNombre(nombreUsuario, claveActual);

            if (usuario == null) {
                return new ResponseDto(false, "Contraseña actual incorrecta", null);
            }

            boolean success = authService.cambiarClave(usuario.getId(), claveNueva);

            if (success) {
                return new ResponseDto(true, "Contraseña cambiada exitosamente", null);
            } else {
                return new ResponseDto(false, "Error al cambiar la contraseña", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "Error en changePassword: " + e.getMessage(), null);
        }
    }
}