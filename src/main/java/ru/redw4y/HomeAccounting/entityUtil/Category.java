package ru.redw4y.HomeAccounting.entityUtil;

import java.util.List;

import ru.redw4y.HomeAccounting.entity.User;

public interface Category extends Comparable<Category> {
	void setName(String name);

	void setUser(User user);

	Integer getId();

	String getName();

	OperationType getType();

	<T extends Operation> List<T> getOperations();
}
