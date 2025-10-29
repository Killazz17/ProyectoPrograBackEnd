package hospital.example.API.controllers;

import com.google.gson.Gson;
import hospital.example.Domain.dtos.RequestDto;
import hospital.example.Domain.dtos.ResponseDto;
import hospital.example.Domain.dtos.auth.LoginRequestDto;
import hospital.example.Domain.dtos.auth.RegisterRequestDto;
import hospital.example.Domain.dtos.auth.UserResponseDto;
import hospital.example.Domain.models.Usuario;
import hospital.example.DataAccess.services.AuthService;

public class AuthController {
    private final AuthService authService;
    private final Gson gson = new Gson();

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Single routing entry point for the handler
    public ResponseDto route(RequestDto request) {
        try {
            switch (request.getRequest()) {
                case "login":
                    return handleLogin(request);
                case "register":
                    return handleRegister(request);
                case "logout":
                    return handleLogout(request);
                default:
                    return new ResponseDto(false, "Unknown request: " + request.getRequest(), null);
            }
        } catch (Exception e) {
            return new ResponseDto(false, e.getMessage(), null);
        }
    }

    // --- LOGIN ---
    private ResponseDto handleLogin(RequestDto request) {
        try {
            LoginRequestDto loginDto = gson.fromJson(request.getData(), LoginRequestDto.class);

            boolean success = authService.login(loginDto.getUsernameOrEmail(), loginDto.getPassword());
            if (!success) {
                return new ResponseDto(false, "Invalid credentials", null);
            }

            UserResponseDto userDto = getUserByUsername(loginDto.getUsernameOrEmail());
            return new ResponseDto(true, "Login successful", gson.toJson(userDto));
        } catch (Exception e) {
            System.out.println("Error in handleLogin: " + e.getMessage());
            throw e;
        }
    }

    // --- REGISTER ---
    private ResponseDto handleRegister(RequestDto request) {
        try {
            RegisterRequestDto regDto = gson.fromJson(request.getData(), RegisterRequestDto.class);
            Usuario user = authService.register(regDto.getUsername(), regDto.getEmail(), regDto.getPassword(), regDto.getRole());

            UserResponseDto userDto = new UserResponseDto(
                  //user.getId()
            );

            return new ResponseDto(true, "User registered successfully", gson.toJson(userDto));
        } catch (Exception e) {
            System.out.println("Error in handleRegister: " + e.getMessage());
            throw e;
        }
    }

    // --- LOGOUT ---
    private ResponseDto handleLogout(RequestDto request) {
        try {
            return new ResponseDto(true, "Logout successful", null);
        } catch (Exception e) {
            System.out.println("Error in handleLogout: " + e.getMessage());
            throw e;
        }
    }

    // --- HELPER: GET USER BY USERNAME ---
    public UserResponseDto getUserByUsername(String username) {
        try {
            Usuario user = authService.getUserByUsername(username);
            if (user == null) return null;

            return new UserResponseDto(
                    //user.getId()
            );
        } catch (Exception e) {
            System.out.println("Error in getUserByUsername: " + e.getMessage());
            throw e;
        }
    }
}
