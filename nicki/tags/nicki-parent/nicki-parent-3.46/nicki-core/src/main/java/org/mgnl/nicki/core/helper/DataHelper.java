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
package org.mgnl.nicki.core.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataHelper {

	private static final Logger LOG = LoggerFactory.getLogger(DataHelper.class);
	public final static String FORMAT_DAY = "yyyyMMdd";
	public final static String FORMAT_DISPLAY_DAY = "dd.MM.yyyy";
	// 20091030115321
	public final static String FORMAT_TIME = "yyyyMMddHHmmss";
	public final static String FORMAT_MILLI = "yyyyMMddHHmmssSSS";
	
	public final static String FORMAT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss Z";
	public final static String FORMAT_GERMAN_TIMESTAMP = "dd.MM.yyyy HH:mm:ss";

        
	public static SimpleDateFormat formatDay = new SimpleDateFormat(FORMAT_DAY);
	public static SimpleDateFormat formatDisplayDay = new SimpleDateFormat(FORMAT_DISPLAY_DAY);
	public static SimpleDateFormat formatTime = new SimpleDateFormat(FORMAT_TIME);
	public static SimpleDateFormat formatMilli = new SimpleDateFormat(FORMAT_MILLI);
	public static SimpleDateFormat formatTimestamp = new SimpleDateFormat(FORMAT_TIMESTAMP);
	public static SimpleDateFormat formatGermanTimestamp = new SimpleDateFormat(FORMAT_GERMAN_TIMESTAMP);

	public static int getInteger(String stringValue, int defaultValue) {
		stringValue = StringUtils.strip(stringValue);
		if (StringUtils.isNotEmpty(stringValue) && StringUtils.isNumeric(stringValue)) {
			try {
				return Integer.parseInt(stringValue);
			} catch (Exception e) {
			}
		}
		return defaultValue;
	}

	public static boolean booleanOf(String value) {

		if (StringUtils.equalsIgnoreCase(value, "J")
				|| StringUtils.equalsIgnoreCase(value, "Y")
				|| StringUtils.equalsIgnoreCase(value, "1")
				|| StringUtils.equalsIgnoreCase(value, "TRUE")
				) {
			return true;
		}
		return false;
	}
	
	public static String getValue(String[] strings) {
		if (strings == null) {
			return null;
		}
		for (int i = 0; i < strings.length; i++) {
			if (StringUtils.isNotEmpty(strings[i])) {
				return strings[i];
			}
		}
		return null;
	}

	public static boolean isEmpty(String[] strings) {
		if (strings == null) {
			return true;
		}
		for (int i = 0; i < strings.length; i++) {
			if (StringUtils.isNotEmpty(strings[i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotEmpty(String[] strings) {
		return !isEmpty(strings);
	}

	public static Map<String, String> getSimpleMap(Map<String, String[]> pMap) {
		Map<String, String> map = new HashMap<String, String>();
		for (String key : pMap.keySet()) {
			if (isNotEmpty(pMap.get(key))) {
				map.put(key, getValue(pMap.get(key)));
			}
		}
		return map;
	}

	public static String cleanup(String text) {
		return StringUtils.trim(StringUtils.remove(text, "'"));
	}

	
	public static List<String> getList(String data, String separator) {
		return new ArrayList<String>(Arrays.asList(StringUtils.split(data, separator)));
	}

	public static Map<String, String> getMap(String data, String entrySeparator, String valueSeparator) {
		if (StringUtils.isEmpty(data)) {
			return new HashMap<String, String>();
		}
		List<String> list = getList(data, entrySeparator);
		Map<String, String> map = new HashMap<String, String>();
		for (String string : list) {
			try {
				String entry[] = StringUtils.split(string, valueSeparator);
				map.put(entry[0], entry[1]);
			} catch (Exception e) {
				LOG.error("Error", e);
			}
			
		}
		return map;
	}
	
	public static String getAsString(List<String> list, String separator) {
		StringBuffer sb = new StringBuffer();
		if (list != null) {
			for (String string : list) {
				if (sb.length() > 0) {
					sb.append(separator);
				}
				sb.append(string);
			}
		}
		return sb.toString();
	}

	public static String getPassword(String string) {
		try {
			if (StringUtils.startsWith(string, "!")) {
				return new String(Base64.decodeBase64(StringUtils.substringAfter(string, "!").getBytes()));
			}
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		return string;
	}

	/**
	 * Checks if two lists have at least one common element.
	 * @param list1 separated String 
	 * @param list2 separated String
	 * @param separator Separator
	 * @return boolean
	 */
	public static boolean match(String list1, String list2, String separator) {
		if (StringUtils.isEmpty(list1) || StringUtils.isEmpty(list2)) {
			return false;
		}
		String elements1[] = StringUtils.split(list1, separator);
		String elements2[] = StringUtils.split(list2, separator);
		for (int i1 = 0; i1 < elements1.length; i1++) {
			for (int i2 = 0; i2 < elements2.length; i2++) {
				if (StringUtils.equals(elements1[i1], elements2[i2])) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getDay(Date value) {
		return formatDay.format(value);
	}

	public static String getDisplayDay(Date value) {
		return formatDisplayDay.format(value);
	}

	public static Date dateFromString(String stored) throws ParseException {
		return formatDay.parse(stored);
	}

	public static boolean contains(String[] list, String entry) {
		for (String listEntry : list) {
			if (StringUtils.equals(entry, listEntry)) {
				return true;
			}
		}
		return false;
	}

	public static String[] addToStringArray(String[] array, String value) {
		if (array == null) {
			return new String[]{value};
		}
		List<String> list = Arrays.asList(array);
		if (!list.contains(value)) {
			list.add(value);
			return list.toArray(new String[0]);
		} else return array;
	}

	public static boolean contains(Collection<String> list, String value) {
		return list != null && list.contains(value);
	}

	public static <T> Collection<T> addToList(Collection<T> list, T value) {
		if (list == null) {
			list = new ArrayList<T>();
		}
		if (!list.contains(value)) {
			list.add(value);
		}
		return list;
	}
	
	public static boolean inRange(int value, int from, int to) {
		if (value < from || value > to) {
			return false;
		} else {
			return true;
		}
	}
}
