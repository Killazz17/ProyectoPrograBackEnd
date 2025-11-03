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
            System.out.println("[AuthController] Comando recibido: " + request.getRequest());

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

    // Login por ID (compatibilidad)
    private ResponseDto handleLogin(RequestDto request) {
        try {
            LoginRequestDto dto = gson.fromJson(request.getData(), LoginRequestDto.class);
            Usuario usuario = authService.login(dto.getId(), dto.getClave());

            if (usuario == null) {
                return new ResponseDto(false, "Credenciales inválidas", null);
            }

            UserResponseDto userDto = new UserResponseDto(usuario.getId(), usuario.getNombre(), usuario.getRol().toString());
            return new ResponseDto(true, "Login exitoso", gson.toJson(userDto));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "Error en login: " + e.getMessage(), null);
        }
    }

    // Login por nombre de usuario
    private ResponseDto handleLoginByNombre(RequestDto request) {
        try {
            System.out.println("[AuthController] Procesando loginByNombre, data: " + request.getData());

            // Esperamos un JSON con { "nombre": "...", "clave": "..." }
            com.google.gson.JsonObject jsonObj = gson.fromJson(request.getData(), com.google.gson.JsonObject.class);
            String nombre = jsonObj.get("nombre").getAsString();
            String clave = jsonObj.get("clave").getAsString();

            System.out.println("[AuthController] Intentando login con nombre: " + nombre);

            Usuario usuario = authService.loginByNombre(nombre, clave);

            if (usuario == null) {
                System.out.println("[AuthController] Login fallido para: " + nombre);
                return new ResponseDto(false, "Credenciales inválidas", null);
            }

            System.out.println("[AuthController] Login exitoso para: " + nombre);
            UserResponseDto userDto = new UserResponseDto(usuario.getId(), usuario.getNombre(), usuario.getRol().toString());
            return new ResponseDto(true, "Login exitoso", gson.toJson(userDto));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "Error en loginByNombre: " + e.getMessage(), null);
        }
    }

    // Cambiar contraseña
    private ResponseDto handleChangePassword(RequestDto request) {
        try {
            System.out.println("[AuthController] Procesando changePassword");

            com.google.gson.JsonObject jsonObj = gson.fromJson(request.getData(), com.google.gson.JsonObject.class);
            String nombreUsuario = jsonObj.get("nombreUsuario").getAsString();
            String claveActual = jsonObj.get("claveActual").getAsString();
            String claveNueva = jsonObj.get("claveNueva").getAsString();

            System.out.println("[AuthController] Cambio de contraseña para: " + nombreUsuario);

            // Verificar credenciales actuales
            Usuario usuario = authService.loginByNombre(nombreUsuario, claveActual);

            if (usuario == null) {
                System.out.println("[AuthController] Contraseña actual incorrecta");
                return new ResponseDto(false, "Contraseña actual incorrecta", null);
            }

            // Cambiar la contraseña
            boolean success = authService.cambiarClave(usuario.getId(), claveNueva);

            if (success) {
                System.out.println("[AuthController] Contraseña cambiada exitosamente");
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