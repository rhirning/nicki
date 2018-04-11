package org.mgnl.nicki.core.data;

import java.util.Calendar;
import java.util.Date;

public class Period {
	Calendar start;
	Calendar end;

	public Period(Calendar start, Calendar end) {
		super();
		this.start = start;
		this.end = end;
	}

	public Calendar getStart() {
		return start;
	}

	public Calendar getEnd() {
		return end;
	}

	public boolean matches(Date date) {
		return getStart().getTime().before(date) && getEnd().getTime().after(date);
	}

	public static Calendar getTodayCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	public static Calendar getFirstDayOfYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 0);
		return calendar;
	}

	public static Calendar getLastDayOfYear() {
		Calendar calendar = getFirstDayOfYear();
		calendar.add(Calendar.YEAR, 1);
		return calendar;
	}

	public static Calendar getFirstDayOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar;
	}

	public static Calendar getLastDayOfMonth() {
		Calendar calendar = getFirstDayOfMonth();
		calendar.add(Calendar.MONTH, 1);
		return calendar;
	}
}
