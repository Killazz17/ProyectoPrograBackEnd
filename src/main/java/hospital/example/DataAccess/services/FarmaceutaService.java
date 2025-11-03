package hospital.example.DataAccess.services;

import hospital.example.Domain.models.Farmaceuta;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class FarmaceutaService {

    private final SessionFactory sessionFactory;

    public FarmaceutaService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Farmaceuta> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Farmaceuta", Farmaceuta.class).list();
        } catch (Exception e) {
            System.err.println("[FarmaceutaService] Error al obtener farmaceutas: " + e.getMessage());
            return null;
        }
    }

    public Farmaceuta findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Farmaceuta.class, id);
        } catch (Exception e) {
            System.err.println("[FarmaceutaService] Error al buscar farmaceuta: " + e.getMessage());
            return null;
        }
    }

    public boolean save(Farmaceuta farmaceuta) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(farmaceuta);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[FarmaceutaService] Error al guardar farmaceuta: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Farmaceuta entity = session.find(Farmaceuta.class, id);
            if (entity != null) {
                session.remove(entity);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[FarmaceutaService] Error al eliminar farmaceuta: " + e.getMessage());
            return false;
        }
    }
}