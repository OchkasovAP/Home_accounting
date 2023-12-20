package ru.redw4y.HomeAccounting.util;

import java.math.BigDecimal;
import java.util.Date;

import ru.redw4y.HomeAccounting.models.CashAccount;
import ru.redw4y.HomeAccounting.models.User;

public interface Operation {

	int getId();
	void setId(int id);

	CashAccount getCashAccount();
	void setCashAccount(CashAccount cashAccount);
	
	Date getDate();
	void setDate(Date date);

	BigDecimal getAmount();
	void setAmount(BigDecimal amount);

	Category getCategory();
	void setCategory(Category category);

	User getUser();
	void setUser(User user);

	String getComment();
	void setComment(String comment);
	
	OperationType getType();	

}