package hospital.example.DataAccess.services;

import hospital.example.DataAccess.HibernateUtil;
import hospital.example.Domain.models.Admin;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class AdminService {

    private final SessionFactory sessionFactory;

    public AdminService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Admin findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Admin.class, id);
        } catch (Exception e) {
            System.err.println("[AdminService] Error al buscar admin por ID: " + e.getMessage());
            return null;
        }
    }

    public List<Admin> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Admin", Admin.class).list();
        } catch (Exception e) {
            System.err.println("[AdminService] Error al obtener admins: " + e.getMessage());
            return null;
        }
    }

    public boolean save(Admin admin) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.persist(admin);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && session != null && session.isOpen()) {
                try {
                    tx.rollback();
                } catch (Exception ex) {
                    System.err.println("[AdminService] Error al hacer rollback: " + ex.getMessage());
                }
            }
            System.err.println("[AdminService] Error al guardar admin: " + e.getMessage());
            return false;
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    public boolean update(Admin admin) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(admin);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[AdminService] Error al actualizar admin: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            Admin admin = session.find(Admin.class, id);
            if (admin == null) return false;

            tx = session.beginTransaction();
            session.remove(admin);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[AdminService] Error al eliminar admin: " + e.getMessage());
            return false;
        }
    }
}