package ru.redw4y.HomeAccounting.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import ru.redw4y.HomeAccounting.exceptions.UserException;
import ru.redw4y.HomeAccounting.models.CashAccount;
import ru.redw4y.HomeAccounting.models.Income;
import ru.redw4y.HomeAccounting.models.IncomeCategory;
import ru.redw4y.HomeAccounting.models.Outcome;
import ru.redw4y.HomeAccounting.models.OutcomeCategory;
import ru.redw4y.HomeAccounting.models.Role;
import ru.redw4y.HomeAccounting.models.User;

@Component
public class UserDAO {
	@Autowired
	private EntityManager entityManager;

	@Transactional(readOnly = true)
	public User checkUser(User user) {
		Session session = entityManager.unwrap(Session.class);
		Query<User> query = session.createQuery("SELECT c FROM User c WHERE c.login = :login", User.class);
		query.setParameter("login", user.getLogin());
		User checkingUser = query.getSingleResult();
		if (checkingUser == null)
			throw new UserException("This user doesn't exist");
		if (checkingUser.getPassword().equals(user.getPassword()))
			return checkingUser;
		else
			throw new UserException("Wrong password");
	}

	@Transactional
	public void addNewUser(User user) {
		Session session = entityManager.unwrap(Session.class);
		user.setRole(session.createQuery("SELECT c FROM Role c WHERE name = 'USER'", Role.class).getSingleResult());
		session.persist(user);
	}

	@Transactional
	public void editUser(int id, User newUser) {
		Session session = entityManager.unwrap(Session.class);
		User editableUser = session.find(User.class, id);
		editableUser.setLogin(newUser.getLogin());
		editableUser.setPassword(newUser.getPassword());
		editableUser.setRole(newUser.getRole());
	}

	@Transactional
	public void deleteUser(int id) {
		Session session = entityManager.unwrap(Session.class);
		User user = session.find(User.class, id);
		session.remove(user);
	}

	@Transactional(readOnly = true)
	public List<User> getUserList() {
		Session session = entityManager.unwrap(Session.class);
		return session.createQuery("SELECT c FROM User c", User.class).getResultList();
	}

	@Transactional(readOnly = true)
	public User getUser(int id) {
		Session session = entityManager.unwrap(Session.class);
		return session.get(User.class, id);
	}
	@Transactional
	public User getFullUser(int id) {
		Session session = entityManager.unwrap(Session.class);
		User user = session.find(User.class, id);
		Hibernate.initialize(user);
		user.getCashAccounts().isEmpty(); //Почему-то без вызова здесь методов с этими коллекциями, передает юзера с пустыми коллекциями
		user.getIncomeCategories().isEmpty();
		user.getOutcomeCategories().isEmpty();
		user.getIncomes().isEmpty();
		user.getOutcomes().isEmpty();
		return user;
	}

}
