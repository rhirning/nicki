
package org.mgnl.nicki.dynamic.objects.wrapper;

/*-
 * #%L
 * nicki-dynamic-objects
 * %%
 * Copyright (C) 2017 Ralf Hirning
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.dynamic.objects.objects.LdapSearchGroup;

import lombok.extern.slf4j.Slf4j;


/**
 *
 * @author cna
 */
@Slf4j
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
	
	
	private LdapSearchGroup group;
	private final static Pattern OUTER_REGEX = Pattern.compile("\\|\\[(.*?)\\](PRECISE|TIME|DAY)?\\|");
	private final static Pattern INNER_REGEX = Pattern.compile("([+-])(\\d+)(MONTH|DAY|HOUR|MINUTE)S?");

	private String dateFormat = FORMAT.PRECISE.getValue();
	

	public LdapSearchGroupWrapper(LdapSearchGroup group) {
		this.group = group;
	}

	public void setDynamicLdapSearch(String dynLdapSearch) {
		String result = dynLdapSearch;
		Matcher match = OUTER_REGEX.matcher(result);
		
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

		if (StringUtils.isBlank(str) || (!StringUtils.startsWith(str, "+") && !StringUtils.startsWith(str, "-"))) {
			return today;
		}

		Matcher match = INNER_REGEX.matcher(str);

		if (match.matches()) {
			int modifier = 1;

			if (StringUtils.equals(match.group(1), "-")) {
				modifier = -1;
			}
			then = add(today, modifier * Integer.parseInt(match.group(2)), UNIT.fromValue(match.group(3)).getValue());
		} else {
			log.debug("PARSE EXCEPTION: " + str + ", USING NOW()");
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
