package ru.redw4y.HomeAccounting.services;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import ru.redw4y.HomeAccounting.models.IncomeCategory;
import ru.redw4y.HomeAccounting.models.OutcomeCategory;
import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.repository.UserRepository;
import ru.redw4y.HomeAccounting.util.Category;
import ru.redw4y.HomeAccounting.util.OperationType;

@Service
@Transactional(readOnly = true)
public class CategoriesService {
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private EntityManager entityManager;
	
	public List<? extends Category> findAllByUser(int userID, OperationType type) {
		User user = userRepo.findById(userID).get();
		List<? extends Category> categories = Collections.emptyList();
		if(OperationType.INCOME.equals(type)) {
			categories = user.getIncomeCategories();
		} else if(OperationType.OUTCOME.equals(type)) {
			categories = user.getOutcomeCategories();
		}
		return categories.stream().sorted(Comparator.comparing(c -> c.getId())).toList();
	}

	public Category findById(int id, Class<? extends Category> itemClass) {
		return entityManager.find(itemClass, id);
	}
	
	@Transactional
	public void edit(String categoryName, OperationType categoryType, int categoryID) {
		Category category = entityManager.find(categoryType.getCategoryClass(), categoryID);
		category.setName(categoryName);
	}
	@Transactional
	public void create(int userID, Category category) {
		User user = userRepo.findById(userID).get();
		if (category instanceof IncomeCategory) {
			user.getIncomeCategories().add((IncomeCategory) category);
		} else if (category instanceof OutcomeCategory) {
			user.getOutcomeCategories().add((OutcomeCategory) category);
		}
		category.setUser(user);
	}
	@Transactional
	public void remove(int userID, int categoryID, OperationType type) {
		User user = userRepo.findById(userID).get();
		Category category = entityManager.find(type.getCategoryClass(), categoryID);
		if (OperationType.INCOME.equals(type)) {
			user.getIncomeCategories().remove((IncomeCategory)category);
		} else if (OperationType.OUTCOME.equals(type)) {
			user.getOutcomeCategories().remove((OutcomeCategory)category);
		}
		category.setUser(null);
	}

}
