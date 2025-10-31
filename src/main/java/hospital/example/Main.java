package hospital.example;

import hospital.example.API.controllers.*;
import hospital.example.DataAccess.HibernateUtil;
import hospital.example.DataAccess.services.*;
import hospital.example.Domain.models.*;
import hospital.example.Server.MessageBroadcaster;
import hospital.example.Server.SocketServer;
import hospital.example.Utilities.Rol;

import java.util.Date;

public class Main {
    public static void main(String[] args) {
        var sessionFactory = HibernateUtil.getSessionFactory();

        // Servicios
        AuthService authService = new AuthService(sessionFactory);
        PacienteService pacienteService = new PacienteService(sessionFactory);
        MedicoService medicoService = new MedicoService(sessionFactory);
        FarmaceutaService farmaceutaService = new FarmaceutaService(sessionFactory);
        AdminService adminService = new AdminService(sessionFactory);
        MedicamentoService medicamentoService = new MedicamentoService(sessionFactory);
        RecetaService recetaService = new RecetaService(sessionFactory);

        // Controladores
        AuthController authController = new AuthController(authService);

        UsuarioController usuarioController = new UsuarioController(
                authService,
                adminService,
                medicoService,
                pacienteService,
                farmaceutaService
        );

        PacienteController pacienteController = new PacienteController(pacienteService);
        MedicoController medicoController = new MedicoController(medicoService);
        FarmaceutaController farmaceutaController = new FarmaceutaController(farmaceutaService);
        AdminController adminController = new AdminController(adminService);
        MedicamentoController medicamentoController = new MedicamentoController(medicamentoService);
        RecetaController recetaController = new RecetaController(recetaService, pacienteService, medicamentoService);

        // ----------------------------
        // Datos iniciales de prueba
        // ----------------------------
        boolean crearDatosIniciales = true;
        if (crearDatosIniciales) {
            // Admins
            for (int i = 1; i <= 5; i++) {
                Admin admin = new Admin();
                admin.setId(i);
                admin.setNombre("Admin" + i);
                admin.setRol(Rol.ADMINISTRADOR);
                authService.asignarClaveHasheada(admin, "admin" + i);
            }

            // Farmaceutas
            for (int i = 1; i <= 5; i++) {
                Farmaceuta f = new Farmaceuta();
                f.setId(i + 5); // IDs 6-10
                f.setNombre("Farmaceuta" + i);
                f.setRol(Rol.FARMACEUTA);
                authService.asignarClaveHasheada(f, "farm" + i);
            }

            // Médicos
            for (int i = 1; i <= 5; i++) {
                Medico m = new Medico();
                m.setId(i + 10); // IDs 11-15
                m.setNombre("Medico" + i);
                m.setEspecialidad("Cardiología");
                m.setRol(Rol.MEDICO);
                authService.asignarClaveHasheada(m, "med" + i);
            }

            // Pacientes
            for (int i = 1; i <= 5; i++) {
                Paciente p = new Paciente();
                p.setId(i + 15); // IDs 16-20
                p.setNombre("Paciente" + i);
                p.setFechaNacimiento(new Date());
                p.setNumeroTelefono("8888-000" + i);
                p.setRol(Rol.PACIENTE);
                authService.asignarClaveHasheada(p, "pac" + i);
            }

            // Medicamentos
            for (int i = 1; i <= 5; i++) {
                Medicamento m = new Medicamento("M" + i, "Ibuprofeno " + i + "mg", "Tabletas");
                medicamentoService.save(m);
            }
        }

        // ----------------------------
        // Iniciar servidores
        // ----------------------------
        int requestPort = 7070;
        SocketServer requestServer = new SocketServer(
                requestPort,
                authController,
                usuarioController,
                pacienteController,
                medicoController,
                farmaceutaController,
                adminController,
                medicamentoController,
                recetaController
        );

        int messagePort = 7001;
        MessageBroadcaster broadcaster = new MessageBroadcaster(messagePort, requestServer);
        requestServer.setMessageBroadcaster(broadcaster);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nApagando servidores...");
            requestServer.stop();
            broadcaster.stop();
        }));

        requestServer.start();
        broadcaster.start();
        System.out.println("🚀 Servidores de Hospital iniciados - Requests: " + requestPort + ", Broadcast: " + messagePort);
    }
}