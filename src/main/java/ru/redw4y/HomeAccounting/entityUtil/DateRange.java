package ru.redw4y.HomeAccounting.entityUtil;

import java.util.Date;


import javax.servlet.http.HttpServletRequest;

import ru.redw4y.HomeAccounting.util.DateUtil;

public class DateRange {
	private Date startDate;
	private Date endDate;

	public DateRange(Date startDate, Date endDate) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public DateRange(String startDate, String endDate) {
		this.startDate = DateUtil.convertStringToDate(startDate);
		this.endDate = DateUtil.convertStringToDate(endDate);
	}

	public DateRange(HttpServletRequest request) {
		String[] dateParams = new String[2];
		dateParams[0] = request.getParameter("startDate");
		dateParams[1] = request.getParameter("endDate");
		Date[] dateRange = new Date[2];
		for (int i = 0; i < dateParams.length; i++) {
			dateRange[i] = DateUtil.convertStringToDate(dateParams[i]);
		}
		startDate = dateRange[0];
		endDate = dateRange[1];
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	@Override
	public String toString() {
		return "Период: " + DateUtil.convertDateToString(startDate) + " : " + DateUtil.convertDateToString(endDate);
	}

}
