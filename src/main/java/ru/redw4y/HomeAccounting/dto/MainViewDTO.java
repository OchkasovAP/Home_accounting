package ru.redw4y.HomeAccounting.dto;


import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.redw4y.HomeAccounting.util.DateUtil;


public class MainViewDTO {
	private double amountInPeriod = 0;
	private double generalBalance = 0;
	private String startDate;
	private String endDate;
	private Integer cashAccountID;
	private String cashAccountName;
	private String type = "outcome";
	private Integer userID;
	private String linkParameters;
	private Map<String, String> predentedCategories = new HashMap<>();

	public MainViewDTO() {
		super();
	}

	public double getAmountInPeriod() {
		return amountInPeriod;
	}

	public void setAmountInPeriod(double amountInPeriod) {
		this.amountInPeriod = amountInPeriod;
	}

	public void setPredentedCategories(Map<String, String> predentedCategories) {
		this.predentedCategories = predentedCategories;
	}

	public double getGeneralBalance() {
		return generalBalance;
	}
	public void setGeneralBalance(double generalBalance) {
		this.generalBalance = generalBalance;
	}
 
	public Integer getCashAccountID() {
		return cashAccountID;
	}

	public String getStartDate() {
		return startDate;
	}

	public Map<String, String> getPredentedCategories() {
		return predentedCategories;
	}

	public String getEndDate() {
		return endDate;
	}

	public String getType() {
		return type;
	}

	public Integer getUserID() {
		return userID;
	}

	public String getCashAccountName() {
		return cashAccountName;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}


	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setCashAccountID(Integer cashAccountID) {
		this.cashAccountID = cashAccountID;
	}

	public void setCashAccountName(String cashAccountName) {
		this.cashAccountName = cashAccountName;
	}

	public String getLinkParameters() {
		return linkParameters;
	}

	public void setLinkParameters(String linkParameters) {
		this.linkParameters = linkParameters;
	}

}
