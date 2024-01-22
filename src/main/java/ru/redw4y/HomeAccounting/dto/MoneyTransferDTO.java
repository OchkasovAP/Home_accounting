package ru.redw4y.HomeAccounting.dto;

import java.math.BigDecimal;


public class MoneyTransferDTO {
	private String accountFrom;
	private String accountTo;
	private BigDecimal amount;
	
	public String getAccountFrom() {
		return accountFrom;
	}
	public void setAccountFrom(String accountFrom) {
		this.accountFrom = accountFrom;
	}
	public String getAccountTo() {
		return accountTo;
	}
	public void setAccountTo(String accountTo) {
		this.accountTo = accountTo;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
