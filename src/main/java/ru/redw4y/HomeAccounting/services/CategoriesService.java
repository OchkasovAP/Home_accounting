package ru.redw4y.HomeAccounting.services;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import ru.redw4y.HomeAccounting.entityUtil.Category;
import ru.redw4y.HomeAccounting.entityUtil.OperationType;
import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class CategoriesService {
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private EntityManager entityManager;
	
	@Transactional
	public void edit(String categoryName, OperationType categoryType, int categoryID) {
		Category category = entityManager.find(categoryType.getCategoryClass(), categoryID);
		category.setName(categoryName);
	}
	@Transactional
	public void create(int userID, Category category) {
		User user = entityManager.find(User.class, userID);
		user.addCategory(category);
	}
	@Transactional
	public void remove(int userID, int categoryID, OperationType type) {
		User user = userRepo.findById(userID).get();
		Category category = entityManager.find(type.getCategoryClass(), categoryID);
		user.removeCategory(category);
	}

	public List<? extends Category> findAllByUser(int userID, OperationType type) {
		User user = userRepo.findById(userID).get();
		return user.getCategories(type)
				.stream()
				.sorted()
				.toList();
	}

	public Category findById(int id, Class<? extends Category> itemClass) {
		return entityManager.find(itemClass, id);
	}
	

	
}
