package hospital.example.DataAccess.services;

import hospital.example.Domain.models.Receta;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class RecetaService {

    private final SessionFactory sessionFactory;

    public RecetaService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // ------------------------
    // Crear nueva receta
    // ------------------------
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
            return false;
        }
    }

    // ------------------------
    // Obtener todas las recetas
    // ------------------------
    public List<Receta> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Receta", Receta.class).list();
        } catch (Exception e) {
            System.err.println("[RecetaService] Error al obtener recetas: " + e.getMessage());
            return null;
        }
    }

    // ------------------------
    // Buscar una receta por ID
    // ------------------------
    public Receta findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Receta.class, id);
        } catch (Exception e) {
            System.err.println("[RecetaService] Error al buscar receta por ID: " + e.getMessage());
            return null;
        }
    }

    public List<Receta> findAllWithMedicamentos() {
        try (Session session = sessionFactory.openSession()) {
            // ✅ USAR JOIN FETCH para cargar medicamentos en una sola query
            return session.createQuery(
                    "SELECT DISTINCT r FROM Receta r " +
                            "LEFT JOIN FETCH r.medicamentos " +
                            "LEFT JOIN FETCH r.paciente",
                    Receta.class
            ).list();
        } catch (Exception e) {
            System.err.println("[RecetaService] Error al obtener recetas con medicamentos: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}