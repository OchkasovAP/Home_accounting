package ru.redw4y.HomeAccounting.entityUtil;

import java.math.BigDecimal;
import java.util.Date;

import ru.redw4y.HomeAccounting.entity.CashAccount;
import ru.redw4y.HomeAccounting.entity.User;

public interface Operation {

	Date getDate();

	Integer getId();

	CashAccount getCashAccount();

	BigDecimal getAmount();

	Category getCategory();

	OperationType getType();

	String getComment();
	
	User getUser();

	void setCashAccount(CashAccount cashAccount);

	void setComment(String comment);


	void setCategory(Category category);

	void setUser(User user);

	void setDate(Date date);

	void setAmount(BigDecimal amount);

}