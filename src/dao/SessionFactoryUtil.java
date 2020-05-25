package dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
/**
 * Clase para obtener la sesion en la base de datos
 * 
 * @author Albert Araque, Francisco José Ruiz
 *
 */
public class SessionFactoryUtil {
	private static final SessionFactory sessionFactory;

	static {
		try {
			// Crea el SessionFactory de hibernate.cfg.xml
			sessionFactory = new Configuration().configure().buildSessionFactory();

		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}