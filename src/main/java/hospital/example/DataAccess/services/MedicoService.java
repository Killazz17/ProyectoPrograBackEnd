package hospital.example.DataAccess.services;

import hospital.example.DataAccess.HibernateUtil;
import hospital.example.Domain.models.Medico;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class MedicoService {

    private final SessionFactory sessionFactory;

    public MedicoService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // ------------------------
    // Obtener todos los médicos
    // ------------------------
    public List<Medico> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Medico", Medico.class).list();
        } catch (Exception e) {
            System.err.println("[MedicoService] Error al obtener médicos: " + e.getMessage());
            return null;
        }
    }

    // ------------------------
    // Guardar un nuevo médico
    // ------------------------

    public boolean save(Medico medico) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.persist(medico);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[MedicoService] Error al guardar admin: " + e.getMessage());
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    // ------------------------
    // Buscar médico por ID
    // ------------------------
    public Medico findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Medico.class, id);
        } catch (Exception e) {
            System.err.println("[MedicoService] Error al buscar médico por ID: " + e.getMessage());
            return null;
        }
    }

    // ------------------------
    // Eliminar médico por ID
    // ------------------------
    public boolean delete(int id) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Medico entity = session.find(Medico.class, id);
            if (entity != null) {
                session.remove(entity);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[MedicoService] Error al eliminar médico: " + e.getMessage());
            return false;
        }
    }
}