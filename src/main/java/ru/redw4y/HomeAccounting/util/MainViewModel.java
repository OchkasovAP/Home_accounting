package ru.redw4y.HomeAccounting.util;


import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainViewModel {
	private double amountInPeriod = 0;
	private double generalBalance = 0;
	private String startDate;
	private String endDate;
	private Integer cashAccountID;
	private String cashAccountName;
	private String type = "outcome";
	private Integer userID;
	private String linkParameters;
	private Map<Integer, String> predentedCategories = new HashMap<>();

	public MainViewModel() {
		super();
		Calendar today = Calendar.getInstance();
		long dayOfTheWeek = today.get(Calendar.DAY_OF_WEEK);
		long day = 1000*60*60*24;
		dayOfTheWeek = (dayOfTheWeek+5)%7;
		long lastWeek = today.getTimeInMillis();
		lastWeek -= dayOfTheWeek*day;
		startDate = DateUtil.convertDateToString(new Date(lastWeek));
		endDate = DateUtil.convertDateToString(today.getTime());
	}

	public double getAmountInPeriod() {
		return amountInPeriod;
	}

	public void setAmountInPeriod(double amountInPeriod) {
		this.amountInPeriod = amountInPeriod;
	}

	public void setPredentedCategories(Map<Integer, String> predentedCategories) {
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

	public Map<Integer, String> getPredentedCategories() {
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
