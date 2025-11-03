package hospital.example.DataAccess.services;

import hospital.example.Domain.models.Receta;
import hospital.example.Utilities.EstadoReceta;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class RecetaService {

    private final SessionFactory sessionFactory;

    public RecetaService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // ========== MÉTODOS EXISTENTES ==========

    public boolean createReceta(Receta receta) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(receta);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[RecetaService] Error al crear receta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Receta> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Receta", Receta.class).list();
        } catch (Exception e) {
            System.err.println("[RecetaService] Error al obtener recetas: " + e.getMessage());
            return null;
        }
    }

    public Receta findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Receta.class, id);
        } catch (Exception e) {
            System.err.println("[RecetaService] Error al buscar receta por ID: " + e.getMessage());
            return null;
        }
    }

    public List<Receta> findAllWithMedicamentos() {
        Session session = null;
        try {
            session = sessionFactory.openSession();

            List<Receta> recetas = session.createQuery(
                    "SELECT DISTINCT r FROM Receta r " +
                            "LEFT JOIN FETCH r.medicamentos " +
                            "LEFT JOIN FETCH r.paciente",
                    Receta.class
            ).getResultList();

            System.out.println("[RecetaService] ✓ Cargadas " + recetas.size() + " recetas con medicamentos");

            for (Receta r : recetas) {
                System.out.println("[RecetaService]   Receta #" + r.getId() + " tiene " +
                        r.getMedicamentos().size() + " medicamentos");
            }

            System.out.println("=== [RecetaService] RECETAS CARGADAS ===");
            for (Receta r : recetas) {
                System.out.println("  Receta ID: " + r.getId() +
                        " | Paciente: " + (r.getPaciente() != null ?
                        "ID=" + r.getPaciente().getId() + ", Nombre=" + r.getPaciente().getNombre() :
                        "NULL"));
            }
            return recetas;

        } catch (Exception e) {
            System.err.println("[RecetaService] ❌ Error al obtener recetas con medicamentos: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    // ========== NUEVOS MÉTODOS PARA DESPACHO ==========

    /**
     * Buscar una receta por ID con sus medicamentos cargados
     */
    public Receta findByIdWithMedicamentos(int id) {
        Session session = null;
        try {
            session = sessionFactory.openSession();

            Receta receta = session.createQuery(
                            "SELECT DISTINCT r FROM Receta r " +
                                    "LEFT JOIN FETCH r.medicamentos " +
                                    "LEFT JOIN FETCH r.paciente " +
                                    "WHERE r.id = :id",
                            Receta.class
                    ).setParameter("id", id)
                    .uniqueResult();

            if (receta != null) {
                System.out.println("[RecetaService] ✓ Receta #" + id + " encontrada con " +
                        receta.getMedicamentos().size() + " medicamentos");
            } else {
                System.out.println("[RecetaService] ⚠️ Receta #" + id + " no encontrada");
            }

            return receta;

        } catch (Exception e) {
            System.err.println("[RecetaService] Error al buscar receta por ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Actualizar el estado de una receta
     */
    public boolean updateEstado(int idReceta, EstadoReceta nuevoEstado) {
        Transaction tx = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            Receta receta = session.find(Receta.class, idReceta);

            if (receta == null) {
                System.err.println("[RecetaService] Receta no encontrada: " + idReceta);
                return false;
            }

            EstadoReceta estadoAnterior = receta.getEstado();
            receta.setEstado(nuevoEstado);
            session.merge(receta);

            tx.commit();

            System.out.println("[RecetaService] ✓ Receta #" + idReceta + " actualizada: " +
                    estadoAnterior + " → " + nuevoEstado);

            return true;

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception ex) {
                    System.err.println("[RecetaService] Error en rollback: " + ex.getMessage());
                }
            }
            System.err.println("[RecetaService] Error al actualizar estado: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Obtener recetas por paciente
     */
    public List<Receta> findByPaciente(int pacienteId) {
        Session session = null;
        try {
            session = sessionFactory.openSession();

            List<Receta> recetas = session.createQuery(
                            "SELECT DISTINCT r FROM Receta r " +
                                    "LEFT JOIN FETCH r.medicamentos " +
                                    "LEFT JOIN FETCH r.paciente " +
                                    "WHERE r.paciente.id = :pacienteId",
                            Receta.class
                    ).setParameter("pacienteId", pacienteId)
                    .getResultList();

            System.out.println("[RecetaService] ✓ Encontradas " + recetas.size() +
                    " recetas para paciente #" + pacienteId);

            return recetas;

        } catch (Exception e) {
            System.err.println("[RecetaService] Error al buscar recetas por paciente: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Obtener recetas por estado
     */
    public List<Receta> findByEstado(EstadoReceta estado) {
        Session session = null;
        try {
            session = sessionFactory.openSession();

            List<Receta> recetas = session.createQuery(
                            "SELECT DISTINCT r FROM Receta r " +
                                    "LEFT JOIN FETCH r.medicamentos " +
                                    "LEFT JOIN FETCH r.paciente " +
                                    "WHERE r.estado = :estado",
                            Receta.class
                    ).setParameter("estado", estado)
                    .getResultList();

            System.out.println("[RecetaService] ✓ Encontradas " + recetas.size() +
                    " recetas con estado " + estado);

            return recetas;

        } catch (Exception e) {
            System.err.println("[RecetaService] Error al buscar recetas por estado: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public List<Receta> findByPacienteId(int pacienteId) {
        try (Session session = sessionFactory.openSession()) {
            List<Receta> recetas = session.createQuery(
                            "SELECT DISTINCT r FROM Receta r " +
                                    "LEFT JOIN FETCH r.medicamentos " +
                                    "WHERE r.paciente.id = :pacienteId",
                            Receta.class
                    ).setParameter("pacienteId", pacienteId)
                    .getResultList();

            System.out.println("[RecetaService] Búsqueda por paciente ID " + pacienteId +
                    " → " + recetas.size() + " recetas encontradas");

            return recetas;
        } catch (Exception e) {
            System.err.println("[RecetaService] Error en findByPacienteId: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}