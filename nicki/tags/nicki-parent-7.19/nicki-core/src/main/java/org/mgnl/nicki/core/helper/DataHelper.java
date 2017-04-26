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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.JsonArray;
import javax.json.JsonString;
import javax.json.JsonValue;
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
	public final static String FORMAT_MILLI = "dd.MM.yyyy HH:mm:ss:SSS";

//	public final static String FORMAT_MILLI = "yyyyMMddHHmmssSSS";
	
	public final static String FORMAT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss Z";
	public final static String FORMAT_GERMAN_TIMESTAMP = "dd.MM.yyyy HH:mm:ss";

        

	/**
	 * Extracts an Integer from a String
	 * @param stringValue String containing the Integer
	 * @param defaultValue default value if parsing does not work
	 * @return the Integer
	 */
	public static int getInteger(String stringValue, int defaultValue) {
		stringValue = StringUtils.strip(stringValue);
		if (StringUtils.isNotEmpty(stringValue)) {
			try {
				return Integer.parseInt(stringValue);
			} catch (Exception e) {
				LOG.debug("Error parsing " + stringValue);
			}
		}
		return defaultValue;
	}

	/**
	 * Makes a boolean out of a String. The value is true if the String is: "J", "Y", "1", or "TRUE" (case insensitive)
	 * @param value String to read
	 * @return the boolean value
	 */
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
	
	/**
	 * Returns the first non blank entry of the String array.
	 * @param strings the String array
	 * @return the first non empty entry
	 */
	public static String getValue(String[] strings) {
		if (strings == null) {
			return null;
		}
		for (String entry : strings) {
			if (StringUtils.isNotBlank(entry)) {
				return entry;
			}
		}
		return null;
	}

	/**
	 * Checks if all entries of the String array are blank.
	 * @param strings the String array
	 * @return boolean
	 */
	public static boolean isEmpty(String[] strings) {
		if (strings == null) {
			return true;
		}
		for (String entry : strings) {
			if (StringUtils.isNotBlank(entry)) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Checks if not all entries of the String array are blank.
	 * @param strings the String array
	 * @return boolean
	 */
	public static boolean isNotEmpty(String[] strings) {
		return !isEmpty(strings);
	}

	/**
	 * Simplifies a Map<String, String[]> by using only the first value of the String Arrays
	 * @param pMap Map<String, String[]>
	 * @return Map<String, String>
	 */
	public static Map<String, String> getSimpleMap(Map<String, String[]> pMap) {
		Map<String, String> map = new HashMap<String, String>();
		for (String key : pMap.keySet()) {
			if (isNotEmpty(pMap.get(key))) {
				map.put(key, getValue(pMap.get(key)));
			}
		}
		return map;
	}

	/**
	 * Removes all "'" and removes control characters (char <= 32) from both ends of this String
	 * @param text  the String to be trimmed, may be null
	 * @return the trimmed string, null if null String input
	 */
	public static String cleanup(String text) {
		return StringUtils.trim(StringUtils.remove(text, "'"));
	}
	
	/**
	 * Splits a String into a String array. Other than String.split each delimiter is recognized, so that empty values are possible / allowed.
	 * <br/><br/>
	 * Input: "1|2||3|4|5" with delimiter "|" results in {"1", "2", "", "3", "4", "5"}
	 * @param data input String
	 * @param separator delimiter
	 * @return String array
	 */
	public static String[] toStringArray(String data, String separator) {
		return getList(data, separator).toArray(new String[]{});
	}
	
	/**
	 * Splits a String into a List<String>. Other than String.split each delimiter is recognized, so that empty values are possible / allowed.
	 * <br/><br/>
	 * Input: "1|2||3|4|5" with delimiter "|" results in {"1", "2", "", "3", "4", "5"}
	 * @param data input String
	 * @param separator delimiter
	 * @return List<String>
	 */
	public static List<String> getList(String dataAsString, String separator) {
		String data = dataAsString;
		List<String> values = new ArrayList<>();
		while (StringUtils.contains(data, separator)) {
			values.add(StringUtils.trimToEmpty(StringUtils.substringBefore(data, separator)));
			data = StringUtils.substringAfter(data, separator);
		}
		if (StringUtils.isNotBlank(data)) {
			values.add(StringUtils.trimToEmpty(data));
		}

		return values;
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
	
	public static String getAsString(Collection<String> list, String separator) {
		StringBuilder sb = new StringBuilder();
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
	
	public static final String PATTERN_STRING = "\\$\\{(.*)\\}";
	public static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);
	public static String translate(String text) {
		String result = text;
		
		while (result != null) {
			Matcher matcher = PATTERN.matcher(result);
			if (matcher.find()) {
				String name = matcher.group(1);
				String value = Environment.getProperty(name);
				if (StringUtils.isNotBlank(value)) {
					result = StringUtils.replace(result, "${" + name + "}", value);
					continue;
				} else {
					result = StringUtils.replace(result, "${" + name + "}", name);
				}
			} else {
				break;
			}
		}
		return result;
	}
	
	@Deprecated
	public static String translate(String text, String defaultValue) {
		String result = text;
		
		while (result != null) {
			Matcher matcher = PATTERN.matcher(result);
			if (matcher.find()) {
				String name = matcher.group(1);
				String value = Environment.getProperty(name);
				if (StringUtils.isNotBlank(value)) {
					result = StringUtils.replace(result, "${" + name + "}", value);
					continue;
				}
				result = StringUtils.replace(result, "${" + name + "}", defaultValue);
			} else {
				break;
			}
		}
		return result;
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

	// FORMAT_DAY = "yyyyMMdd"
	public static String getDay(Date value) {
		return new SimpleDateFormat(FORMAT_DAY).format(value);
	}

	public static Date dateFromString(String stored) throws ParseException {
		return new SimpleDateFormat(FORMAT_DAY).parse(stored);
	}

	// FORMAT_MILLI = "dd.MM.yyyy HH:mm:ss:SSS"
	public static String getMilli(Date value) {
		return new SimpleDateFormat(FORMAT_MILLI).format(value);
	}

	public static Date milliFromString(String stored) throws ParseException {
		return new SimpleDateFormat(FORMAT_MILLI).parse(stored);
	}

	// FORMAT_DISPLAY_DAY = "dd.MM.yyyy"
	public static String getDisplayDay(Date value) {
		return new SimpleDateFormat(FORMAT_DISPLAY_DAY).format(value);
	}

	public static Date dateFromDisplayDay(String stored) throws ParseException {
		return new SimpleDateFormat(FORMAT_DISPLAY_DAY).parse(stored);
	}
	
	// FORMAT_TIME = "yyyyMMddHHmmss"
	public static String getTime(Date value) {
		return new SimpleDateFormat(FORMAT_TIME).format(value);
	}

	public static Date timeFromString(String stored) throws ParseException {
		return new SimpleDateFormat(FORMAT_TIME).parse(stored);
	}
	
	// FORMAT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss Z"
	public static String getTimestamp(Date value) {
		return new SimpleDateFormat(FORMAT_TIMESTAMP).format(value);
	}

	public static Date timestampFromString(String stored) throws ParseException {
		return new SimpleDateFormat(FORMAT_TIMESTAMP).parse(stored);
	}
	
	// FORMAT_GERMAN_TIMESTAMP = "dd.MM.yyyy HH:mm:ss"
	public static String getGermanTimestamp(Date value) {
		return new SimpleDateFormat(FORMAT_GERMAN_TIMESTAMP).format(value);
	}

	public static Date germanTimestampFromString(String stored) throws ParseException {
		return new SimpleDateFormat(FORMAT_GERMAN_TIMESTAMP).parse(stored);
	}

	
	public static <T> boolean contains(T[] array, T entry) {
		for (T t : array) {
			if (t == entry) {
				return true;
			}
		}
		return false;
		
	}

	public static boolean contains(String[] list, String entry) {
		for (String listEntry : list) {
			if (StringUtils.equals(entry, listEntry)) {
				return true;
			}
		}
		return false;
	}

	public static boolean contains(String list, String entry) {
		if (list != null) {
			for (String listEntry : StringUtils.split(list)) {
				if (StringUtils.equals(entry, listEntry)) {
					return true;
				}
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

	public static Map<String, String> getMap(JsonArray registerInfo, String valueSeparator) {
		Map<String, String> map = new HashMap<>();
		if (registerInfo != null) {
			for (JsonValue jsonValue : registerInfo) {
				JsonString jsonString = (JsonString) jsonValue;
				String [] value = StringUtils.split(jsonString.getString(), valueSeparator);
				if (value != null && value.length == 2) {
					map.put(value[0], value[1]);
				}
			}
		}
		return map;
	}
	
	public static String normalize(String text, boolean transliterate) {
		if (text == null) {
			return null;
		} else {

			if (transliterate) {
				text = StringUtils.replace(text, "\u00c4", "AE");
				text = StringUtils.replace(text, "\u00e4", "ae");
				text = StringUtils.replace(text, "\u00d6", "OE");
				text = StringUtils.replace(text, "\u00f6", "oe");
				text = StringUtils.replace(text, "\u00dc", "UE");
				text = StringUtils.replace(text, "\u00fc", "ue");
				text = StringUtils.replace(text, "\u00df", "ss");
			} else {
				text = StringUtils.replace(text, "\u00df", "s");
			}
			text = java.text.Normalizer.normalize(text,	java.text.Normalizer.Form.NFD);
			text = text.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
			return StringUtils.lowerCase(text);
		}
	}
}
