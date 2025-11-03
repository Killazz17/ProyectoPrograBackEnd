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
            e.printStackTrace();
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

    // ✅ MÉTODO CORREGIDO: Obtener todas las recetas con medicamentos
    public List<Receta> findAllWithMedicamentos() {
        Session session = null;
        try {
            session = sessionFactory.openSession();

            // ✅ Query con JOIN FETCH para cargar medicamentos y paciente en UNA sola consulta
            List<Receta> recetas = session.createQuery(
                    "SELECT DISTINCT r FROM Receta r " +
                            "LEFT JOIN FETCH r.medicamentos " +
                            "LEFT JOIN FETCH r.paciente",
                    Receta.class
            ).getResultList();

            System.out.println("[RecetaService] ✓ Cargadas " + recetas.size() + " recetas con medicamentos");

            // Verificar que los medicamentos se cargaron
            for (Receta r : recetas) {
                System.out.println("[RecetaService]   Receta #" + r.getId() + " tiene " +
                        r.getMedicamentos().size() + " medicamentos");
            }

            return recetas;

        } catch (Exception e) {
            System.err.println("[RecetaService] ❌ Error al obtener recetas con medicamentos: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // ✅ Retornar lista vacía en lugar de null
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}