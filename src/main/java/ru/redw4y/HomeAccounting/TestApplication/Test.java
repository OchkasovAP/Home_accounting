package ru.redw4y.HomeAccounting.TestApplication;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Test {
	public static void main(String[] args) throws Throwable {
		Calendar calendar = new GregorianCalendar(2023, 10, 20);
		System.out.println(calendar.getTime());
		long dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK);
		long day = 1000*60*60*24;
		dayOfTheWeek = (dayOfTheWeek+5)%7;
		long date = calendar.getTimeInMillis();
		date -= dayOfTheWeek*day;
		System.out.println(new Date(date));
		
	}
}
