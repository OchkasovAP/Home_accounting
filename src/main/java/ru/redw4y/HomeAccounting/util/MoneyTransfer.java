package ru.redw4y.HomeAccounting.util;

import java.math.BigDecimal;

import ru.redw4y.HomeAccounting.models.CashAccount;

public class MoneyTransfer {
	private CashAccount accountFrom;
	private CashAccount accountTo;
	private BigDecimal amount;
	
	public CashAccount getAccountFrom() {
		return accountFrom;
	}
	public void setAccountFrom(CashAccount accountFrom) {
		this.accountFrom = accountFrom;
	}
	public CashAccount getAccountTo() {
		return accountTo;
	}
	public void setAccountTo(CashAccount accountTo) {
		this.accountTo = accountTo;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	
}
