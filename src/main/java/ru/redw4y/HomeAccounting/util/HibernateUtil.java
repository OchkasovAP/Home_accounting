package ru.redw4y.HomeAccounting.util;

import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import ru.redw4y.HomeAccounting.model.User;

@Component
public class HibernateUtil {
	private static SessionFactory sessionFactory;
	static {
		Configuration config = new Configuration();
		config.configure();
		try {
			sessionFactory = config.buildSessionFactory();
			sessionFactory.openSession();
		} catch (Exception e) {
			System.err.println("--------------Error in build session factory---------------");
			System.err.println(e);
			System.err.println("-----------------------------------------------------------");
			StandardServiceRegistryBuilder.destroy(config.getStandardServiceRegistryBuilder()
										.getBootstrapServiceRegistry());
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void close() {
		getSessionFactory().close();
	}

	public static Session getCurrentSession() {
		Session session = sessionFactory.getCurrentSession();
		return session;
	}

}
