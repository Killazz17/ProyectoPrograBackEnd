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
        boolean crearDatosIniciales = false; // Poner en true para cargar datos quemados.
        if (crearDatosIniciales) {
            System.out.println("Creando datos iniciales...");

            // Admins
            for (int i = 1; i <= 5; i++) {
                Admin admin = new Admin(i, "", "Admin" + i);
                adminService.save(admin);
                authService.asignarClaveHasheada(admin, "admin" + i);
                System.out.println("✓ Admin" + i + " creado");
            }

            // Farmaceutas
            for (int i = 6; i <= 10; i++) {
                Farmaceuta f = new Farmaceuta(i, "", "Farmaceuta" + (i-5));
                farmaceutaService.save(f);
                authService.asignarClaveHasheada(f, "farm" + (i-5));
                System.out.println("✓ Farmaceuta" + (i-5) + " creado");
            }

            // Médicos
            for (int i = 11; i <= 15; i++) {
                Medico m = new Medico(i, "", "Medico" + (i-10), "Cardiología");
                medicoService.save(m);
                authService.asignarClaveHasheada(m, "med" + (i-10));
                System.out.println("✓ Medico" + (i-10) + " creado");
            }

            // Pacientes
            for (int i = 16; i <= 20; i++) {
                Paciente p = new Paciente(i, "", "Paciente" + (i-15), new Date(), "8888-000" + (i-15));
                pacienteService.save(p);
                authService.asignarClaveHasheada(p, "pac" + (i-15));
                System.out.println("✓ Paciente" + (i-15) + " creado");
            }

            // Medicamentos
            for (int i = 1; i <= 5; i++) {
                Medicamento m = new Medicamento("M" + i, "Ibuprofeno " + i + "mg", "Tabletas");
                medicamentoService.save(m);
                System.out.println("✓ Medicamento M" + i + " creado");
            }

            System.out.println("\n✅ Datos iniciales creados exitosamente!\n");
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
        System.out.println("🚀 Servidores de Hospital iniciados");
        System.out.println("   → Requests: localhost:" + requestPort);
        System.out.println("   → Broadcast: localhost:" + messagePort);
    }
}