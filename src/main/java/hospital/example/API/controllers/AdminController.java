package hospital.example.API.controllers;

import com.google.gson.Gson;
import hospital.example.DataAccess.services.AdminService;
import hospital.example.Domain.dtos.Admin.AdminCreateDto;
import hospital.example.Domain.dtos.Admin.AdminResponseDto;
import hospital.example.Domain.dtos.RequestDto;
import hospital.example.Domain.dtos.ResponseDto;
import hospital.example.Domain.models.Admin;

import java.util.List;
import java.util.stream.Collectors;

public class AdminController {
    private final AdminService adminService;
    private final Gson gson = new Gson();

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    public ResponseDto route(RequestDto request) {
        try {
            switch (request.getRequest()) {
                case "getAllAdmins":
                    return handleGetAll();
                case "createAdmin":
                    return handleCreate(request);
                case "deleteAdmin":
                    return handleDelete(request);
                default:
                    return new ResponseDto(false, "Comando no reconocido en AdminController", null);
            }
        } catch (Exception e) {
            return new ResponseDto(false, "Error en AdminController: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleGetAll() {
        List<Admin> admins = adminService.findAll();

        List<AdminResponseDto> dtos = admins.stream()
                .map(a -> new AdminResponseDto(a.getId(), a.getNombre()))
                .collect(Collectors.toList());

        return new ResponseDto(true, "Admins cargados correctamente", gson.toJson(dtos));
    }

    private ResponseDto handleCreate(RequestDto request) {
        try {
            AdminCreateDto dto = gson.fromJson(request.getData(), AdminCreateDto.class);
            Admin admin = new Admin(dto.getId(), "", dto.getNombre());

            boolean success = adminService.save(admin);
            return new ResponseDto(success, success ? "Admin creado correctamente" : "Error al guardar", null);

        } catch (Exception e) {
            return new ResponseDto(false, "Error al crear admin: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleDelete(RequestDto request) {
        int id = Integer.parseInt(request.getData());
        boolean success = adminService.delete(id);
        return new ResponseDto(success, success ? "Admin eliminado" : "No se encontró el admin", null);
    }
}