package hospital.example.DataAccess.services;

import hospital.example.Domain.models.Medicamento;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class MedicamentoService {

    private final SessionFactory sessionFactory;

    // ✅ Constructor para compatibilidad con Main.java
    public MedicamentoService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // ------------------------
    // Obtener todos los medicamentos
    // ------------------------
    public List<Medicamento> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Medicamento", Medicamento.class).list();
        } catch (Exception e) {
            System.err.println("[MedicamentoService] Error al obtener medicamentos: " + e.getMessage());
            return null;
        }
    }

    // ------------------------
    // Guardar un nuevo medicamento
    // ------------------------
    public boolean save(Medicamento medicamento) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(medicamento);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[MedicamentoService] Error al guardar medicamento: " + e.getMessage());
            return false;
        }
    }

    // ------------------------
    // Buscar medicamento por código
    // ------------------------
    public Medicamento findByCodigo(String codigo) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Medicamento.class, codigo);
        } catch (Exception e) {
            System.err.println("[MedicamentoService] Error al buscar medicamento: " + e.getMessage());
            return null;
        }
    }

    // ------------------------
    // Eliminar medicamento por código
    // ------------------------
    public boolean delete(String codigo) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Medicamento entity = session.find(Medicamento.class, codigo);
            if (entity != null) {
                session.remove(entity);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[MedicamentoService] Error al eliminar medicamento: " + e.getMessage());
            return false;
        }
    }
}