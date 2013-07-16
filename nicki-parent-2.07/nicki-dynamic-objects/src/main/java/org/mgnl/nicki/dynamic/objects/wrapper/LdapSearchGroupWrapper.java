/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.dynamic.objects.wrapper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.dynamic.objects.objects.LdapSearchGroup;


/**
 *
 * @author cna
 */
public class LdapSearchGroupWrapper {

	public static enum UNIT {

		MONTH(Calendar.MONTH),
		DAY(Calendar.DATE),
		HOUR(Calendar.HOUR),
		MINUTE(Calendar.MINUTE);
		private int value;
		private static HashMap<String, UNIT> map = new HashMap<String, UNIT>();

		static {
			map.put("MONTH", MONTH);
			map.put("DAY", DAY);
			map.put("HOUR", HOUR);
			map.put("MINUTE", MINUTE);
		}

		private UNIT(int value) {
			this.value = value;
		}

		public static UNIT fromValue(String value) {
			return map.get(StringUtils.upperCase(value));
		}

		public int getValue() {
			return value;
		}
	};
	
	public static enum FORMAT {

		PRECISE("yyyyMMddHHmmssZ"),
		TIME("yyyyMMddHHmm"),
		DAY("yyyyMMdd");
		
		private String value;
		private static HashMap<String, FORMAT> map = new HashMap<String, FORMAT>();

		static {
			map.put("PRECISE", PRECISE);
			map.put("TIME", TIME);
			map.put("DAY", DAY);
		}

		private FORMAT(String value) {
			this.value = value;
		}

		public static FORMAT fromValue(String value) {
			return map.get(StringUtils.upperCase(value));
		}

		public String getValue() {
			return value;
		}
	};
	
	
	private LdapSearchGroup group = null;
	private final static Pattern outer_regex = Pattern.compile("\\|\\[(.*?)\\](PRECISE|TIME|DAY)?\\|");
	private final static Pattern inner_regex = Pattern.compile("([+-])(\\d+)(MONTH|DAY|HOUR|MIN)S?");

	private String dateFormat = FORMAT.PRECISE.getValue();
	

	public LdapSearchGroupWrapper(LdapSearchGroup group) {
		this.group = group;
	}

	public void setDynamicLdapSearch(String dynLdapSearch) {
		String result = dynLdapSearch;
		Matcher match = outer_regex.matcher(result);
		
		if (!match.find()) {
			group.setQueryComponents(group.getSearchRoot(), group.getSearchScope(), result);
			return;
		}

		match.reset();
		
		while (match.find()) {
			Date date = getCalculatedTime(match.group(1));
			if(StringUtils.isNotBlank(match.group(2))) {
				result = StringUtils.replace(result, match.group(), getDateString(date, FORMAT.fromValue(match.group(2)).getValue()));
			} else {
				result = StringUtils.replace(result, match.group(), getDateString(date, dateFormat));
			}
		}

		group.setQueryComponents(group.getSearchRoot(), group.getSearchScope(), result);

	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/*
	 * str in the form: ([+-])(\d+)(MONTH|DAY|HOUR|MIN)S?
	 * example: -3DAY
	 * example: +4MONTH
	 *
	 * returns Date, NOW() + (str)
	 */
	private Date getCalculatedTime(String str) {
		//NOW
		Date today = new Date();
		Date then = (Date) today.clone();

		if (StringUtils.isBlank(str) || (!(StringUtils.startsWith(str, "+")) && !(StringUtils.startsWith(str, "-")))) {
			return today;
		}

		Matcher match = inner_regex.matcher(str);

		if (match.matches()) {
			int modifier = 1;

			if (StringUtils.equals(match.group(1), "-")) {
				modifier = -1;
			}
			then = add(today, modifier * Integer.parseInt(match.group(2)), UNIT.fromValue(match.group(3)).getValue());
		} else {
			System.out.println("PARSE EXCEPTION: " + str + ", USING NOW()");
		}

		return then;

	}

	private String getDateString(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	private Date add(Date date, int amount, int units) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(units, amount);

		return cal.getTime();
	}
}
