package ru.redw4y.HomeAccounting.util;

import java.util.List;

import ru.redw4y.HomeAccounting.models.User;

public interface Category {
	String getName();
	void setName(String name);
	
	User getUser();
	void setUser(User user);

	Integer getId();
	void setId(Integer id);

	OperationType getType();
	<T extends Operation> List<T> getOperations();
	
}
