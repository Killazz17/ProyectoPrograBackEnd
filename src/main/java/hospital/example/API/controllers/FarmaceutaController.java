package hospital.example.API.controllers;

import com.google.gson.Gson;
import hospital.example.DataAccess.services.FarmaceutaService;
import hospital.example.Domain.dtos.Farmaceuta.FarmaceutaCreateDto;
import hospital.example.Domain.dtos.Farmaceuta.FarmaceutaResponseDto;
import hospital.example.Domain.dtos.RequestDto;
import hospital.example.Domain.dtos.ResponseDto;
import hospital.example.Domain.models.Farmaceuta;

import java.util.List;
import java.util.stream.Collectors;

public class FarmaceutaController {
    private final FarmaceutaService farmaceutaService;
    private final Gson gson = new Gson();

    public FarmaceutaController(FarmaceutaService farmaceutaService) {
        this.farmaceutaService = farmaceutaService;
    }

    public ResponseDto route(RequestDto request) {
        try {
            switch (request.getRequest()) {
                case "getAllFarmaceutas":
                    return handleGetAll();
                case "createFarmaceuta":
                    return handleCreate(request);
                case "deleteFarmaceuta":
                    return handleDelete(request);
                default:
                    return new ResponseDto(false, "Comando no reconocido en FarmaceutaController", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "Error en FarmaceutaController: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleGetAll() {
        List<Farmaceuta> farmaceutas = farmaceutaService.findAll();

        List<FarmaceutaResponseDto> dtos = farmaceutas.stream()
                .map(f -> new FarmaceutaResponseDto(f.getId(), f.getNombre()))
                .collect(Collectors.toList());

        return new ResponseDto(true, "Farmacéutas cargados correctamente", gson.toJson(dtos));
    }

    private ResponseDto handleCreate(RequestDto request) {
        try {
            FarmaceutaCreateDto dto = gson.fromJson(request.getData(), FarmaceutaCreateDto.class);
            // Constructor Farmaceuta: (id, claveHash, nombre)
            Farmaceuta f = new Farmaceuta(dto.getId(), "", dto.getNombre());
            boolean success = farmaceutaService.save(f);

            return new ResponseDto(success, success ? "Farmacéuta creado correctamente" : "Error al guardar", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "Error al crear farmacéuta: " + e.getMessage(), null);
        }
    }

    private ResponseDto handleDelete(RequestDto request) {
        int id = Integer.parseInt(request.getData());
        boolean success = farmaceutaService.delete(id);
        return new ResponseDto(success, success ? "Farmacéuta eliminado" : "No se encontró el farmacéuta", null);
    }
}