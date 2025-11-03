package hospital.example.Server;

import hospital.example.API.controllers.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SocketServer {

    private final int port;

    // Controladores
    private final AuthController authController;
    private final UsuarioController usuarioController;
    private final PacienteController pacienteController;
    private final MedicoController medicoController;
    private final FarmaceutaController farmaceutaController;
    private final AdminController adminController;
    private final MedicamentoController medicamentoController;
    private final RecetaController recetaController;
    private final MedicamentoPrescritoController medicamentoPrescritoController;

    private ServerSocket serverSocket;
    private final List<ClientHandler> activeClients = new CopyOnWriteArrayList<>();
    private MessageBroadcaster messageBroadcaster;

    public SocketServer(int port,
                        AuthController authController,
                        UsuarioController usuarioController,
                        PacienteController pacienteController,
                        MedicoController medicoController,
                        FarmaceutaController farmaceutaController,
                        AdminController adminController,
                        MedicamentoController medicamentoController,
                        RecetaController recetaController,
                        MedicamentoPrescritoController medicamentoPrescritoController) {

        this.port = port;
        this.authController = authController;
        this.usuarioController = usuarioController;
        this.pacienteController = pacienteController;
        this.medicoController = medicoController;
        this.farmaceutaController = farmaceutaController;
        this.adminController = adminController;
        this.medicamentoController = medicamentoController;
        this.recetaController = recetaController;
        this.medicamentoPrescritoController = medicamentoPrescritoController;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("[SocketServer] Escuchando en puerto " + port);

            new Thread(this::acceptConnections, "SocketServer-Acceptor").start();

        } catch (IOException e) {
            System.err.println("[SocketServer] Error al iniciar: " + e.getMessage());
        }
    }

    private void acceptConnections() {
        try {
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SocketServer] Nuevo cliente: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(
                        clientSocket,
                        authController,
                        usuarioController,
                        pacienteController,
                        medicoController,
                        farmaceutaController,
                        adminController,
                        medicamentoController,
                        recetaController,
                        medicamentoPrescritoController,
                        this
                );
                activeClients.add(handler);
                new Thread(handler, "ClientHandler-" + activeClients.size()).start();
            }

        } catch (IOException e) {
            System.err.println("[SocketServer] Error en conexión: " + e.getMessage());
        }
    }

    public void removeClient(ClientHandler handler) {
        activeClients.remove(handler);
        System.out.println("[SocketServer] Cliente removido. Activos: " + activeClients.size());
    }

    public void broadcast(Object message) {
        System.out.println("[SocketServer] Enviando a " + activeClients.size() + " clientes: " + message);

        if (messageBroadcaster != null) {
            messageBroadcaster.broadcastToAll(message);
        }
    }

    public void setMessageBroadcaster(MessageBroadcaster broadcaster) {
        this.messageBroadcaster = broadcaster;
        System.out.println("[SocketServer] Broadcaster configurado");
    }

    public int getActiveClientCount() {
        return activeClients.size();
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("[SocketServer] Servidor detenido.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}