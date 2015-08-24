package main.java.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


/**
 * Created by olga on 20.08.15.
 */
public class HibernateUtil {

	private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

	private HibernateUtil(){}

	private static SessionFactory buildSessionFactory() {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			return new Configuration().configure().buildSessionFactory();
		}
		catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return SESSION_FACTORY;
	}

	public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
	}

}
