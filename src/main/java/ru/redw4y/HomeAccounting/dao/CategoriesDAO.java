package ru.redw4y.HomeAccounting.dao;

import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import ru.redw4y.HomeAccounting.entityUtil.Category;
import ru.redw4y.HomeAccounting.entityUtil.OperationType;
import ru.redw4y.HomeAccounting.models.User;


@Component
public class CategoriesDAO {
	@Autowired
	private EntityManager entityManager;
	@Transactional
	public void editCategory(String categoryName, OperationType categoryType, int categoryID) {
		Session session = entityManager.unwrap(Session.class);
		Category category = session.find(categoryType.getCategoryClass(), categoryID);
		category.setName(categoryName);
	}
	@Transactional
	public void addCategory(int userID, Category category) {
		Session session = entityManager.unwrap(Session.class);
		User user = session.find(User.class, userID);
		user.addCategory(category);
	}
	@Transactional
	public void removeCategory(int userID, int categoryID, OperationType type) {
		Session session = entityManager.unwrap(Session.class);
		User user = session.find(User.class, userID);
		Category category = session.find(type.getCategoryClass(), categoryID);
		user.removeCategory(category);
	}

	@Transactional(readOnly = true)
	public List<? extends Category> getCategoryList(int userID, OperationType type) {
		Session session = entityManager.unwrap(Session.class);
		User user = session.find(User.class, userID);
		List<? extends Category> categories = user.getCategories(type);
		Collections.sort(categories);
		return categories;
	}

	@Transactional(readOnly = true)
	public Category getCategory(int id, Class<? extends Category> itemClass) {
		Session session = entityManager.unwrap(Session.class);
		return session.find(itemClass, id);
	}

}
