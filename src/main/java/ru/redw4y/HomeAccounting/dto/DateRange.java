package ru.redw4y.HomeAccounting.dto;

import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import ru.redw4y.HomeAccounting.util.DateUtil;
@Component
public class DateRange {
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "Поле не должно быть пустым")
	private Date startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "Поле не должно быть пустым")
	private Date endDate;
	
	public static DateRange defaultDateRange() {
		return new DateRange(new GregorianCalendar(1900,0,1).getTime(), new Date());
	}
	public DateRange() {
		
	}

	public DateRange(Date startDate, Date endDate) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public DateRange(String startDate, String endDate) {
		this.startDate = DateUtil.convertStringToDate(startDate);
		this.endDate = DateUtil.convertStringToDate(endDate);
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
