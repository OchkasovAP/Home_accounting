package ru.redw4y.HomeAccounting.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import ru.redw4y.HomeAccounting.entity.Role;
import ru.redw4y.HomeAccounting.entity.User;
import ru.redw4y.HomeAccounting.exceptions.UserException;
import ru.redw4y.HomeAccounting.util.Actioner;

@Component
public class UserDAO extends AbstractDAO {
	public User checkUser(User user) {
		Session session = getCurrentSession();
		Transaction transaction = session.beginTransaction();
		Query<User> query = session.createQuery("SELECT c FROM User c WHERE c.login = :login", User.class);
		query.setParameter("login", user.getLogin());
		User checkingUser = query.getSingleResultOrNull();
		transaction.commit();
		if (checkingUser == null)
			throw new UserException("This user doesn't exist");
		if (checkingUser.getPassword().equals(user.getPassword()))
			return checkingUser;
		else
			throw new UserException("Wrong password");
	}

	public void addNewUser(User user) {
		executeTransaction(new Actioner() {
			@Override
			public void action() throws Exception {
				Session session = getCurrentSession();
				user.setRole(
						session.createQuery("SELECT c FROM Role c WHERE name = 'USER'", Role.class).getSingleResult());
				session.persist(user);
			}
		});
	}

	public void editUser(int id, User newUser) {
		executeTransaction(new Actioner() {
			@Override
			public void action() throws Exception {
				Session session = getCurrentSession();
				User editableUser = session.find(User.class, id);
				editableUser.setLogin(newUser.getLogin());
				editableUser.setPassword(newUser.getPassword());
				editableUser.setRole(newUser.getRole());
			}
		});
	}

	public void deleteUser(int id) {
		deleteItem(id, User.class);
	}

	public List<User> getUserList() {
		Session session = getCurrentSession();
		Transaction transaction = session.beginTransaction();
		Query<User> query = session.createQuery("SELECT c FROM User c", User.class);
		List<User> userList = query.getResultList();
		transaction.commit();
		return userList;
	}

	public User getUser(int id) {
		return getItem(id, User.class);
	}
	

}
