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

    public Usuario loginByNombre(String nombre, String clave) {
        Session session = null;
        try {
            session = sessionFactory.openSession();

            Usuario u = session.createQuery(
                            "FROM Usuario WHERE nombre = :nombre", Usuario.class)
                    .setParameter("nombre", nombre)
                    .uniqueResult();

            if (u == null) {
                System.out.println("[AuthService] Usuario no encontrado: " + nombre);
                return null;
            }

            if (u.getSalt() == null || u.getClaveHash() == null) {
                System.out.println("[AuthService] Usuario sin clave configurada");
                return null;
            }

            String claveHasheada = hash(clave, u.getSalt());
            boolean claveCorrecta = claveHasheada.equals(u.getClaveHash());

            return claveCorrecta ? u : null;

        } catch (Exception e) {
            System.err.println("[AuthService] Error en login: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Usuario login(int id, String clave) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Usuario u = session.find(Usuario.class, id);

            if (u == null) {
                System.out.println("[AuthService] Usuario no encontrado con ID: " + id);
                return null;
            }

            if (u.getSalt() == null || u.getClaveHash() == null) {
                System.out.println("[AuthService] Usuario sin clave configurada");
                return null;
            }

            String claveHasheada = hash(clave, u.getSalt());
            boolean claveCorrecta = claveHasheada.equals(u.getClaveHash());

            System.out.println("[AuthService] Login attempt for ID " + id + ": " + (claveCorrecta ? "SUCCESS" : "FAILED"));

            return claveCorrecta ? u : null;

        } catch (Exception e) {
            System.err.println("[AuthService] Error en login: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Cambiar la contraseña de un usuario
     */
    public boolean cambiarClave(int userId, String nuevaClave) {
        if (nuevaClave == null || nuevaClave.isEmpty()) {
            System.err.println("[AuthService] Nueva clave nula o vacía");
            return false;
        }

        String salt = genSalt();
        String claveHash = hash(nuevaClave, salt);

        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            Usuario usuario = session.find(Usuario.class, userId);

            if (usuario == null) {
                System.err.println("[AuthService] No se encontró usuario con ID: " + userId);
                return false;
            }

            usuario.setSalt(salt);
            usuario.setClaveHash(claveHash);
            session.merge(usuario);

            tx.commit();

            System.out.println("[AuthService] Contraseña cambiada correctamente para usuario ID: " + userId);
            return true;

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception ex) {
                    System.err.println("[AuthService] Error en rollback: " + ex.getMessage());
                }
            }
            System.err.println("[AuthService] Error al cambiar clave: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Asigna una clave hasheada a un usuario
     * Este método debe llamarse DESPUÉS de guardar el usuario en la BD
     */
    public void asignarClaveHasheada(Usuario u, String clave) {
        if (u == null || clave == null || clave.isEmpty()) {
            System.err.println("[AuthService] Usuario o clave nula/vacía");
            return;
        }

        String salt = genSalt();
        String claveHash = hash(clave, salt);

        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            Usuario usuarioEnBD = session.find(Usuario.class, u.getId());

            if (usuarioEnBD != null) {
                usuarioEnBD.setSalt(salt);
                usuarioEnBD.setClaveHash(claveHash);
            } else {
                System.err.println("[AuthService] No se encontró usuario con ID: " + u.getId());
                if (tx != null) tx.rollback();
                return;
            }

            tx.commit();

            u.setSalt(salt);
            u.setClaveHash(claveHash);

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
            var spec = new PBEKeySpec(
                    pass.toCharArray(),
                    Base64.getDecoder().decode(salt),
                    ITERATIONS,
                    KEY_LENGTH
            );
            var factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return Base64.getEncoder().encodeToString(factory.generateSecret(spec).getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Error al hashear clave", e);
        }
    }
}