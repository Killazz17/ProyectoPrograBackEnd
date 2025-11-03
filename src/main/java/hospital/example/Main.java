// src/main/java/hospital/example/Main.java
package hospital.example;

import hospital.example.API.controllers.*;
import hospital.example.DataAccess.HibernateUtil;
import hospital.example.DataAccess.services.*;
import hospital.example.Domain.models.*;
import hospital.example.Server.MessageBroadcaster;
import hospital.example.Server.SocketServer;
import hospital.example.Utilities.EstadoReceta;

import java.time.LocalDate;
import java.util.Calendar;
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
        MedicamentoPrescritoController medicamentoPrescritoController = new MedicamentoPrescritoController(recetaService, medicamentoService);

        // ----------------------------
        // Datos iniciales de prueba
        // ----------------------------
        boolean crearDatosIniciales = false; // ⚠️ Cambiar a false después de primera ejecución
        if (crearDatosIniciales) {
            System.out.println("\n╔═══════════════════════════════════════════╗");
            System.out.println("║    CREANDO DATOS INICIALES REALISTAS      ║");
            System.out.println("╚═══════════════════════════════════════════╝\n");

            // ----------------------------
            // 1. ADMINS (IDs 1-3) - SOLO USERNAME
            // ----------------------------
            System.out.println("👨‍💼 Creando Administradores...");
            for (int i = 1; i <= 3; i++) {
                Admin admin = new Admin(i, "", "Admin" + i); // ✅ SOLO "Admin1"
                if (adminService.save(admin)) {
                    authService.asignarClaveHasheada(admin, "admin" + i);
                    System.out.println("  ✓ Admin" + i + " (user: Admin" + i + ", pass: admin" + i + ")");
                }
            }

            // ----------------------------
            // 2. FARMACEUTAS (IDs 4-7) - SOLO USERNAME
            // ----------------------------
            System.out.println("\n💊 Creando Farmacéutas...");
            for (int i = 1; i <= 4; i++) {
                int id = 3 + i;
                Farmaceuta f = new Farmaceuta(id, "", "Farmaceuta" + i); // ✅ SOLO "Farmaceuta1"
                if (farmaceutaService.save(f)) {
                    authService.asignarClaveHasheada(f, "farm" + i);
                    System.out.println("  ✓ Farmaceuta" + i + " (user: Farmaceuta" + i + ", pass: farm" + i + ")");
                }
            }

            // ----------------------------
            // 3. MÉDICOS (IDs 8-13) - SOLO USERNAME
            // ----------------------------
            System.out.println("\n👨‍⚕️ Creando Médicos...");
            String[] especialidades = {"Cardiología", "Pediatría", "Medicina Interna",
                    "Neurología", "Dermatología", "Traumatología"};

            for (int i = 1; i <= 6; i++) {
                int id = 7 + i;
                Medico m = new Medico(id, "", "Medico" + i, especialidades[i-1]); // ✅ SOLO "Medico1"
                if (medicoService.save(m)) {
                    authService.asignarClaveHasheada(m, "med" + i);
                    System.out.println("  ✓ Medico" + i + " - " + especialidades[i-1] +
                            " (user: Medico" + i + ", pass: med" + i + ")");
                }
            }

            // ----------------------------
            // 4. PACIENTES (IDs 14-23) - SOLO USERNAME
            // ----------------------------
            System.out.println("\n🏥 Creando Pacientes...");
            String[][] fechasTels = {
                    {"1985-03-15", "8888-1234"},
                    {"1990-07-22", "8888-2345"},
                    {"1978-11-08", "8888-3456"},
                    {"1995-01-30", "8888-4567"},
                    {"1982-05-17", "8888-5678"},
                    {"1988-09-25", "8888-6789"},
                    {"1975-12-03", "8888-7890"},
                    {"1992-04-14", "8888-8901"},
                    {"1980-08-19", "8888-9012"},
                    {"1998-02-28", "8888-0123"}
            };

            for (int i = 1; i <= 10; i++) {
                int id = 13 + i;
                Date fechaNac = parseFecha(fechasTels[i-1][0]);

                Paciente p = new Paciente(id, "", "Paciente" + i, fechaNac, fechasTels[i-1][1]); // ✅ SOLO "Paciente1"
                if (pacienteService.save(p)) {
                    authService.asignarClaveHasheada(p, "pac" + i);
                    System.out.println("  ✓ Paciente" + i + " (user: Paciente" + i + ", pass: pac" + i + ")");
                }
            }

            // ----------------------------
            // 5. MEDICAMENTOS (30 medicamentos)
            // ----------------------------
            System.out.println("\n💉 Creando Medicamentos...");
            String[][] medicamentosData = {
                    {"M001", "Ibuprofeno 400mg", "Tabletas"},
                    {"M002", "Paracetamol 500mg", "Tabletas"},
                    {"M003", "Diclofenaco 75mg", "Tabletas"},
                    {"M004", "Naproxeno 500mg", "Tabletas"},
                    {"M005", "Ketorolaco 10mg", "Tabletas"},
                    {"M006", "Amoxicilina 500mg", "Cápsulas"},
                    {"M007", "Azitromicina 500mg", "Tabletas"},
                    {"M008", "Ciprofloxacino 500mg", "Tabletas"},
                    {"M009", "Cefalexina 500mg", "Cápsulas"},
                    {"M010", "Omeprazol 20mg", "Cápsulas"},
                    {"M011", "Ranitidina 150mg", "Tabletas"},
                    {"M012", "Metoclopramida 10mg", "Tabletas"},
                    {"M013", "Loratadina 10mg", "Tabletas"},
                    {"M014", "Cetirizina 10mg", "Tabletas"},
                    {"M015", "Desloratadina 5mg", "Tabletas"},
                    {"M016", "Losartán 50mg", "Tabletas"},
                    {"M017", "Enalapril 10mg", "Tabletas"},
                    {"M018", "Atorvastatina 20mg", "Tabletas"},
                    {"M019", "Metoprolol 50mg", "Tabletas"},
                    {"M020", "Metformina 850mg", "Tabletas"},
                    {"M021", "Glibenclamida 5mg", "Tabletas"},
                    {"M022", "Salbutamol 100mcg", "Inhalador"},
                    {"M023", "Beclometasona 250mcg", "Inhalador"},
                    {"M024", "Carbamazepina 200mg", "Tabletas"},
                    {"M025", "Gabapentina 300mg", "Cápsulas"},
                    {"M026", "Clonazepam 2mg", "Tabletas"},
                    {"M027", "Tramadol 50mg", "Cápsulas"},
                    {"M028", "Ácido Fólico 5mg", "Tabletas"},
                    {"M029", "Complejo B", "Tabletas"},
                    {"M030", "Vitamina D 1000UI", "Cápsulas"}
            };

            for (String[] medData : medicamentosData) {
                Medicamento m = new Medicamento(medData[0], medData[1], medData[2]);
                if (medicamentoService.save(m)) {
                    System.out.println("  ✓ " + medData[0] + " - " + medData[1]);
                }
            }

            // ----------------------------
            // 6. RECETAS (50 recetas)
            // ----------------------------
            System.out.println("\n📋 Creando Recetas...");

            LocalDate hoy = LocalDate.now();

            for (int i = 0; i < 50; i++) {
                Receta receta = new Receta();

                // Paciente aleatorio (IDs 14-23)
                int idPaciente = 14 + (i % 10);
                Paciente paciente = pacienteService.findById(idPaciente);
                receta.setPaciente(paciente);

                // Fecha de confección: distribuida en últimos 12 meses
                int diasAtras = (i * 7) % 365;
                LocalDate fechaConfeccion = hoy.minusDays(diasAtras);
                receta.setFechaConfeccion(fechaConfeccion);
                receta.setFechaRetiro(fechaConfeccion.plusDays(7));

                // Estado según antigüedad
                EstadoReceta estado;
                if (diasAtras < 7) estado = EstadoReceta.confeccionada;
                else if (diasAtras < 14) estado = EstadoReceta.proceso;
                else if (diasAtras < 30) estado = EstadoReceta.lista;
                else estado = EstadoReceta.entregada;
                receta.setEstado(estado);

                // Agregar 1-4 medicamentos
                int cantidadMeds = 1 + (i % 4);
                for (int j = 0; j < cantidadMeds; j++) {
                    String codigoMed = String.format("M%03d", ((i + j) % 30) + 1);
                    Medicamento med = medicamentoService.findByCodigo(codigoMed);

                    if (med != null) {
                        MedicamentoPrescrito mp = new MedicamentoPrescrito(
                                codigoMed,
                                1 + (j % 3),
                                7 + (j * 7),
                                generarIndicaciones(med.getNombre())
                        );
                        receta.addMedicamento(mp);
                    }
                }

                if (recetaService.createReceta(receta)) {
                    System.out.println("  ✓ Receta #" + (i+1) + " - Paciente" + (idPaciente-13) +
                            " - " + cantidadMeds + " meds - " + estado);
                }
            }

            System.out.println("\n╔═══════════════════════════════════════════╗");
            System.out.println("║      ✅ DATOS INICIALES CREADOS           ║");
            System.out.println("╚═══════════════════════════════════════════╝");
            System.out.println("\n📊 RESUMEN:");
            System.out.println("  • 3 Administradores");
            System.out.println("  • 4 Farmacéutas");
            System.out.println("  • 6 Médicos");
            System.out.println("  • 10 Pacientes");
            System.out.println("  • 30 Medicamentos");
            System.out.println("  • 50 Recetas");

            System.out.println("\n📝 CREDENCIALES:");
            System.out.println("  Admin1 / admin1");
            System.out.println("  Medico1 / med1");
            System.out.println("  Farmaceuta1 / farm1");
            System.out.println("  Paciente1 / pac1");

            System.out.println("\n⚠️  Cambia 'crearDatosIniciales' a false\n");
        }

        // Iniciar servidores
        int requestPort = 7070;
        SocketServer requestServer = new SocketServer(
                requestPort, authController, usuarioController, pacienteController,
                medicoController, farmaceutaController, adminController,
                medicamentoController, recetaController, medicamentoPrescritoController
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

        System.out.println("\n╔═══════════════════════════════════════════╗");
        System.out.println("║       🚀 SERVIDORES INICIADOS             ║");
        System.out.println("╚═══════════════════════════════════════════╝");
        System.out.println("  📡 Requests:   localhost:" + requestPort);
        System.out.println("  📢 Broadcast:  localhost:" + messagePort);
        System.out.println("═══════════════════════════════════════════════\n");
    }

    private static Date parseFecha(String fechaStr) {
        try {
            String[] partes = fechaStr.split("-");
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]) - 1, Integer.parseInt(partes[2]));
            return cal.getTime();
        } catch (Exception e) {
            return new Date();
        }
    }

    private static String generarIndicaciones(String nombreMed) {
        if (nombreMed.contains("Ibuprofeno") || nombreMed.contains("Paracetamol")) {
            return "Tomar 1 tableta cada 8 horas después de las comidas";
        } else if (nombreMed.contains("Amoxicilina") || nombreMed.contains("Azitromicina")) {
            return "Tomar 1 cápsula cada 12 horas durante tratamiento completo";
        } else if (nombreMed.contains("Omeprazol")) {
            return "Tomar 1 cápsula en ayunas antes del desayuno";
        } else if (nombreMed.contains("Loratadina") || nombreMed.contains("Cetirizina")) {
            return "Tomar 1 tableta cada 24 horas";
        } else {
            return "Seguir indicaciones del médico";
        }
    }
}