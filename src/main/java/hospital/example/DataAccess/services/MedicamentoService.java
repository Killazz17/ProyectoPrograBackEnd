package hospital.example.DataAccess.services;

import hospital.example.Domain.models.Medicamento;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class MedicamentoService {

    private final SessionFactory sessionFactory;

    public MedicamentoService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Medicamento> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Medicamento", Medicamento.class).list();
        } catch (Exception e) {
            System.err.println("[MedicamentoService] Error al obtener medicamentos: " + e.getMessage());
            return null;
        }
    }

    public boolean save(Medicamento medicamento) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(medicamento);
            tx.commit();
            System.out.println("[MedicamentoService] ✓ Medicamento guardado: " + medicamento.getNombre());
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[MedicamentoService] Error al guardar medicamento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Medicamento findByCodigo(String codigo) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Medicamento.class, codigo);
        } catch (Exception e) {
            System.err.println("[MedicamentoService] Error al buscar medicamento: " + e.getMessage());
            return null;
        }
    }

    public boolean delete(String codigo) {
        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            Medicamento medicamento = session.find(Medicamento.class, codigo);

            if (medicamento == null) {
                System.err.println("[MedicamentoService] Medicamento no encontrado con código: " + codigo);
                return false;
            }

            System.out.println("[MedicamentoService] Eliminando medicamento: " + medicamento.getNombre());

            // Verificar si está siendo usado en recetas
            Long countUsos = session.createQuery(
                            "SELECT COUNT(mp) FROM MedicamentoPrescrito mp WHERE mp.medicamentoCodigo = :codigo",
                            Long.class)
                    .setParameter("codigo", codigo)
                    .uniqueResult();

            if (countUsos != null && countUsos > 0) {
                System.err.println("[MedicamentoService] No se puede eliminar el medicamento");
                System.err.println("[MedicamentoService] Está siendo usado en " + countUsos + " prescripciones");
                return false;
            }

            session.remove(medicamento);
            tx.commit();
            System.out.println("[MedicamentoService] Medicamento eliminado exitosamente");
            return true;

        } catch (org.hibernate.exception.ConstraintViolationException e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception ex) {
                    System.err.println("[MedicamentoService] Error en rollback: " + ex.getMessage());
                }
            }
            System.err.println("[MedicamentoService] No se puede eliminar el medicamento");
            System.err.println("[MedicamentoService] Está siendo usado en recetas existentes");
            System.err.println("[MedicamentoService] Debes eliminar primero las recetas que lo usan");
            e.printStackTrace();
            return false;

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception ex) {
                    System.err.println("[MedicamentoService] Error en rollback: " + ex.getMessage());
                }
            }
            System.err.println("[MedicamentoService] Error al eliminar medicamento: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}