package hospital.example.DataAccess.services;

import hospital.example.Domain.models.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AuthService {

    private final SessionFactory sessionFactory;
    private static final int SALT_LENGTH = 16, ITERATIONS = 65536, KEY_LENGTH = 256;

    public AuthService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Usuario login(int id, String clave) {
        try (Session session = sessionFactory.openSession()) {
            Usuario u = session.find(Usuario.class, id);
            if (u == null || u.getSalt() == null || u.getClaveHash() == null) return null;
            
            boolean authenticated = hash(clave, u.getSalt()).equals(u.getClaveHash());
            if (authenticated) {
                // Eagerly initialize rol before session closes to avoid LazyInitializationException
                u.getRol(); // Force initialization
                return u;
            }
            return null;
        }
    }

    public void asignarClaveHasheada(Usuario u, String clave) {
        String salt = genSalt();
        u.setSalt(salt);
        u.setClaveHash(hash(clave, salt));

        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            // Usar merge en lugar de persist si el objeto ya existe
            session.merge(u);

            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception ex) {
                    System.err.println("[AuthService] Error en rollback: " + ex.getMessage());
                }
            }
            System.err.println("[AuthService] Error al asignar clave: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    private String genSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String hash(String pass, String salt) {
        try {
            var spec = new PBEKeySpec(pass.toCharArray(), Base64.getDecoder().decode(salt), ITERATIONS, KEY_LENGTH);
            var factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return Base64.getEncoder().encodeToString(factory.generateSecret(spec).getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Error al hashear clave", e);
        }
    }
}