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
            System.err.println("[PacienteService] Error al guardar admin: " + e.getMessage());
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
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Paciente entity = session.find(Paciente.class, id);
            if (entity != null) {
                session.remove(entity);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[PacienteService] Error al eliminar paciente: " + e.getMessage());
            return false;
        }
    }
}