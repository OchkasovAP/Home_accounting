package ru.redw4y.HomeAccounting.entityUtil;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.redw4y.HomeAccounting.model.CashAccount;
import ru.redw4y.HomeAccounting.model.User;
import ru.redw4y.HomeAccounting.util.DateUtil;

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

	private Map<Category, double[]> categoriesSum = new HashMap<>();

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
	
	public void init(List<? extends Operation> operations) {
		calculateAmoundCategories(operations);
		categoryViewFormatting();
		createLinkParameters();
	}

	public void recalculateGeneralBalance(CashAccount cashAccount) {
		this.cashAccountName = cashAccount.getName();
		this.cashAccountID = cashAccount.getId();
		generalBalance = cashAccount.getBalance().doubleValue();
	}

	public void recalculateGeneralBalance(User user) {
		generalBalance = CashAccount.getGeneralBalance(user.getCashAccounts()).doubleValue();
	}

	public DateRange getDateRange() {
		Date convertedStartDate = DateUtil.convertStringToDate(startDate);
		Date convertedEndDate = DateUtil.convertStringToDate(endDate);
		return new DateRange(convertedStartDate, convertedEndDate);
	}

	public Double getGeneralBalance() {
		return generalBalance;
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

	private void calculateAmoundCategories(List<? extends Operation> operations) {
		for (Operation operation : operations) {
			double operationSum = operation.getAmount().doubleValue();
			amountInPeriod += operationSum;
			Category operationCategory = operation.getCategory();
			if (!categoriesSum.containsKey(operationCategory)) {
				categoriesSum.put(operationCategory, new double[2]);
			}
			categoriesSum.get(operationCategory)[0]+=operationSum;
			categoriesSum.get(operationCategory)[1]+=operationSum;
		}
	}

	private void categoryViewFormatting() {
		for (Category category : categoriesSum.keySet()) {
			categoriesSum.get(category)[0]/=amountInPeriod;
			double percent = categoriesSum.get(category)[0];
			double categoryAmount = categoriesSum.get(category)[1];;
			String categotyDescript = String.format("%-20s|%15.2f\u0025%|%15.2f", category.getName(), percent*100,
					categoryAmount);
			predentedCategories.put(category.getId(), categotyDescript);
		}
	}
	private void createLinkParameters() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("startDate="+startDate);
		stringBuilder.append("&endDate="+endDate);
		stringBuilder.append("&userID="+userID);
		if(cashAccountID!=null&&!cashAccountID.equals("")) {
			stringBuilder.append("&cashAccountID="+cashAccountID);
		}
		linkParameters = stringBuilder.toString();
	}


}
