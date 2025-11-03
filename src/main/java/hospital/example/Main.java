package hospital.example;

import hospital.example.API.controllers.*;
import hospital.example.DataAccess.HibernateUtil;
import hospital.example.DataAccess.services.*;
import hospital.example.Domain.models.*;
import hospital.example.Server.MessageBroadcaster;
import hospital.example.Server.SocketServer;

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
        boolean crearDatosIniciales = false; //Se cambia a false despues de primera ejecución
        if (crearDatosIniciales) {
            System.out.println("\n=================================");
            System.out.println("  CREANDO DATOS INICIALES");
            System.out.println("=================================\n");

            // ----------------------------
            // 1. ADMINS (IDs 1-5)
            // ----------------------------
            System.out.println("📋 Creando Administradores...");
            for (int i = 1; i <= 5; i++) {
                Admin admin = new Admin(i, "", "Admin" + i);
                if (adminService.save(admin)) {
                    authService.asignarClaveHasheada(admin, "admin" + i);
                    System.out.println("  ✓ Admin" + i + " creado (user: Admin" + i + ", pass: admin" + i + ")");
                } else {
                    System.out.println("  ✗ Error al crear Admin" + i);
                }
            }

            // ----------------------------
            // 2. FARMACEUTAS (IDs 6-10)
            // ----------------------------
            System.out.println("\n💊 Creando Farmacéutas...");
            for (int i = 6; i <= 10; i++) {
                Farmaceuta f = new Farmaceuta(i, "", "Farmaceuta" + (i-5));
                if (farmaceutaService.save(f)) {
                    authService.asignarClaveHasheada(f, "farm" + (i-5));
                    System.out.println("  ✓ Farmaceuta" + (i-5) + " creado (user: Farmaceuta" + (i-5) + ", pass: farm" + (i-5) + ")");
                } else {
                    System.out.println("  ✗ Error al crear Farmaceuta" + (i-5));
                }
            }

            // ----------------------------
            // 3. MÉDICOS (IDs 11-15)
            // ----------------------------
            System.out.println("\n👨‍⚕️ Creando Médicos...");
            String[] especialidades = {"Cardiología", "Pediatría", "Neurología", "Dermatología", "Traumatología"};
            for (int i = 11; i <= 15; i++) {
                String especialidad = especialidades[i - 11];
                Medico m = new Medico(i, "", "Medico" + (i-10), especialidad);
                if (medicoService.save(m)) {
                    authService.asignarClaveHasheada(m, "med" + (i-10));
                    System.out.println("  ✓ Medico" + (i-10) + " creado - " + especialidad + " (user: Medico" + (i-10) + ", pass: med" + (i-10) + ")");
                } else {
                    System.out.println("  ✗ Error al crear Medico" + (i-10));
                }
            }

            // ----------------------------
            // 4. PACIENTES (IDs 16-20)
            // ----------------------------
            System.out.println("\n🏥 Creando Pacientes...");
            for (int i = 16; i <= 20; i++) {
                Paciente p = new Paciente(
                        i,
                        "",
                        "Paciente" + (i-15),
                        new Date(),
                        "8888-000" + (i-15)
                );
                if (pacienteService.save(p)) {
                    authService.asignarClaveHasheada(p, "pac" + (i-15));
                    System.out.println("  ✓ Paciente" + (i-15) + " creado (user: Paciente" + (i-15) + ", pass: pac" + (i-15) + ")");
                } else {
                    System.out.println("  ✗ Error al crear Paciente" + (i-15));
                }
            }

            // ----------------------------
            // 5. MEDICAMENTOS
            // ----------------------------
            System.out.println("\n💉 Creando Medicamentos...");
            String[][] medicamentosData = {
                    {"M001", "Ibuprofeno 400mg", "Tabletas"},
                    {"M002", "Paracetamol 500mg", "Tabletas"},
                    {"M003", "Amoxicilina 500mg", "Cápsulas"},
                    {"M004", "Omeprazol 20mg", "Cápsulas"},
                    {"M005", "Loratadina 10mg", "Tabletas"},
                    {"M006", "Metformina 850mg", "Tabletas"},
                    {"M007", "Atorvastatina 20mg", "Tabletas"},
                    {"M008", "Losartán 50mg", "Tabletas"},
                    {"M009", "Diclofenaco 75mg", "Tabletas"},
                    {"M010", "Cetirizina 10mg", "Tabletas"}
            };

            for (String[] medData : medicamentosData) {
                Medicamento m = new Medicamento(medData[0], medData[1], medData[2]);
                if (medicamentoService.save(m)) {
                    System.out.println("  ✓ " + medData[0] + " - " + medData[1] + " creado");
                } else {
                    System.out.println("  ✗ Error al crear " + medData[0]);
                }
            }

            System.out.println("\n=================================");
            System.out.println("  ✅ DATOS INICIALES CREADOS");
            System.out.println("=================================");
            System.out.println("\n📝 CREDENCIALES DE PRUEBA:");
            System.out.println("  Admin1    / admin1");
            System.out.println("  Medico1   / med1");
            System.out.println("  Farmaceuta1 / farm1");
            System.out.println("  Paciente1 / pac1");
            System.out.println("\n⚠️  IMPORTANTE: Cambia 'crearDatosIniciales' a false");
            System.out.println("   para no duplicar datos en próximas ejecuciones.\n");
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
            System.out.println("\n🛑 Apagando servidores...");
            requestServer.stop();
            broadcaster.stop();
        }));

        requestServer.start();
        broadcaster.start();

        System.out.println("\n=================================");
        System.out.println("🚀 SERVIDORES INICIADOS");
        System.out.println("=================================");
        System.out.println("📡 Requests:   localhost:" + requestPort);
        System.out.println("📢 Broadcast:  localhost:" + messagePort);
        System.out.println("=================================\n");
    }
}