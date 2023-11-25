package ru.redw4y.HomeAccounting.util;

import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import ru.redw4y.HomeAccounting.entity.User;

@Component
public class HibernateUtil {
	private static SessionFactory sessionFactory;
	static {
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure() // configures settings
																									// from
																									// hibernate.cfg.xml
				.build();
		try {
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
			sessionFactory.openSession();
		} catch (Exception e) {
			System.out.println("Exception in create SessionFactory------------------------------------");
			System.out.println(e);
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void close() {
		getSessionFactory().close();
	}

	public Session openSession() {
		return sessionFactory.openSession();
	}
	public Session getCurrentSession() {
		Session session = sessionFactory.getCurrentSession();
		return session;
	}

	public User findUser(int id) {
		Session session = openSession();
		User user = session.find(User.class, id);
		session.close();
		return user;
	}

	public void updateUser(HttpSession session) {
		Integer userId = ((User) session.getAttribute("user")).getId();
		session.setAttribute("user", findUser(userId));
	}

	public void executeTransaction(Session session, Model model, Actioner actioner) {
		Transaction transaction = session.getTransaction();
		transaction.begin();
		try {
			actioner.action();
			transaction.commit();
		} catch (Exception ex) {
			model.addAttribute("error", ex.getMessage());
			transaction.rollback();
		} finally {
			session.close();
		}
	}
}
