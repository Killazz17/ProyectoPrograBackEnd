package hospital.example.Server;

import com.google.gson.Gson;
import hospital.example.API.controllers.*;
import hospital.example.Domain.dtos.RequestDto;
import hospital.example.Domain.dtos.ResponseDto;
import hospital.example.Domain.dtos.auth.UserResponseDto;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final AuthController authController;
    private final UsuarioController usuarioController;
    private final PacienteController pacienteController;
    private final MedicoController medicoController;
    private final FarmaceutaController farmaceutaController;
    private final AdminController adminController;
    private final MedicamentoController medicamentoController;
    private final RecetaController recetaController;
    private final MedicamentoPrescritoController medicamentoPrescritoController;
    private final SocketServer server;
    private final Gson gson = new Gson();
    private PrintWriter out;

    public ClientHandler(Socket clientSocket,
                         AuthController authController,
                         UsuarioController usuarioController,
                         PacienteController pacienteController,
                         MedicoController medicoController,
                         FarmaceutaController farmaceutaController,
                         AdminController adminController,
                         MedicamentoController medicamentoController,
                         RecetaController recetaController,
                         MedicamentoPrescritoController medicamentoPrescritoController,
                         SocketServer server) {
        this.clientSocket = clientSocket;
        this.authController = authController;
        this.usuarioController = usuarioController;
        this.pacienteController = pacienteController;
        this.medicoController = medicoController;
        this.farmaceutaController = farmaceutaController;
        this.adminController = adminController;
        this.medicamentoController = medicamentoController;
        this.recetaController = recetaController;
        this.medicamentoPrescritoController = medicamentoPrescritoController;
        this.server = server;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println("[ClientHandler] Conectado: " + Thread.currentThread().getName());

            String inputJson;
            while ((inputJson = in.readLine()) != null) {
                System.out.println("[ClientHandler] Recibido: " + inputJson);

                RequestDto request = gson.fromJson(inputJson, RequestDto.class);
                ResponseDto response = handleRequest(request);

                Thread.sleep(200); // Espera opcional
                out.println(gson.toJson(response));
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("[ClientHandler] Cliente desconectado: " + Thread.currentThread().getName());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignore) {}
            server.removeClient(this);
        }
    }

    private ResponseDto handleRequest(RequestDto request) {
        switch (request.getController()) {
            case "Auth":
                ResponseDto response = authController.route(request);

                // Emitir notificación si login fue exitoso
                if ("login".equals(request.getRequest()) && response.isSuccess()) {
                    UserResponseDto user = gson.fromJson(response.getData(), UserResponseDto.class);
                    String msg = "Usuario " + user.getNombre() + " (" + user.getRol() + ") se conectó.";
                    System.out.println("[ClientHandler] Broadcast: " + msg);
                    server.broadcast(msg);
                }
                return response;

            case "Usuarios":
                return usuarioController.route(request);
            case "Pacientes":
                return pacienteController.route(request);
            case "Medicos":
                return medicoController.route(request);
            case "Farmaceutas":
                return farmaceutaController.route(request);
            case "Admins":
                return adminController.route(request);
            case "Medicamentos":
                return medicamentoController.route(request);
            case "Recetas":
                return recetaController.route(request);
            case "MedicamentosPrescritos":
                return medicamentoPrescritoController.route(request);

            default:
                return new ResponseDto(false, "Controlador desconocido: " + request.getController(), null);
        }
    }

    public void sendMessage(Object message) {
        if (out != null) {
            String jsonMessage = gson.toJson(message);
            out.println(jsonMessage);
            System.out.println("[ClientHandler] Enviado: " + jsonMessage);
        }
    }
}
