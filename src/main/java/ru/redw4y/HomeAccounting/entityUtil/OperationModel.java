package ru.redw4y.HomeAccounting.entityUtil;

import ru.redw4y.HomeAccounting.util.DateUtil;

public class OperationModel {
	private Integer cashAccountID;
	private Integer categoryID;
	private Integer userID;
	private String date;
	private String type;
	private String comment;
	private Double amount;
	
	public OperationModel() {
		super();
	}
	
	protected OperationModel(MainViewModel viewModel) {
		this.cashAccountID = viewModel.getCashAccountID();
		this.userID = viewModel.getUserID();
		this.type = viewModel.getType();
	}
	
	public OperationModel(Operation operation) {
		userID = operation.getUser().getId();
		categoryID = operation.getCategory().getId();
		cashAccountID = operation.getCashAccount().getId();
		date = DateUtil.convertDateToString(operation.getDate());
		type = operation.getType().name().toLowerCase();
		comment = operation.getComment();
		amount = operation.getAmount().doubleValue();
	}

	public Integer getCashAccountID() {
		return cashAccountID;
	}

	public void setCashAccountID(Integer cashAccountID) {
		this.cashAccountID = cashAccountID;
	}

	public Integer getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(Integer categoryID) {
		this.categoryID = categoryID;
	}

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "OperationModel [cashAccountID=" + cashAccountID + ", categoryID=" + categoryID + ", userID=" + userID
				+ ", date=" + date + ", type=" + type + ", comment=" + comment + ", amount=" + amount + "]";
	}
	
}
