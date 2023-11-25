package ru.redw4y.HomeAccounting.util;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
	public static Date convertStringToDate(String dateParam) {
		if (dateParam != null) {
			String[] dateParts = dateParam.split("-");
			int year = Integer.parseInt(dateParts[0]);
			int month = Integer.parseInt(dateParts[1]) - 1;
			int day = Integer.parseInt(dateParts[2]);
			return new GregorianCalendar(year, month, day).getTime();
		}
		return new GregorianCalendar().getTime();
	}
	public static String convertDateToString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date);
	}
}
