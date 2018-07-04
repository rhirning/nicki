package org.mgnl.nicki.core.data;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 - 2018 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
		setToBeginOfDay(calendar);
		return calendar;
	}

	public static Calendar getFirstDayOfYear() {
		Calendar calendar = getTodayCalendar();
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
		Calendar calendar = getTodayCalendar();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar;
	}

	public static Calendar getLastDayOfMonth() {
		Calendar calendar = getFirstDayOfMonth();
		calendar.add(Calendar.MONTH, 1);
		return calendar;
	}
	
	public static void setToBeginOfDay(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
	}
}
