package ru.redw4y.HomeAccounting.dao;

import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.redw4y.HomeAccounting.entity.User;
import ru.redw4y.HomeAccounting.entityUtil.Category;
import ru.redw4y.HomeAccounting.entityUtil.OperationType;
import ru.redw4y.HomeAccounting.util.Actioner;

@Component
public class CategoriesDAO extends AbstractDAO {
	@Autowired
	private UserDAO userDAO;

	public void editCategory(String categoryName, OperationType categoryType, int categoryID) {
		executeTransaction(new Actioner() {
			@Override
			public void action() throws Exception {
				Category category = getCurrentSession().find(categoryType.getCategoryClass(), categoryID);
				category.setName(categoryName);
			}
		});
	}

	public void addCategory(int userID, Category category) {
		executeTransaction(new Actioner() {
			@Override
			public void action() throws Exception {
				User user = getCurrentSession().find(User.class, userID);
				user.addCategory(category);
			}
		});
	}

	public void removeCategory(int userID, int categoryID, OperationType type) {
		executeTransaction(new Actioner() {
			@Override
			public void action() throws Exception {
				Session session = getCurrentSession();
				User user = session.find(User.class, userID);
				Category category = session.find(type.getCategoryClass(), categoryID);
				user.removeCategory(category);
			}
		});
	}

	public List<? extends Category> getCategoryList(int userID, OperationType type) {
		User user = userDAO.getUser(userID);
		List<? extends Category> categories = user.getCategories(type);
		Collections.sort(categories);
		return categories;
	}

}
