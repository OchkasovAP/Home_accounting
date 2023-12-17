package ru.redw4y.HomeAccounting.entityUtil;

import ru.redw4y.HomeAccounting.util.DateUtil;

public class OperationModel {
	private Integer id;
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
	
	public class Builder{
		private OperationModel model;
		public Builder() {
			model = new OperationModel();
		}
		public Builder id(int id) {
			model.setId(id);
			return this;
		}
		public Builder cashAccountID(int id) {
			model.setCashAccountID(id);
			return this;
		}
		public Builder categoryID(int id) {
			model.setCategoryID(id);
			return this;
		}
		public Builder userID(int id) {
			model.setUserID(id);
			return this;
		}
		public Builder date(String date) {
			model.setDate(date);
			return this;
		}
		public Builder type(String type) {
			model.setType(type);
			return this;
		}
		public Builder comment(String comment) {
			model.setComment(comment);
			return this;
		}
		public Builder amount(Double amount) {
			model.setAmount(amount);
			return this;
		}
		public OperationModel build() {
			return model;
		}
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCashAccountID() {
		return cashAccountID;
	}

	public void setCashAccountID(Integer cashAccountId) {
		this.cashAccountID = cashAccountId;
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
