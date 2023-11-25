package ru.redw4y.HomeAccounting.entityUtil;

import ru.redw4y.HomeAccounting.util.DateUtil;

public class OperationsFilter extends OperationModel{
	private String startDate;
	private String endDate;
	
	public OperationsFilter() {
		super();
	}

	public OperationsFilter(Operation operation) {
		super(operation);
	}

	public OperationsFilter(MainViewModel viewModel) {
		super(viewModel);
		DateRange dateRange = viewModel.getDateRange();
		startDate = DateUtil.convertDateToString(dateRange.getStartDate());
		endDate = DateUtil.convertDateToString(dateRange.getEndDate());
	}

	public DateRange getDateRange() {
		return new DateRange(startDate, endDate);
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "OperationsFilter [startDate=" + startDate + ", endDate=" + endDate + "]" + super.toString();
	}
	public Integer getVal(Integer val) {
		return val;
	}
	
	
}
