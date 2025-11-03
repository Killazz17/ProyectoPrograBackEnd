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
import java.util.Random;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        var sessionFactory = HibernateUtil.getSessionFactory();

        AuthService authService = new AuthService(sessionFactory);
        PacienteService pacienteService = new PacienteService(sessionFactory);
        MedicoService medicoService = new MedicoService(sessionFactory);
        FarmaceutaService farmaceutaService = new FarmaceutaService(sessionFactory);
        AdminService adminService = new AdminService(sessionFactory);
        MedicamentoService medicamentoService = new MedicamentoService(sessionFactory);
        RecetaService recetaService = new RecetaService(sessionFactory);

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
        boolean crearDatosIniciales = false;
        if (crearDatosIniciales) {

            // ----------------------------
            // 1. ADMINS (IDs 1-3)
            // ----------------------------
            System.out.println("Creando Administradores...");
            for (int i = 1; i <= 3; i++) {
                Admin admin = new Admin(i, "", "Admin" + i);
                if (adminService.save(admin)) {
                    authService.asignarClaveHasheada(admin, "admin" + i);
                    System.out.println("  ✓ Admin" + i + " (user: Admin" + i + ", pass: admin" + i + ")");
                }
            }

            // ----------------------------
            // 2. FARMACEUTAS (IDs 4-7)
            // ----------------------------
            System.out.println("\nCreando Farmacéutas...");
            for (int i = 1; i <= 4; i++) {
                int id = 3 + i;
                Farmaceuta f = new Farmaceuta(id, "", "Farmaceuta" + i);
                if (farmaceutaService.save(f)) {
                    authService.asignarClaveHasheada(f, "farm" + i);
                    System.out.println("  ✓ Farmaceuta" + i + " (user: Farmaceuta" + i + ", pass: farm" + i + ")");
                }
            }

            // ----------------------------
            // 3. MÉDICOS (IDs 8-13)
            // ----------------------------
            System.out.println("\nCreando Médicos...");
            String[] especialidades = {"Cardiología", "Pediatría", "Medicina Interna",
                    "Neurología", "Dermatología", "Traumatología"};

            for (int i = 1; i <= 6; i++) {
                int id = 7 + i;
                Medico m = new Medico(id, "", "Medico" + i, especialidades[i-1]);
                if (medicoService.save(m)) {
                    authService.asignarClaveHasheada(m, "med" + i);
                    System.out.println("  ✓ Medico" + i + " - " + especialidades[i-1] +
                            " (user: Medico" + i + ", pass: med" + i + ")");
                }
            }

            // ----------------------------
            // 4. PACIENTES (IDs 14-23)
            // ----------------------------
            System.out.println("\nCreando Pacientes...");
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

                Paciente p = new Paciente(id, "", "Paciente" + i, fechaNac, fechasTels[i-1][1]);
                if (pacienteService.save(p)) {
                    authService.asignarClaveHasheada(p, "pac" + i);
                    System.out.println("  ✓ Paciente" + i + " (user: Paciente" + i + ", pass: pac" + i + ")");
                }
            }

            // ----------------------------
            // 5. MEDICAMENTOS (30 medicamentos)
            // ----------------------------
            System.out.println("\nCreando Medicamentos...");
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
            // 6. RECETAS DISTRIBUIDAS EN 12 MESES
            // ----------------------------
            System.out.println("\nCreando 60 Recetas distribuidas en 12 meses...");

            LocalDate hoy = LocalDate.now();
            Random random = new Random();

            // Array de estados para distribución (25% cada uno)
            EstadoReceta[] estados = {
                    EstadoReceta.confeccionada,
                    EstadoReceta.proceso,
                    EstadoReceta.lista,
                    EstadoReceta.entregada
            };

            int[] contadorEstados = {0, 0, 0, 0};
            int totalRecetas = 60;

            for (int i = 0; i < totalRecetas; i++) {
                Receta receta = new Receta();

                // Paciente aleatorio (IDs 14-23)
                int idPaciente = 14 + (i % 10);
                Paciente paciente = pacienteService.findById(idPaciente);
                receta.setPaciente(paciente);

                // DISTRIBUCIÓN TEMPORAL MEJORADA
                // Distribuir recetas en los últimos 12 meses de manera uniforme
                int mesAtras = random.nextInt(12); // 0-11 meses atrás
                int diaDelMes = random.nextInt(28) + 1; // 1-28 (evita problemas con febrero)

                LocalDate fechaConfeccion = hoy.minusMonths(mesAtras).withDayOfMonth(diaDelMes);
                receta.setFechaConfeccion(fechaConfeccion);
                receta.setFechaRetiro(fechaConfeccion.plusDays(7));

                // Asignar estado de manera uniforme
                EstadoReceta estado = estados[i % 4];
                receta.setEstado(estado);
                contadorEstados[i % 4]++;

                // AGREGAR MEDICAMENTOS CON VARIEDAD
                // Cada receta tendrá entre 1 y 4 medicamentos
                int cantidadMeds = random.nextInt(4) + 1;

                // Seleccionar medicamentos aleatorios sin repetir
                Set<Integer> medicamentosUsados = new java.util.HashSet<>();

                for (int j = 0; j < cantidadMeds; j++) {
                    int indiceMed;
                    do {
                        indiceMed = random.nextInt(30); // 0-29
                    } while (medicamentosUsados.contains(indiceMed));

                    medicamentosUsados.add(indiceMed);
                    String codigoMed = String.format("M%03d", indiceMed + 1);

                    Medicamento med = medicamentoService.findByCodigo(codigoMed);

                    if (med != null) {
                        // Cantidad variable: 1-5 unidades
                        int cantidad = random.nextInt(5) + 1;

                        // Duración variable: 7, 14, 21 o 30 días
                        int[] duraciones = {7, 14, 21, 30};
                        int duracion = duraciones[random.nextInt(duraciones.length)];

                        MedicamentoPrescrito mp = new MedicamentoPrescrito(
                                codigoMed,
                                cantidad,
                                duracion,
                                generarIndicaciones(med.getNombre())
                        );
                        receta.addMedicamento(mp);
                    }
                }

                if (recetaService.createReceta(receta)) {
                    System.out.println("  ✓ Receta #" + (i+1) +
                            " | " + fechaConfeccion +
                            " | Paciente" + (idPaciente-13) +
                            " | " + cantidadMeds + " meds" +
                            " | Estado: " + estado);
                }
            }

        }

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
            System.out.println("\nApagando servidores...");
            requestServer.stop();
            broadcaster.stop();
        }));

        requestServer.start();
        broadcaster.start();

        System.out.println("-----------------------------------");
        System.out.println("    SERVIDORES INICIADOS ");
        System.out.println("-----------------------------------");
        System.out.println("  Requests:   localhost:" + requestPort);
        System.out.println("  Broadcast:  localhost:" + messagePort);
        System.out.println("-----------------------------------\n");
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