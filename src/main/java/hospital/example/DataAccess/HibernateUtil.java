package hospital.example.DataAccess;


import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.io.IOException;
import java.util.Properties;


public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            Properties properties = new Properties();
            properties.load(HibernateUtil.class.getClassLoader().getResourceAsStream("hibernate.properties"));

            sessionFactory = new Configuration()
                    .setProperties(properties)

                    // Agregar aqui otras entidades.
                    .buildSessionFactory();

        } catch (IOException e) {
            throw new ExceptionInInitializerError("Could not load hibernate.properties: " + e.getMessage());
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed: " + ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
