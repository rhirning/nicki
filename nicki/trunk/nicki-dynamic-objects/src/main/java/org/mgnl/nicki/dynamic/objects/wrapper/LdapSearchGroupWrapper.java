/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
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
import org.apache.commons.lang.ArrayUtils;
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
	private LdapSearchGroup group = null;
	private final static Pattern regex = Pattern.compile("([+-])(\\d+)(MONTH|DAY|HOUR|MIN)S?");
	private String LDAP_DATE_FORMAT = "yyyyMMddHHmmssZ";

	public LdapSearchGroupWrapper(LdapSearchGroup group) {
		this.group = group;
	}

	public void setDynamicLdapSearch(String dynLdapSearch) {
		String result = dynLdapSearch;
		String[] cmds = StringUtils.substringsBetween(dynLdapSearch, "|[", "]|");

		if (ArrayUtils.isEmpty(cmds)) {
			group.setQueryComponents(group.getSearchRoot(), group.getSearchScope(), result);
			return;
		}

		for (int i = 0; i < cmds.length; i++) {
			Date date = getCalculatedTime(cmds[i]);
			result = StringUtils.replace(result, "|[" + cmds[i] + "]|", getLdapDateString(date));
		}

		group.setQueryComponents(group.getSearchRoot(), group.getSearchScope(), result);

	}

	public void setDateFormat(String LDAP_DATE_FORMAT) {
		this.LDAP_DATE_FORMAT = LDAP_DATE_FORMAT;
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

		Matcher match = regex.matcher(str);

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

	private String getLdapDateString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(LDAP_DATE_FORMAT);
		return sdf.format(date);
	}

	private Date add(Date date, int amount, int units) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(units, amount);

		return cal.getTime();
	}
}
