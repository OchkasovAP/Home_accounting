package ru.redw4y.HomeAccounting.util;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import ru.redw4y.HomeAccounting.models.Income;
import ru.redw4y.HomeAccounting.models.IncomeCategory;
import ru.redw4y.HomeAccounting.models.Outcome;
import ru.redw4y.HomeAccounting.models.OutcomeCategory;
import ru.redw4y.HomeAccounting.models.User;

public enum OperationType {
	INCOME(Income.class, IncomeCategory.class), OUTCOME(Outcome.class, OutcomeCategory.class);

	private final Class<? extends Operation> operationClass;
	private final Class<? extends Category> categoryClass;

	private OperationType(Class<? extends Operation> operationClass, Class<? extends Category> categoryClass) {
		this.operationClass = operationClass;
		this.categoryClass = categoryClass;
	}

	@SuppressWarnings("unchecked")
	public <T extends Operation> Class<T> getOperationClass() {
		return (Class<T>) operationClass;
	}

	@SuppressWarnings("unchecked")
	public <T extends Category> Class<T> getCategoryClass() {
		return (Class<T>) categoryClass;
	}

	public Category newCategory(String categoryName) {
		try {
			return categoryClass.getConstructor(categoryName.getClass()).newInstance(categoryName);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public Operation newEmptyOperation() {
		try {
			return operationClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
}
