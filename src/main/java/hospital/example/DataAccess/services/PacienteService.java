package hospital.example.DataAccess.services;

import hospital.example.DataAccess.HibernateUtil;
import hospital.example.Domain.models.Paciente;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class PacienteService {

    private final SessionFactory sessionFactory;

    public PacienteService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Paciente> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Paciente", Paciente.class).list();
        } catch (Exception e) {
            System.err.println("[PacienteService] Error al obtener pacientes: " + e.getMessage());
            return null;
        }
    }

    public boolean save(Paciente paciente) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.persist(paciente);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[PacienteService] Error al guardar paciente: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    public Paciente findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Paciente.class, id);
        } catch (Exception e) {
            System.err.println("[PacienteService] Error al buscar paciente por ID: " + e.getMessage());
            return null;
        }
    }

    public boolean delete(int id) {
        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            // Buscar el paciente
            Paciente paciente = session.find(Paciente.class, id);

            if (paciente == null) {
                System.err.println("[PacienteService] Paciente no encontrado con ID: " + id);
                return false;
            }

            System.out.println("[PacienteService] Eliminando paciente: " + paciente.getNombre());

            session.remove(paciente);

            tx.commit();
            System.out.println("[PacienteService] Paciente eliminado exitosamente");
            return true;

        } catch (org.hibernate.exception.ConstraintViolationException e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception ex) {
                    System.err.println("[PacienteService] Error en rollback: " + ex.getMessage());
                }
            }
            System.err.println("[PacienteService] No se puede eliminar el paciente porque tiene recetas asociadas");
            System.err.println("[PacienteService] Solución: Ejecuta el script SQL 'fix_foreign_keys.sql' en tu base de datos");
            e.printStackTrace();
            return false;

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception ex) {
                    System.err.println("[PacienteService] Error en rollback: " + ex.getMessage());
                }
            }
            System.err.println("[PacienteService] Error al eliminar paciente: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}