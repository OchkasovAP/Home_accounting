package ru.redw4y.HomeAccounting.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import ru.redw4y.HomeAccounting.util.Actioner;
import ru.redw4y.HomeAccounting.util.HibernateUtil;

public abstract class AbstractDAO {
	@Autowired
	private HibernateUtil daoUtil;
	
	protected Session getCurrentSession() {
		return daoUtil.getCurrentSession();
	}
	public void executeTransaction(Actioner actioner) {
		Transaction transaction = getCurrentSession().beginTransaction();
		try {
			actioner.action();
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			ex.printStackTrace();
		} 
	}
	protected <T> void deleteItem(int id, Class<T> itemClass) {
		executeTransaction(new Actioner() {	
			@Override
			public void action() throws Exception {
				Session session = getCurrentSession();
				T item = session.find(itemClass, id);
				session.remove(item);
			}
		});
	}
	public <T> T getItem(int id, Class<T> itemClass) {
		Session session = getCurrentSession();
		Transaction transaction = session.beginTransaction();
		T item = session.find(itemClass, id);
		transaction.commit();
		return item;
	}
}
